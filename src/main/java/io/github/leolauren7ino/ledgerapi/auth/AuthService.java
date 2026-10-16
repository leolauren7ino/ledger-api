package io.github.leolauren7ino.ledgerapi.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.leolauren7ino.ledgerapi.usuario.Perfil;
import io.github.leolauren7ino.ledgerapi.usuario.Usuario;
import io.github.leolauren7ino.ledgerapi.usuario.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(CadastroRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException();
        }
        String hash = passwordEncoder.encode(request.senha());
        return usuarioRepository.save(new Usuario(email, hash, Perfil.CUSTOMER));
    }
}
