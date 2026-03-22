# Crawler de Medicamentos da ANVISA

Webcrawler que coleta dados de medicamentos do portal público da ANVISA com JSOUP e Spring Boot. Os dados são persistidos no banco e expostos via API REST.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| JSOUP | 1.17.2 |
| Spring Data JPA | 3.x |
| PostgreSQL | 15+ |
| JUnit 5 + Mockito | — |

---

## Como o JSOUP funciona

O JSOUP faz uma requisição HTTP normal e faz o parse do HTML retornado — sem precisar de browser. É adequado para sites com server-side rendering, onde os dados chegam no próprio HTML da resposta.

```java
// Baixa o HTML da página
Document pagina = Jsoup.connect("https://site.com")
        .userAgent("Mozilla/5.0")
        .timeout(10000)
        .get();

// Navega com seletores CSS — igual ao querySelector do JavaScript
Elements linhas = pagina.select("table tbody tr");

for (Element linha : linhas) {
    String texto = linha.select("td").get(0).text();
}
```

Para sites que carregam dados via JavaScript (React, Angular), o JSOUP não é suficiente — nesses casos é necessário usar Selenium.

---

## Arquitetura

```
src/main/java/com/leonlima/crawler/
├── controller/   → ControladorMedicamento (endpoints REST)
├── servico/      → ServicoCrawler (coleta), ServicoMedicamento (consultas)
├── repositorio/  → MedicamentoRepositorio (Spring Data JPA)
├── modelo/       → Medicamento (entidade JPA)
├── dto/          → MedicamentoDTO (resposta e resultado da coleta)
└── excecao/      → TratadorDeExcecoes
```

---

## Como executar

```sql
CREATE DATABASE crawlerdb;
```

```bash
mvn spring-boot:run
```

API disponível em `http://localhost:8084`.

---

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/medicamentos/coletar` | Executa o crawler na ANVISA |
| GET | `/api/medicamentos` | Lista medicamentos coletados |
| GET | `/api/medicamentos/{id}` | Busca por ID |
| GET | `/api/medicamentos/busca?nome=` | Busca por nome |
| GET | `/api/medicamentos/busca?principioAtivo=` | Busca por princípio ativo |

---

## Exemplo

```bash
# Dispara a coleta
curl -X POST http://localhost:8084/api/medicamentos/coletar
```

```json
{
  "totalColetado": 25,
  "totalSalvo": 25,
  "fonte": "https://consultas.anvisa.gov.br/...",
  "executadoEm": "2025-01-01T10:00:00",
  "mensagem": "25 medicamentos coletados com sucesso"
}
```

```bash
# Consulta os dados
curl "http://localhost:8084/api/medicamentos/busca?nome=paracetamol"
```

## Testes

```bash
mvn test
```

---

## Autor

**LNL**
GitHub: [@leonlimask20-dot](https://github.com/leonlimask20-dot)
Email: leonlimask@gmail.com
