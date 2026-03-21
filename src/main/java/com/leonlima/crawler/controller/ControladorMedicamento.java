package com.leonlima.crawler.controller;

import com.leonlima.crawler.dto.MedicamentoDTO;
import com.leonlima.crawler.servico.ServicoCrawler;
import com.leonlima.crawler.servico.ServicoMedicamento;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
public class ControladorMedicamento {

    private final ServicoMedicamento servicoMedicamento;
    private final ServicoCrawler servicoCrawler;

    // Dispara o crawler manualmente — em produção poderia ser agendado com @Scheduled
    @PostMapping("/coletar")
    public ResponseEntity<MedicamentoDTO.ResultadoCrawl> coletar() {
        return ResponseEntity.ok(servicoCrawler.executar());
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoDTO.Resposta>> listar() {
        return ResponseEntity.ok(servicoMedicamento.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoDTO.Resposta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoMedicamento.buscarPorId(id));
    }

    // GET /api/medicamentos/busca?nome=paracetamol
    // GET /api/medicamentos/busca?principioAtivo=dipirona
    @GetMapping("/busca")
    public ResponseEntity<List<MedicamentoDTO.Resposta>> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String principioAtivo
    ) {
        if (nome != null) return ResponseEntity.ok(servicoMedicamento.buscarPorNome(nome));
        if (principioAtivo != null) return ResponseEntity.ok(servicoMedicamento.buscarPorPrincipioAtivo(principioAtivo));
        return ResponseEntity.ok(servicoMedicamento.listarTodos());
    }
}
