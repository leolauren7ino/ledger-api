package io.github.leolauren7ino.ledgerapi.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import io.github.leolauren7ino.ledgerapi.usuario.Usuario;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final long expiracaoMinutos;

    public TokenService(JwtEncoder encoder,
                        @Value("${app.jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.encoder = encoder;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();
        JwtClaimsSet conteudo = JwtClaimsSet.builder()
                .subject(usuario.getEmail())
                .issuedAt(agora)
                .expiresAt(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES))
                .claim("perfil", usuario.getPerfil().name())
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(cabecalho, conteudo)).getTokenValue();
    }
}