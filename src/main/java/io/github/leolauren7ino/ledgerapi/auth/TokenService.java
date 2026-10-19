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
        // [estudo] O conteúdo do token (claims). Qualquer um consegue LER; a assinatura é que impede alterar.
        JwtClaimsSet conteudo = JwtClaimsSet.builder()
        // [estudo] sub: de quem é o token.
                .subject(usuario.getEmail())
                 // [estudo] iat: quando foi emitido.
                .issuedAt(agora)
                //exp: quando vence (agora + o prazo configurado, hoje 60 min).
                .expiresAt(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES))
                .claim("perfil", usuario.getPerfil().name())
                .build();
        // [estudo] O cabeçalho declara o algoritmo da assinatura.
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        // [estudo] O encoder assina com a chave e devolve o texto final: cabeçalho.conteúdo.assinatura.
        return encoder.encode(JwtEncoderParameters.from(cabecalho, conteudo)).getTokenValue();
    }
}