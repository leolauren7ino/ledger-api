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

    // [estudo] O assinador (JwtEncoder) vem do JwtConfig, injetado pelo Spring no
    // construtor.
    private final JwtEncoder encoder;
    private final long expiracaoMinutos;

    // [estudo] @Value lê app.jwt.expiracao-minutos do application.properties (hoje
    // 60).
    public TokenService(JwtEncoder encoder,
            @Value("${app.jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.encoder = encoder;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();
        // [estudo] O conteúdo do token (claims). Qualquer um consegue LER; a assinatura
        // é que impede alterar.
        // [estudo] Não entra senha nem hash aqui: o hash fica só no banco.
        JwtClaimsSet conteudo = JwtClaimsSet.builder()
                // [estudo] sub: de quem é o token. O MeController o lê de volta com
                // getSubject().
                .subject(usuario.getEmail())
                // [estudo] iat: quando foi emitido.
                .issuedAt(agora)
                // exp: quando vence (agora + o prazo configurado, hoje 60 min).
                .expiresAt(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES))
                // [estudo] perfil: campo nosso. É uma foto do login: se o perfil mudar no
                // banco, o token antigo não muda.
                .claim("perfil", usuario.getPerfil().name())
                .build();
        // [estudo] O cabeçalho declara o algoritmo da assinatura (HS256), igual para
        // todo token.
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        // [estudo] Cabeçalho e conteúdo são só reescritos em base64url (qualquer um
        // traduz de volta).
        // [estudo] A assinatura é o valor calculado com o JWT_SECRET: prova que ninguém
        // alterou o conteúdo.
        // [estudo] Resultado: cabeçalho.conteúdo.assinatura.
        return encoder.encode(JwtEncoderParameters.from(cabecalho, conteudo)).getTokenValue();
    }
}