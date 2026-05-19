# ANVISA Medication Crawler

![CI](https://github.com/leonlimask20-dot/crawler-medicamentos/actions/workflows/ci.yml/badge.svg)

Web crawler that collects medication data from ANVISA's public portal using
JSOUP and Spring Boot. The data is persisted to the database and exposed via a
REST API. (ANVISA is the Brazilian health regulatory agency.)

---

## Tech stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| JSOUP | 1.17.2 |
| Spring Data JPA | 3.x |
| PostgreSQL | 15+ |
| JUnit 5 + Mockito | — |

---

## How JSOUP works

JSOUP makes a regular HTTP request and parses the returned HTML — no browser
needed. It is suitable for server-side rendered sites, where the data arrives
in the response HTML itself.

```java
// Download the page HTML
Document page = Jsoup.connect("https://site.com")
        .userAgent("Mozilla/5.0")
        .timeout(10000)
        .get();

// Navigate with CSS selectors — just like JavaScript's querySelector
Elements rows = page.select("table tbody tr");

for (Element row : rows) {
    String text = row.select("td").get(0).text();
}
```

For sites that load data via JavaScript (React, Angular), JSOUP is not enough —
in those cases Selenium is required.

---

## Architecture

```
src/main/java/com/leonlima/crawler/
├── controller/   → ControladorMedicamento (REST endpoints)
├── servico/      → ServicoCrawler (collection), ServicoMedicamento (queries)
├── repositorio/  → MedicamentoRepositorio (Spring Data JPA)
├── modelo/       → Medicamento (JPA entity)
├── dto/          → MedicamentoDTO (response and collection result)
└── excecao/      → TratadorDeExcecoes
```

---

## How to run

```sql
CREATE DATABASE crawlerdb;
```

```bash
mvn spring-boot:run
```

API available at `http://localhost:8084`.

---

## Endpoints

| Method | Route | Description |
|--------|------|-------------|
| POST | `/api/medicamentos/coletar` | Run the crawler on ANVISA |
| GET | `/api/medicamentos` | List collected medications |
| GET | `/api/medicamentos/{id}` | Get by ID |
| GET | `/api/medicamentos/busca?nome=` | Search by name |
| GET | `/api/medicamentos/busca?principioAtivo=` | Search by active ingredient |

---

## Example

```bash
# Trigger the collection
curl -X POST http://localhost:8084/api/medicamentos/coletar
```

```json
{
  "totalColetado": 25,
  "totalSalvo": 25,
  "fonte": "https://consultas.anvisa.gov.br/...",
  "executadoEm": "2025-01-01T10:00:00",
  "mensagem": "25 medications collected successfully"
}
```

```bash
# Query the data
curl "http://localhost:8084/api/medicamentos/busca?nome=paracetamol"
```

## Tests

```bash
mvn test
```

---

## 🤖 Agent Architecture

This project was built and code-reviewed using a **multi-agent
context-optimization workflow**: specialized AI agents each audit a single
slice of the codebase — crawling logic, persistence, REST layer, tests —
within a strict context budget. The approach cuts review time and token cost
while keeping full traceability of every finding.

Methodology, agent templates and the full playbook: **[leonlim3.gumroad.com](https://leonlim3.gumroad.com)**

---

## Author

**LNL**
GitHub: [@leonlimask20-dot](https://github.com/leonlimask20-dot)
Email: leonlimask@gmail.com
