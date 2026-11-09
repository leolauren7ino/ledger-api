package io.github.leolauren7ino.ledgerapi.conta;

import java.math.BigDecimal;

public record ContaResponse (Long id,String numero, BigDecimal saldo){

    public static ContaResponse de(Conta conta){
        return new ContaResponse(conta.getId(),conta.getNumero(),conta.getSaldo());
    }
}
