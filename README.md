# CDQ Chat Assistant

## Prerequisites

- JDK 25
- Docker (for pgvector and Ollama containers)

## Run

1. Run the app (Spring Boot auto-starts pgvector and Ollama via `assistant-app/docker-compose.yml`):

   ```
   ./mvnw spring-boot:run -pl assistant-app
   ```

2. Pull the required Ollama models (first run only):

   ```
   docker exec -it $(docker ps -qf ancestor=ollama/ollama) ollama pull qwen3:4b
   docker exec -it $(docker ps -qf ancestor=ollama/ollama) ollama pull nomic-embed-text
   ```

3. Chat with the model:

   ```
   curl "http://localhost:8080/api/chat?message=Hello"
   ```

## Tech details

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
