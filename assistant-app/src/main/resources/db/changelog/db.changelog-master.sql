--liquibase formatted sql

-- PgVectorStore schema auto-initialization is disabled
-- (spring.ai.vectorstore.pgvector.initialize-schema=false); the "vector_store"
-- table is created here instead. Dimensions/index must match the
-- spring.ai.vectorstore.pgvector.* config in application.yml.

--changeset assistant-app:0-init
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vector_store (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    content text,
    metadata json,
    embedding vector(768)
);

CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);
