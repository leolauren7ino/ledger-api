package io.github.leolauren7ino.ledgerapi.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.leolauren7ino.ledgerapi.conta.Conta;
import io.github.leolauren7ino.ledgerapi.conta.ContaResponse;
import io.github.leolauren7ino.ledgerapi.conta.DepositoRequest;
import io.github.leolauren7ino.ledgerapi.conta.DepositoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/contas")
public class AdminContaController {

    private final DepositoService depositoService;

    public AdminContaController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    @PostMapping("/{id}/depositos")
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse depositar(@PathVariable Long id, @Valid @RequestBody DepositoRequest request) {
        Conta conta = depositoService.depositar(id, request.valor());
        return ContaResponse.de(conta);
    }
}