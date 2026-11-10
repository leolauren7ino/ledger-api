package io.github.leolauren7ino.ledgerapi.conta;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lancamentos")
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false, updatable = false)
    private Long contaId;

    @Column(name = "transferencia_id", updatable = false)
    private Long transferenciaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TipoLancamento tipo;

    @Column(nullable = false, updatable = false)
    private BigDecimal valor;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

    protected Lancamento() { }

    public Lancamento(Long contaId, TipoLancamento tipo, BigDecimal valor) {
        this.contaId = contaId;
        this.tipo = tipo;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public Long getContaId() {
        return contaId;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }
}