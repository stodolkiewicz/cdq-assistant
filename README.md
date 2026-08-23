# CDQ Chat Assistant

## Prerequisites

- JDK 25
- Docker (for pgvector and Ollama containers)
- A restcountries.com API key (free tier, 1000 req/month) — sign up at
  https://restcountries.com/sign-up

## Run

Two independent Spring Boot processes — start both, `mcp-server-countries` first (the MCP
client in `assistant-app` connects on startup and `assistant-app` may fail to start otherwise):

1. `mcp-server-countries` (port `8081`) — MCP server exposing the `get-country-info` tool,
   backed by restcountries.com. Requires a `COUNTRIES_API_KEY` env var set before starting:

   - IntelliJ: Run Configuration for the `mcp-server-countries` module → Environment
     variables → `COUNTRIES_API_KEY=your-key`
   - Shell (Linux/macOS): `export COUNTRIES_API_KEY=your-key`
   - Shell (Windows PowerShell): `$env:COUNTRIES_API_KEY="your-key"`

   ```
   ./mvnw spring-boot:run -pl mcp-server-countries
   ```

2. `assistant-app` (port `8080`) — the chat orchestrator; auto-starts pgvector and Ollama via
   `assistant-app/docker-compose.yml`, and connects to `mcp-server-countries` as an MCP client:

   ```
   ./mvnw spring-boot:run -pl assistant-app
   ```

3. Pull the required Ollama models (first run only):

   ```
   docker exec -it $(docker ps -qf ancestor=ollama/ollama) ollama pull qwen3:4b
   docker exec -it $(docker ps -qf ancestor=ollama/ollama) ollama pull nomic-embed-text
   ```

4. Chat with the model:

   ```
   curl "http://localhost:8080/api/chat?message=Hello"
   ```

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
