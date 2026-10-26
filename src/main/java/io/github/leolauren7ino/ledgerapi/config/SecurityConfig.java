package io.github.leolauren7ino.ledgerapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

// [estudo] Regras de quem entra onde. O Spring lê esta classe ao iniciar e guarda os @Bean.
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // [estudo] CSRF desligado porque a API é stateless e autentica por token no cabeçalho, não por cookie.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/auth/**", "/error").permitAll()
                // [estudo] A ordem importa: /admin/** vem antes do anyRequest(), senão nunca seria avaliada.
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            // [estudo] Liga a leitura do Authorization: Bearer. O JwtDecoder (do JwtConfig) confere o token.
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(conversorDePerfil())));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private JwtAuthenticationConverter conversorDePerfil() {
        JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
        // [estudo] Lê o campo "perfil" do token (o TokenService o grava) e o transforma em autoridade.
        autoridades.setAuthoritiesClaimName("perfil");
        // [estudo] hasRole("ADMIN") procura a autoridade ROLE_ADMIN, por isso o prefixo.
        autoridades.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(autoridades);
        return conversor;
    }
}