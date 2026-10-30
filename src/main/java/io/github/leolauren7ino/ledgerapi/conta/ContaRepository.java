package io.github.leolauren7ino.ledgerapi.conta;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    // [estudo] O nome vira consulta, como no UsuarioRepository.
    boolean existsByNumero(String numero);
}