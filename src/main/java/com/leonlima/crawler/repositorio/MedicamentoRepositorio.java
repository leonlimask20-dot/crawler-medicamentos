package com.leonlima.crawler.repositorio;

import com.leonlima.crawler.modelo.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepositorio extends JpaRepository<Medicamento, Long> {

    List<Medicamento> findByNomeContainingIgnoreCase(String nome);

    List<Medicamento> findByPrincipioAtivoContainingIgnoreCase(String principioAtivo);

    Optional<Medicamento> findByNumeroRegistro(String numeroRegistro);

    boolean existsByNumeroRegistro(String numeroRegistro);
}
