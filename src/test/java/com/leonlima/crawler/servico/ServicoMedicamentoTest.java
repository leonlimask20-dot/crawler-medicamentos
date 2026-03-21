package com.leonlima.crawler.servico;

import com.leonlima.crawler.dto.MedicamentoDTO;
import com.leonlima.crawler.modelo.Medicamento;
import com.leonlima.crawler.repositorio.MedicamentoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServicoMedicamento — testes unitários")
class ServicoMedicamentoTest {

    @Mock
    private MedicamentoRepositorio repositorio;

    @InjectMocks
    private ServicoMedicamento servico;

    private Medicamento medicamentoExemplo;

    @BeforeEach
    void configurar() {
        medicamentoExemplo = Medicamento.builder()
                .id(1L)
                .nome("PARACETAMOL 500MG")
                .principioAtivo("PARACETAMOL")
                .laboratorio("EMS S/A")
                .numeroRegistro("1234567890")
                .situacao("Válido")
                .urlOrigem("https://consultas.anvisa.gov.br")
                .build();
    }

    @Test
    @DisplayName("Deve retornar todos os medicamentos cadastrados")
    void listarTodos_retornaTodos() {
        when(repositorio.findAll()).thenReturn(List.of(medicamentoExemplo));

        List<MedicamentoDTO.Resposta> resultado = servico.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("PARACETAMOL 500MG");
        assertThat(resultado.get(0).getPrincipioAtivo()).isEqualTo("PARACETAMOL");
    }

    @Test
    @DisplayName("Deve buscar medicamentos por nome parcial sem diferenciar maiúsculas")
    void buscarPorNome_retornaCorrespondencias() {
        when(repositorio.findByNomeContainingIgnoreCase("paracetamol"))
                .thenReturn(List.of(medicamentoExemplo));

        List<MedicamentoDTO.Resposta> resultado = servico.buscarPorNome("paracetamol");

        assertThat(resultado).hasSize(1);
        verify(repositorio).findByNomeContainingIgnoreCase("paracetamol");
    }

    @Test
    @DisplayName("Deve buscar por princípio ativo")
    void buscarPorPrincipioAtivo_retornaCorrespondencias() {
        when(repositorio.findByPrincipioAtivoContainingIgnoreCase("paracetamol"))
                .thenReturn(List.of(medicamentoExemplo));

        List<MedicamentoDTO.Resposta> resultado = servico.buscarPorPrincipioAtivo("paracetamol");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLaboratorio()).isEqualTo("EMS S/A");
    }
}
