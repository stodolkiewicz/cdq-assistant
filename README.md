# CDQ Chat Assistant

A chat API (Ollama `qwen3:4b`) that answers questions using three knowledge sources:

- **`assistant-app`** — the chat API itself. RAG over CDQ product info, stored in pgvector.
- **`mcp-server-countries`** — our own MCP server, in this repo, wrapping restcountries.com.
- **`mcp-weather`** — a third-party MCP server (not ours), run from a Docker image, for weather data.

`assistant-app` talks to both MCP servers as an MCP client, and to pgvector + Ollama
(auto-started via Docker).

## Prerequisites

- JDK 25
- Docker (for pgvector and Ollama containers)
- A restcountries.com API key (free tier, 1000 req/month) — sign up at
  https://restcountries.com/sign-up
- A weatherapi.com API key (free tier) — sign up at https://www.weatherapi.com/signup.aspx

## Run

Do these steps in order.

**1. Set two env vars** (the API keys from Prerequisites):

```
export COUNTRIES_API_KEY=your-key
export WEATHER_API_KEY=your-key
```

IntelliJ instead: set both in each module's Run Configuration → Environment variables.

**2. Build the weather MCP Docker image** (one-time):

```
docker build -t mcp-weather-local assistant-app/mcp-weather
```

No need to run this image yourself — `assistant-app` starts it as a subprocess
(`docker run -i --rm mcp-weather-local`) via the MCP `stdio` transport each time it
connects (see `spring.ai.mcp.client.stdio.connections.weather` in `application.yml`).

**3. Start `mcp-server-countries`** (our own MCP server, port `8081`, wraps restcountries.com):

```
./mvnw spring-boot:run -pl mcp-server-countries
```

**4. Start `assistant-app`** (port `8080`, the chat API — auto-starts pgvector + Ollama):

```
./mvnw spring-boot:run -pl assistant-app
```

**5. Pull the Ollama models** (one-time):

```
docker exec -it $(docker ps -qf ancestor=ollama/ollama) ollama pull qwen3:4b
docker exec -it $(docker ps -qf ancestor=ollama/ollama) ollama pull nomic-embed-text
```

**6. Chat:**

```
curl "http://localhost:8080/api/chat?message=Hello"
```

## Tests

Run from the repo root (where `./mvnw` and the root `pom.xml` live) — the reactor build
covers both `assistant-app` and `mcp-server-countries`.

**Unit tests** (`*Test.java`, no external dependencies needed):

```
./mvnw test
```

**Integration tests** (`*IT.java`, Testcontainers + the real stack):

```
./mvnw verify
```

Needs everything from [Run](#run) in place first: `COUNTRIES_API_KEY` and
`WEATHER_API_KEY` set, the `mcp-weather-local` image built, `mcp-server-countries`
running on port `8081`, and Ollama (with both models pulled) reachable at
`localhost:11434`. Postgres/pgvector is spun up automatically via Testcontainers.

## Tech details

**Running `RestCountriesClientIT` from IntelliJ**

`COUNTRIES_API_KEY` set on the module's Spring Boot run configuration does **not** carry
over to a JUnit run configuration — IntelliJ creates a separate one automatically when you
run a test class via the gutter icon, and env vars aren't shared between run configurations.
Set it there too: Run → Edit Configurations... → `RestCountriesClientIT` → Environment
variables → `COUNTRIES_API_KEY=your-key`.

To avoid repeating this per test, set it once on the JUnit run configuration template
instead — every new test run configuration then inherits it:

1. `Run` → `Edit Configurations...`
2. In the left panel, find the **"Templates"** section (below the list of existing
   configurations — scroll down or expand it if collapsed).
3. Select `JUnit` under Templates.
4. In the **Environment variables** field, add `COUNTRIES_API_KEY=your-key`.
5. `Apply` / `OK`.

Note: the template only applies to run configurations created **after** this change. An
existing `RestCountriesClientIT` run configuration (e.g. one IntelliJ already auto-generated
the first time you ran the test via the gutter icon) won't retroactively inherit it — remove
that configuration (select it in Edit Configurations → `-`) and re-run the test via the
gutter icon so IntelliJ regenerates it from the updated template.

**maven-failsafe-plugin**

Runs `*IT.java` (under `src/it/java`) in the `integration-test`/`verify` phases,
i.e. only on `./mvnw verify`, not on a plain `./mvnw test`.

Unlike Surefire — which runs unit tests (`*Test.java` under `src/test/java`) and
fails the build immediately on a failure — Failsafe's `integration-test` goal
doesn't fail the build right away.

The separate `verify` goal checks results afterwards.

**Markdown chunking** (`MarkdownChunkingService`)

Two steps, via Spring AI:

1. `MarkdownDocumentReader` splits the file by structure — one chunk per `##`
   section, heading text goes into chunk metadata (`title`), so a section
   is never cut mid-sentence.
2. `TokenTextSplitter` runs on top, capping any oversized section to a
   sensible token size.

Reader first, splitter second — structure drives the chunk boundaries,
the token limit only kicks in for sections that are too big on their own.
