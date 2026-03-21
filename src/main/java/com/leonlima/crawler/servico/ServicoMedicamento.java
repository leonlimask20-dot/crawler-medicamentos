package com.leonlima.crawler.servico;

import com.leonlima.crawler.dto.MedicamentoDTO;
import com.leonlima.crawler.repositorio.MedicamentoRepositorio;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoMedicamento {

    private final MedicamentoRepositorio repositorio;

    public List<MedicamentoDTO.Resposta> listarTodos() {
        return repositorio.findAll().stream()
                .map(MedicamentoDTO.Resposta::deEntidade)
                .toList();
    }

    public MedicamentoDTO.Resposta buscarPorId(Long id) {
        return repositorio.findById(id)
                .map(MedicamentoDTO.Resposta::deEntidade)
                .orElseThrow(() -> new EntityNotFoundException("Medicamento não encontrado: " + id));
    }

    public List<MedicamentoDTO.Resposta> buscarPorNome(String nome) {
        return repositorio.findByNomeContainingIgnoreCase(nome).stream()
                .map(MedicamentoDTO.Resposta::deEntidade)
                .toList();
    }

    public List<MedicamentoDTO.Resposta> buscarPorPrincipioAtivo(String principioAtivo) {
        return repositorio.findByPrincipioAtivoContainingIgnoreCase(principioAtivo).stream()
                .map(MedicamentoDTO.Resposta::deEntidade)
                .toList();
    }
}
