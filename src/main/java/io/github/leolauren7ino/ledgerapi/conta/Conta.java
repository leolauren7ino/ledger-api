package io.github.leolauren7ino.ledgerapi.conta;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// [estudo] Esta classe agora é uma linha da tabela contas, além de guardar as regras de saldo.
@Entity
@Table(name = "contas")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    // [estudo] O dono da conta. updatable = false: depois de aberta, a conta não troca de dono.
    @Column(name = "usuario_id", nullable = false, updatable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private BigDecimal saldo;

    // [estudo] Quem preenche é o banco (DEFAULT now()), como no Usuario.
    @Column(name = "criada_em", insertable = false, updatable = false)
    private OffsetDateTime criadaEm;

    protected Conta() { }   // exigido pelo JPA

    // [estudo] Construtor de uso real: abre uma conta com saldo zero para um dono.
    public Conta(String numero, Long usuarioId) {
        this.numero = numero;
        this.usuarioId = usuarioId;
        this.saldo = BigDecimal.ZERO.setScale(2);
    }

    public Conta(BigDecimal saldoInicial) {
        validarCasasDecimais(saldoInicial);
        //.signum é o método de BigDecimal que olha um número e diz se o sinal dele é: negativo, zero ou positivo
        if (saldoInicial.signum() < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo");
        }
        this.saldo = saldoInicial;
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public OffsetDateTime getCriadaEm() {
        return criadaEm;
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
        //A scale de um BigDecimal é a quantidade de casas depois da vírgula.
        //0.500 tem escala 3; o stripTrailingZeros() tira os zeros do fim e o número vira 0.5, de escala 1.
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("São permitidas no máximo 2 casas decimais");
        }
    }
}