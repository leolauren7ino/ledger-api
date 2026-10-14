package io.github.leolauren7ino.ledgerapi.conta;

import java.math.BigDecimal;

public class Conta {
    private BigDecimal saldo;

    public Conta(BigDecimal saldoInicial) {
        validarCasasDecimais(saldoInicial);
        //.signum é o método de BigDecimal que olha um número e diz se o sinal dele é: negativo, zero ou positivo
        if (saldoInicial.signum() < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo");
        }
        this.saldo = saldoInicial;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void creditar(BigDecimal valor) {
        validarValor(valor);
        saldo = saldo.add(valor);
    }

    public void debitar(BigDecimal valor) {
        validarValor(valor);
        //É o método do BigDecimal que compara dois valores entre, devolvendo -1(a é menor), 0 (iguais) e 1(a é maior)
        if (valor.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente: saldo " + saldo + ", solicitado " + valor);
        }
        saldo = saldo.subtract(valor);
    }

    private static void validarValor(BigDecimal valor) {
        validarCasasDecimais(valor);
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
    }

    private static void validarCasasDecimais(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O valor não pode ser nulo");
        }
        //A scale de um número do tp BigDecimal é a quantidade de números após a vírgula
        //stripTrailingZeros() ignora os zeros por exemplo em 0.500, pois a escala é 1, o número é 0.5.
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("São permitidas no máximo 2 casas decimais");
        }
    }
}
