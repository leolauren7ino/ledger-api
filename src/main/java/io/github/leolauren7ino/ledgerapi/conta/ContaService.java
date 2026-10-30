package io.github.leolauren7ino.ledgerapi.conta;

import java.util.List;
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
        Usuario usuario = donoDoToken(emailDoDono);

        String numero;
        do {
            numero = String.format("%010d", ThreadLocalRandom.current().nextLong(10_000_000_000L));
        } while (contaRepository.existsByNumero(numero));

        Conta conta = new Conta(numero, usuario.getId());
        return contaRepository.save(conta);
    }

    public List<Conta> listar(String emailDoDono) {
        Usuario usuario = donoDoToken(emailDoDono);
        return contaRepository.findByUsuarioId(usuario.getId());
    }

    public Conta buscar(String emailDoDono, Long id) {
        Usuario usuario = donoDoToken(emailDoDono);
        return contaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ContaNaoEncontradaException());
    }

    private Usuario donoDoToken(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new CredenciaisInvalidasException());
    }
}