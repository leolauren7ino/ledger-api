package io.github.leolauren7ino.ledgerapi.conta;

// RuntimeException (não verificada): o @Transactional do Spring faz rollback nela por padrão.
//Nao é usado Exception pois sua filha (IOException) é uma exceção verificada e commita mudanças da transação por padrão.
//As classes filhas da RuntimeException (IllegalArgumentException, NullPointerException, a sua SaldoInsuficienteException) nao sao verificadas
//e dão rollback por padrao através do @Transactional. Dar rollback: descartar as mudanças da transação antes de gravar.
public class SaldoInsuficienteException extends  RuntimeException {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
