# Snip — URL Shortener

A clean, production-grade URL shortener built with **Java 17 + Spring Boot 3.2** (backend) and **React + TypeScript + Vite** (frontend), fully containerised with Docker.

---

## Stack

| Layer     | Technology                                          |
|-----------|-----------------------------------------------------|
| Backend   | Java 17, Spring Boot 3.2, Spring Data JPA           |
| Database  | PostgreSQL 16 (prod) / H2 file (dev)                |
| Frontend  | React 18, TypeScript, Vite, plain CSS               |
| Tests     | JUnit 5 + Mockito (backend), Vitest + RTL (frontend)|
| Container | Docker multi-stage builds, Docker Compose, Maven    |

---

## Running Locally

### Prerequisites
- Docker & Docker Compose ≥ v2
- (Optional, for running tests without Docker) JDK 17+, Maven 3.9+, Node 20+

---

### Option A — Full stack with PostgreSQL (recommended)

```bash
git clone <your-fork-url>
cd url-shortener
docker compose up --build
```

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080

---

### Option B — Lightweight dev (H2, no Postgres)

```bash
docker compose -f docker-compose.dev.yml up --build
```

Same ports, data stored in an H2 file database inside the container volume.

---

### Option C — Backend only (for API development)

```bash
cd backend
./mvnw spring-boot:run
```

Starts on port 8080. Data persisted to `./data/urlshortener.mv.db`.

---

### Option D — Frontend only (dev server with proxy)

```bash
cd frontend
npm install
npm run dev
```

Starts on http://localhost:3000. Proxies `/shorten`, `/urls`, and `/{alias}` to `http://localhost:8080`.

---

## Running Tests

### Backend (JUnit 5 + Mockito)

```bash
cd backend
./mvnw test
```

Tests include:
- **Unit tests** (`UrlShortenerServiceTest`) — alias generation, all validation branches, CRUD operations
- **Integration tests** (`UrlShortenerControllerIntegrationTest`) — full HTTP round-trips via MockMvc against an in-memory H2 database

### Frontend (Vitest + React Testing Library)

```bash
cd frontend
npm install
npm test
```

### Both at once

```bash
make test
```

---

## API Usage

Full spec in [`openapi.yaml`](./openapi.yaml). Quick examples:

### Shorten a URL (random alias)

```bash
curl -s -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"fullUrl": "https://example.com/a/very/long/path"}'
# → {"shortUrl":"http://localhost:8080/xK3mP7a"}
```

### Shorten with custom alias

```bash
curl -s -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"fullUrl": "https://example.com", "customAlias": "my-link"}'
# → {"shortUrl":"http://localhost:8080/my-link"}
```

### Follow a short URL (redirect)

```bash
curl -v http://localhost:8080/my-link
# → 302 Location: https://example.com
```

### List all shortened URLs

```bash
curl -s http://localhost:8080/urls | jq .
```

### Delete a shortened URL

```bash
curl -X DELETE http://localhost:8080/my-link
# → 204 No Content
```

### Error cases

```bash
# Invalid URL scheme
curl -s -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"fullUrl": "ftp://example.com"}'
# → 400 {"error": "URL must start with http:// or https://"}

# Duplicate alias
curl -s -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"fullUrl": "https://other.com", "customAlias": "my-link"}'
# → 400 {"error": "Alias 'my-link' is already taken"}
```

---

## Architecture Notes

### Backend

- **`ShortenedUrl`** — JPA entity; `alias` is the natural primary key (no surrogate needed)
- **`UrlShortenerService`** — all business logic: URL validation via `java.net.URI`, alias regex validation, `SecureRandom`-based alias generation with collision retry up to 10 attempts
- **`UrlShortenerController`** — thin HTTP adapter with Java records for request/response DTOs; matches OpenAPI contract exactly
- **`GlobalExceptionHandler`** — `@RestControllerAdvice` maps domain exceptions to `400`/`404`/`500` with consistent `{"error": "..."}` bodies
- **`UrlShortenerExceptions`** — domain exceptions as static inner classes, keeping the package clean

### Java 17 features used

- **Records** for `ShortenRequest`, `ShortenResponse`, `UrlEntry`, `ErrorResponse` — immutable DTOs with no boilerplate
- **Text blocks** in integration tests for inline JSON
- **`List.of()`**, `stream().toList()` — modern collections API

### Alias generation

Random aliases are 7 characters from `[a-zA-Z0-9]` using `SecureRandom`. On collision it retries up to 10 times. Custom aliases accept `[a-zA-Z0-9_-]` up to 50 characters.

### Data persistence

- **Dev / H2**: File-based H2, zero external dependencies. Schema at `./data/`
- **Prod / Postgres**: Full ACID persistence. Schema managed by Hibernate `ddl-auto: update`

---

## Makefile Shortcuts

```bash
make up            # Full stack (Postgres)
make dev           # Lightweight (H2)
make down          # Stop and remove containers
make test          # Run all tests
make test-backend  # Backend only (mvn test)
make test-frontend # Frontend only (vitest)
make logs          # Follow container logs
make clean         # Remove everything including volumes
```

---

## Assumptions & Decisions

1. **No auth** — out of scope; production would add API keys or JWT.
2. **H2 default for local dev** — eliminates needing a local Postgres for `mvn spring-boot:run`.
3. **Java records for DTOs** — clean, immutable, zero boilerplate (Java 17+).
4. **`SecureRandom` over `Random`** — better entropy for alias generation; still fast enough for this use case.
5. **`BASE_URL` env var** — makes the deployment URL configurable without code changes.
6. **CORS `*`** — fine for a demo; production would restrict to the frontend origin.

---

## Time Spent

Approximately 3–4 hours including project scaffolding, backend + tests, frontend + tests, Docker, and README.
