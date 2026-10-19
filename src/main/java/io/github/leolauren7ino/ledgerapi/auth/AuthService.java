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
    private final TokenService tokenService;   // NOVO
    private final String hashFalso;            // NOVO

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {   // NOVO: terceiro parâmetro
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;                                    // NOVO
        this.hashFalso = passwordEncoder.encode("senha-que-ninguem-usa");    // NOVO
    }

    public Usuario cadastrar(CadastroRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException();
        }
        String hash = passwordEncoder.encode(request.senha());
        return usuarioRepository.save(new Usuario(email, hash, Perfil.CUSTOMER));
    }

    public String autenticar(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        String hash = (usuario != null) ? usuario.getSenhaHash() : hashFalso;
        boolean senhaConfere = passwordEncoder.matches(request.senha(), hash);
        if (usuario == null || !senhaConfere) {
            throw new CredenciaisInvalidasException();
        }
        return tokenService.gerar(usuario);
    }
}