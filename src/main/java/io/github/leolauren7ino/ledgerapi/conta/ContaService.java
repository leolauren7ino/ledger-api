package io.github.leolauren7ino.ledgerapi.conta;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import io.github.leolauren7ino.ledgerapi.auth.CredenciaisInvalidasException;
import io.github.leolauren7ino.ledgerapi.usuario.Usuario;
import io.github.leolauren7ino.ledgerapi.usuario.UsuarioRepository;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;

    public ContaService(ContaRepository contaRepository, UsuarioRepository usuarioRepository) {
        this.contaRepository = contaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Conta abrir(String emailDoDono) {
        // 1. buscar o usuário pelo e-mail
        // 2. se não existir, lançar uma exceção
        // 3. sortear um número de 10 dígitos, repetindo enquanto já existir

        Usuario usuario = usuarioRepository.findByEmail(emailDoDono)
                .orElseThrow(() -> new CredenciaisInvalidasException());

        String numero;
        do {
            numero = String.format("%010d", ThreadLocalRandom.current().nextLong(10_000_000_000L));
        } while (contaRepository.existsByNumero(numero));
        // 4. criar a Conta com o número e o id do usuário, e gravá-la
        // 5. devolver a conta gravada
        Conta conta = new Conta(numero,usuario.getId());
        return contaRepository.save(conta);
    }
}