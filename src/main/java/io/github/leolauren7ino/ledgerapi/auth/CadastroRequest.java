package io.github.leolauren7ino.ledgerapi.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//record é uma classe enxuta só para carregar dados: o Java gera sozinho o construtor, o equals, o hashCode e os acessores. 
// Aqui os acessores se chamam email() e senha() (sem get). Ela é imutável, ideal para um DTO.
//@NotBlank, @Email, @Size são as regras do Bean Validation. 
// Elas só passam a valer quando o controller marcar o parâmetro com @Valid, no próximo passo.
//O limite de 72 existe porque o BCrypt só usa os primeiros 72 bytes da senha.
//O perfil não está aqui, de propósito. Se o cliente pudesse mandar o perfil, qualquer pessoa se cadastraria como ADMIN
//O cadastro público sempre cria CUSTOMER. 
//Uma armadilha: o record gera um toString() que imprime todos os campos, inclusive a senha. Nunca logue o objeto inteiro.
public record CadastroRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail é inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
        String senha
) { }