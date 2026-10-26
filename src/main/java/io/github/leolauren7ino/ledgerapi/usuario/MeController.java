package io.github.leolauren7ino.ledgerapi.usuario;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// [estudo] Rota GET /me: mostra quem o sistema acha que você é, lendo só o token que chegou.
@RestController
public class MeController {

    @GetMapping("/me")
    // [estudo] @AuthenticationPrincipal: "Spring, me entregue quem está logado
    // nesta chamada".
    // [estudo] Jwt é o TOKEN já validado e lido como objeto. Não é a chave, e não
    // tem a senha nem o hash.
    // [estudo] Authentication é a pessoa autenticada completa; é nele que ficam as
    // autoridades.
    public MeResponse me(@AuthenticationPrincipal Jwt jwt, Authentication autenticacao) {
        // [estudo] getAuthorities() devolve objetos GrantedAuthority (permissões), e o
        // JSON quer texto.
        // [estudo] O stream percorre cada um, o map troca o objeto pelo texto (ex.:
        // "ROLE_CUSTOMER") e o toList junta.
        // [estudo] As autoridades NÃO estão dentro do token: o Spring as monta. Sem o
        // conversor de perfil, só aparece FACTOR_BEARER.
        var autoridades = autenticacao.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        // [estudo] email vem do campo sub, que o TokenService gravou no login: lido do
        // token, sem consultar o banco.
        // [estudo] perfil é o campo nosso gravado no token. autoridades é o que o
        // Spring entendeu a partir dele.
        return new MeResponse(jwt.getSubject(), jwt.getClaimAsString("perfil"), autoridades);
    }
}