--liquibase formatted sql

-- PgVectorStore creates/manages its own "vector_store" table via
-- spring.ai.vectorstore.pgvector.initialize-schema=true.
-- This changelog exists so spring-boot-starter-liquibase has a valid entry
-- point to run against; add app-specific changesets here as they're needed.

--changeset assistant-app:0-init
CREATE EXTENSION IF NOT EXISTS vector;
