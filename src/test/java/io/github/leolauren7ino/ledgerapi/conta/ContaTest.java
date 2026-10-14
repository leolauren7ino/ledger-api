package io.github.leolauren7ino.ledgerapi.conta;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ContaTest {
    // Compara com compareTo: assertEquals em BigDecimal compararia a escala também.
    private static void assertSaldo(String esperado, Conta conta) {
        //assertEquals passa o valor esperado e o compara com o valor final da conta pra ver se bate com o esperadp
        //se nao é o esperado, ele lança uma mensagem de exceção
        assertEquals(0, new BigDecimal(esperado).compareTo(conta.getSaldo()),
                "Saldo esperado " + esperado + " mas foi " + conta.getSaldo());
    }

    @Test
    void creditarAumentaSaldo() {
        Conta conta = new Conta(new BigDecimal("100.00"));
        conta.creditar(new BigDecimal("50.00"));
        assertSaldo("150.00", conta);
    }

    @Test
    void debitarDiminuiSaldo() {
        Conta conta = new Conta(new BigDecimal("100.00"));
        conta.debitar(new BigDecimal("30.00"));
        assertSaldo("70.00", conta);
    }

    @Test
    void debitarAcimaDoSaldoLancaExcecaoEMantemSaldo() {
        Conta conta = new Conta(new BigDecimal("100.00"));
        //afirma que o código vai lançar uma exceção daquele tipo e o teste passa quando isso acontece.
        assertThrows(SaldoInsuficienteException.class, () -> conta.debitar(new BigDecimal("100.01")));
        assertSaldo("100.00", conta);
    }

    @Test
    void valorZeroEhRejeitado() {
        Conta conta = new Conta(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> conta.creditar(BigDecimal.ZERO));
    }

    @Test
    void valorNegativoEhRejeitado() {
        Conta conta = new Conta(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> conta.creditar(new BigDecimal("-1.00")));
    }

    @Test
    void valorComTresCasasDecimaisEhRejeitado() {
        Conta conta = new Conta(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> conta.creditar(new BigDecimal("1.001")));
    }

    @Test
    void equalsDoBigDecimalComparaEscalaMasCompareToNao() {
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");
        // equals() compara valor E escala (1 casa contra 2); compareTo() compara só o valor numérico.
        assertFalse(a.equals(b));
        assertEquals(0, a.compareTo(b));
    }
}
