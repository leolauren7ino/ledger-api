package io.github.leolauren7ino.ledgerapi.conta;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    // [estudo] O nome vira consulta, como no UsuarioRepository.
    boolean existsByNumero(String numero);

    List<Conta> findByUsuarioId(Long usuarioId);

    Optional<Conta> findByIdAndUsuarioId(Long id, Long usuarioId);
}
