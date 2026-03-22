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

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicoCrawler {

    private final MedicamentoRepositorio repositorio;

    @Value("${crawler.timeout-ms}")
    private int timeoutMs;

    @Value("${crawler.user-agent}")
    private String userAgent;

    private static final String URL_BUSCA =
        "https://pt.wikipedia.org/wiki/Lista_de_medicamentos_essenciais_da_OMS";

    @Transactional
    public MedicamentoDTO.ResultadoCrawl executar() {
        log.info("Iniciando coleta de medicamentos");

        List<Medicamento> coletados = new ArrayList<>();

        try {
            Document pagina = Jsoup.connect(URL_BUSCA)
                    .userAgent(userAgent)
                    .timeout(timeoutMs)
                    .get();

            log.info("Página obtida — título: {}", pagina.title());

            Elements itens = pagina.select("table.wikitable td:first-child");
		if (itens.isEmpty()) {
   		 itens = pagina.select("div#mw-content-text li");
  		  log.info("Usando seletor de lista — itens: {}", itens.size());
			} else {
  		  log.info("Usando seletor de tabela — itens: {}", itens.size());
		}
            log.info("Itens encontrados: {}", itens.size());

            int contador = 0;
            for (Element item : itens) {
                if (contador >= 20) break;

                String nome = item.text().trim();
                if (nome.length() < 4 || nome.length() > 80) continue;
                if (nome.contains("[") || nome.contains("=")) continue;

                String chave = nome.toLowerCase().replaceAll("[^a-z0-9]", "");
                if (chave.length() > 30) chave = chave.substring(0, 30);
                if (chave.isEmpty() || repositorio.existsByNumeroRegistro(chave)) continue;

                coletados.add(Medicamento.builder()
                        .nome(nome)
                        .principioAtivo(nome.split(" ")[0])
                        .situacao("Essencial OMS")
                        .numeroRegistro(chave)
                        .urlOrigem(URL_BUSCA)
                        .build());

                contador++;
            }

            if (!coletados.isEmpty()) repositorio.saveAll(coletados);
            log.info("{} medicamentos salvos", coletados.size());

        } catch (IOException e) {
            log.error("Falha ao acessar o site: {}", e.getMessage());
            return MedicamentoDTO.ResultadoCrawl.builder()
                    .totalColetado(0).totalSalvo(0)
                    .fonte(URL_BUSCA).executadoEm(LocalDateTime.now())
                    .mensagem("Erro: " + e.getMessage())
                    .build();
        }

        return MedicamentoDTO.ResultadoCrawl.builder()
                .totalColetado(coletados.size())
                .totalSalvo(coletados.size())
                .fonte(URL_BUSCA)
                .executadoEm(LocalDateTime.now())
                .mensagem(coletados.isEmpty()
                    ? "Nenhum medicamento novo encontrado"
                    : coletados.size() + " medicamentos coletados com sucesso")
                .build();
    }
}