package com.cdq.assistant.rag;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Direct JDBC access to the vector store table, for queries not covered by the
 * {@link org.springframework.ai.vectorstore.VectorStore} abstraction.
 */
@Repository
public class VectorStoreRepository {

	private static final String COUNT_QUERY = "SELECT COUNT(*) FROM vector_store";

	private final JdbcTemplate jdbcTemplate;

	public VectorStoreRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Checks whether the vector store table has no rows.
	 */
	public boolean isEmpty() {
		Long count = jdbcTemplate.queryForObject(COUNT_QUERY, Long.class);
		return count == null || count == 0;
	}

}
