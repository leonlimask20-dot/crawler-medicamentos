package com.leonlima.crawler.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicamentos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column
    private String principioAtivo;

    @Column
    private String laboratorio;

    // Número de registro na ANVISA — usado como chave de idempotência no crawl
    @Column(unique = true)
    private String numeroRegistro;

    @Column
    private String categoria;

    @Column
    private String situacao;

    @Column(nullable = false)
    private String urlOrigem;

    // Momento da última coleta — útil para saber se os dados estão desatualizados
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime coletadoEm = LocalDateTime.now();
}
