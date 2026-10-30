package io.github.leolauren7ino.ledgerapi.conta;

public class ContaNaoEncontradaException extends RuntimeException {

    public ContaNaoEncontradaException() {
        super("Conta não encontrada");
    }
}