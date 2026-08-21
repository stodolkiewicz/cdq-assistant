package com.cdq.assistant.rag;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Storage and retrieval against the configured {@link VectorStore}.
 */
@Service
public class VectorStoreService {

	private final VectorStore vectorStore;
	private final JdbcTemplate jdbcTemplate;

	public VectorStoreService(VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
		this.vectorStore = vectorStore;
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Adds the given documents to the vector store.
	 *
	 * @param documents documents to insert; must not be null or empty
	 */
	public void ingest(List<Document> documents) {
		Assert.notEmpty(documents, "documents must not be null or empty");
		vectorStore.add(documents);
	}

	/**
	 * Returns the documents most semantically similar to the given query.
	 *
	 * @param query search text; must not be null or blank
	 */
	public List<Document> search(String query) {
		Assert.hasText(query, "query must not be null or blank");
		return vectorStore.similaritySearch(SearchRequest.builder().query(query).build());
	}

	/**
	 * Checks whether the vector store has no documents in it.
	 */
	public boolean isEmpty() {
		Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Long.class);
		return count == null || count == 0;
	}

}
