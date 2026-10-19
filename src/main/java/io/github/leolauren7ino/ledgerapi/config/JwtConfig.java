package io.github.leolauren7ino.ledgerapi.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

// [estudo] Classe de configuração: o Spring a lê ao iniciar e guarda os objetos dos métodos @Bean.
@Configuration
public class JwtConfig {

    // [estudo] A chave que assina E confere os tokens. É a mesma para os dois lados.
    private final SecretKey chave;

    // [estudo] @Value lê app.jwt.secret do application.properties, que lê a variável JWT_SECRET do ambiente.
    public JwtConfig(@Value("${app.jwt.secret}") String segredo) {
        // [estudo] Texto -> bytes (UTF-8 declarado) -> chave para o algoritmo HMAC-SHA256 (o HS256).
        this.chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    // [estudo] O conferidor: refaz a conta da assinatura e checa a validade. Só aceita HS256.
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
    }

    // [estudo] O assinador: o TokenService o usa no login para fabricar o token.
    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chave));
    }
}