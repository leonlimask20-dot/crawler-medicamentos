package com.leonlima.crawler.controller;

import com.leonlima.crawler.dto.MedicamentoDTO;
import com.leonlima.crawler.servico.ServicoCrawler;
import com.leonlima.crawler.servico.ServicoMedicamento;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorMedicamento.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ControladorMedicamento - testes MockMvc")
class ControladorMedicamentoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicoMedicamento servicoMedicamento;

    @MockBean
    private ServicoCrawler servicoCrawler;

    private MedicamentoDTO.Resposta medicamento() {
        return MedicamentoDTO.Resposta.builder()
            .id(1L)
            .nome("PARACETAMOL 500MG")
            .principioAtivo("PARACETAMOL")
            .laboratorio("EMS S/A")
            .numeroRegistro("1234567890")
            .situacao("Valido")
            .urlOrigem("https://consultas.anvisa.gov.br")
            .coletadoEm(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("GET /api/medicamentos - deve listar todos")
    void listar_retornaLista() throws Exception {
        when(servicoMedicamento.listarTodos()).thenReturn(List.of(medicamento()));

        mockMvc.perform(get("/api/medicamentos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nome").value("PARACETAMOL 500MG"))
            .andExpect(jsonPath("$[0].laboratorio").value("EMS S/A"));
    }

    @Test
    @DisplayName("GET /api/medicamentos/{id} - deve retornar medicamento por id")
    void buscarPorId_existente_retornaMedicamento() throws Exception {
        when(servicoMedicamento.buscarPorId(1L)).thenReturn(medicamento());

        mockMvc.perform(get("/api/medicamentos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.principioAtivo").value("PARACETAMOL"));
    }

    @Test
    @DisplayName("GET /api/medicamentos/{id} - deve retornar 404 quando nao encontrado")
    void buscarPorId_naoExistente_retorna404() throws Exception {
        when(servicoMedicamento.buscarPorId(99L))
            .thenThrow(new EntityNotFoundException("Medicamento nao encontrado"));

        mockMvc.perform(get("/api/medicamentos/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/medicamentos/busca?nome=paracetamol - deve filtrar por nome")
    void buscar_porNome_retornaFiltrado() throws Exception {
        when(servicoMedicamento.buscarPorNome("paracetamol"))
            .thenReturn(List.of(medicamento()));

        mockMvc.perform(get("/api/medicamentos/busca").param("nome", "paracetamol"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nome").value("PARACETAMOL 500MG"));
    }

    @Test
    @DisplayName("GET /api/medicamentos/busca?principioAtivo=paracetamol - deve filtrar por principio ativo")
    void buscar_porPrincipioAtivo_retornaFiltrado() throws Exception {
        when(servicoMedicamento.buscarPorPrincipioAtivo("paracetamol"))
            .thenReturn(List.of(medicamento()));

        mockMvc.perform(get("/api/medicamentos/busca").param("principioAtivo", "paracetamol"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].principioAtivo").value("PARACETAMOL"));
    }

    @Test
    @DisplayName("GET /api/medicamentos/busca - sem parametros deve listar todos")
    void buscar_semParametros_retornaLista() throws Exception {
        when(servicoMedicamento.listarTodos()).thenReturn(List.of(medicamento()));

        mockMvc.perform(get("/api/medicamentos/busca"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nome").value("PARACETAMOL 500MG"));
    }

    @Test
    @DisplayName("POST /api/medicamentos/coletar - deve disparar o crawler")
    void coletar_retornaResultado() throws Exception {
        MedicamentoDTO.ResultadoCrawl resultado = MedicamentoDTO.ResultadoCrawl.builder()
            .totalColetado(10)
            .totalSalvo(8)
            .fonte("ANVISA")
            .executadoEm(LocalDateTime.now())
            .mensagem("Coleta concluida")
            .build();

        when(servicoCrawler.executar()).thenReturn(resultado);

        mockMvc.perform(post("/api/medicamentos/coletar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalColetado").value(10))
            .andExpect(jsonPath("$.totalSalvo").value(8));
    }
}
