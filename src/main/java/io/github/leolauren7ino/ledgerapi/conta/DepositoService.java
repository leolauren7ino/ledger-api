package io.github.leolauren7ino.ledgerapi.conta;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepositoService {

    private final ContaRepository contaRepository;
    private final LancamentoRepository lancamentoRepository;

    public DepositoService(ContaRepository contaRepository, LancamentoRepository lancamentoRepository) {
        this.contaRepository = contaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    @Transactional
    public Conta depositar(Long contaId, BigDecimal valor) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException());

        conta.creditar(valor);

        lancamentoRepository.save(new Lancamento(conta.getId(), TipoLancamento.DEPOSITO, valor));

        return conta;
    }
}
