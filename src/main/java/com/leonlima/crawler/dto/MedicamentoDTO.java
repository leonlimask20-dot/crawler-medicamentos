package com.leonlima.crawler.dto;

import com.leonlima.crawler.modelo.Medicamento;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

public class MedicamentoDTO {

    @Data
    @Builder
    public static class Resposta {
        private Long id;
        private String nome;
        private String principioAtivo;
        private String laboratorio;
        private String numeroRegistro;
        private String categoria;
        private String situacao;
        private String urlOrigem;
        private LocalDateTime coletadoEm;

        public static Resposta deEntidade(Medicamento m) {
            return Resposta.builder()
                    .id(m.getId())
                    .nome(m.getNome())
                    .principioAtivo(m.getPrincipioAtivo())
                    .laboratorio(m.getLaboratorio())
                    .numeroRegistro(m.getNumeroRegistro())
                    .categoria(m.getCategoria())
                    .situacao(m.getSituacao())
                    .urlOrigem(m.getUrlOrigem())
                    .coletadoEm(m.getColetadoEm())
                    .build();
        }
    }

    @Data
    @Builder
    public static class ResultadoCrawl {
        private int totalColetado;
        private int totalSalvo;
        private String fonte;
        private LocalDateTime executadoEm;
        private String mensagem;
    }
}
