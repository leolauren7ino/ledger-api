package io.github.leolauren7ino.ledgerapi.erro;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.leolauren7ino.ledgerapi.auth.CredenciaisInvalidasException;
import io.github.leolauren7ino.ledgerapi.auth.EmailJaCadastradoException;

// [estudo] Funil de erros: toda exceção lançada num controller passa por aqui antes de virar resposta.
@RestControllerAdvice
public class TratadorDeErros {

    // [estudo] "Quando este tipo de exceção acontecer, rode este método."
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail dadosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        // [estudo] Cada violação do @NotBlank, @Email ou @Size vira um par: nome do campo -> a mensagem que escrevemos.
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> erros.put(e.getField(), e.getDefaultMessage()));
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Um ou mais campos são inválidos");
        problema.setTitle("Dados inválidos");
        // [estudo] setProperty acrescenta um campo nosso ao JSON do erro.
        problema.setProperty("erros", erros);
        return problema;
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ProblemDetail emailJaCadastrado(EmailJaCadastradoException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problema.setTitle("E-mail já cadastrado");
        return problema;
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ProblemDetail credenciaisInvalidas(CredenciaisInvalidasException ex) {
        // [estudo] A mesma mensagem para e-mail inexistente e senha errada: a resposta não revela quais e-mails existem.
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problema.setTitle("Credenciais inválidas");
        return problema;
    }
}