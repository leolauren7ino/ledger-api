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
    private final TokenService tokenService;
    private final String hashFalso;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.hashFalso = passwordEncoder.encode("senha-que-ninguem-usa");
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
        // [estudo] orElse(null): se o e-mail não existe, segue com usuario = null (sem lançar erro aqui).
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        // [estudo] Se o e-mail não existe, confere contra um hash falso. Assim o BCrypt roda nos dois casos
        // [estudo] e o tempo de resposta não revela quais e-mails têm conta.
        String hash = (usuario != null) ? usuario.getSenhaHash() : hashFalso;
        boolean senhaConfere = passwordEncoder.matches(request.senha(), hash);
        // [estudo] Mesma exceção (401) para e-mail inexistente e senha errada: a resposta também não revela nada.
        if (usuario == null || !senhaConfere) {
            throw new CredenciaisInvalidasException();
        }
        return tokenService.gerar(usuario);
    }
}