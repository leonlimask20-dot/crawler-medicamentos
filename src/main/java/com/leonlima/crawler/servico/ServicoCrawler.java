package com.leonlima.crawler.servico;

import com.leonlima.crawler.dto.MedicamentoDTO;
import com.leonlima.crawler.modelo.Medicamento;
import com.leonlima.crawler.repositorio.MedicamentoRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Coleta dados de medicamentos do portal público da ANVISA via JSOUP.
 *
 * O JSOUP é adequado aqui porque o portal usa server-side rendering —
 * os dados chegam no HTML da resposta HTTP, sem necessidade de executar JavaScript.
 * Para sites dinâmicos (React, Angular), seria necessário usar Selenium.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicoCrawler {

    private final MedicamentoRepositorio repositorio;

    @Value("${crawler.timeout-ms}")
    private int timeoutMs;

    @Value("${crawler.user-agent}")
    private String userAgent;

    private static final String URL_ANVISA =
        "https://consultas.anvisa.gov.br/#/medicamentos/q/?substancia=paracetamol&situacaoRegistro=A";

    @Transactional
    public MedicamentoDTO.ResultadoCrawl executar() {
        log.info("Iniciando coleta de medicamentos na ANVISA");

        List<Medicamento> coletados = new ArrayList<>();

        try {
            // Jsoup.connect() faz o GET e retorna o HTML parseado como árvore DOM
            Document pagina = Jsoup.connect(URL_ANVISA)
                    .userAgent(userAgent)
                    .timeout(timeoutMs)
                    .get();

            log.info("Página obtida — título: {}", pagina.title());

            // select() navega na árvore DOM com seletores CSS, igual ao querySelector do JavaScript
            Elements linhas = pagina.select("table tbody tr");
            log.info("Linhas encontradas: {}", linhas.size());

            for (Element linha : linhas) {
                Elements celulas = linha.select("td");
                if (celulas.size() < 4) continue;

                // element.text() retorna o texto visível, removendo as tags HTML
                String numeroRegistro = celulas.get(0).text().trim();
                String nome           = celulas.get(1).text().trim();
                String principioAtivo = celulas.get(2).text().trim();
                String laboratorio    = celulas.get(3).text().trim();
                String situacao       = celulas.size() > 4 ? celulas.get(4).text().trim() : "";

                if (nome.isEmpty()) continue;

                // Não salva se já existe — evita duplicatas em execuções repetidas
                if (repositorio.existsByNumeroRegistro(numeroRegistro)) continue;

                coletados.add(Medicamento.builder()
                        .nome(nome)
                        .principioAtivo(principioAtivo)
                        .laboratorio(laboratorio)
                        .numeroRegistro(numeroRegistro)
                        .situacao(situacao)
                        .urlOrigem(URL_ANVISA)
                        .build());
            }

            if (!coletados.isEmpty()) {
                repositorio.saveAll(coletados);
            }

            log.info("{} medicamentos salvos", coletados.size());

        } catch (IOException e) {
            log.error("Falha ao acessar a ANVISA: {}", e.getMessage());
            return MedicamentoDTO.ResultadoCrawl.builder()
                    .totalColetado(0).totalSalvo(0)
                    .fonte(URL_ANVISA).executadoEm(LocalDateTime.now())
                    .mensagem("Erro ao acessar o site: " + e.getMessage())
                    .build();
        }

        return MedicamentoDTO.ResultadoCrawl.builder()
                .totalColetado(coletados.size())
                .totalSalvo(coletados.size())
                .fonte(URL_ANVISA)
                .executadoEm(LocalDateTime.now())
                .mensagem(coletados.isEmpty()
                    ? "Nenhum medicamento novo encontrado"
                    : coletados.size() + " medicamentos coletados com sucesso")
                .build();
    }
}
