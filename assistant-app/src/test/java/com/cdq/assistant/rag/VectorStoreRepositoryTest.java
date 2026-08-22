package com.cdq.assistant.rag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VectorStoreRepositoryTest {

	private static final String COUNT_QUERY = "SELECT COUNT(*) FROM vector_store";

	@Mock
	private JdbcTemplate jdbcTemplate;

	private VectorStoreRepository repository;

	@BeforeEach
	void setUp() {
		repository = new VectorStoreRepository(jdbcTemplate);
	}

	@Test
	void isEmptyReturnsTrueWhenTheTableHasNoRows() {
		when(jdbcTemplate.queryForObject(COUNT_QUERY, Long.class)).thenReturn(0L);

		assertThat(repository.isEmpty()).isTrue();
	}

	@Test
	void isEmptyReturnsTrueWhenTheCountIsNull() {
		when(jdbcTemplate.queryForObject(COUNT_QUERY, Long.class)).thenReturn(null);

		assertThat(repository.isEmpty()).isTrue();
	}

	@Test
	void isEmptyReturnsFalseWhenTheTableHasRows() {
		when(jdbcTemplate.queryForObject(COUNT_QUERY, Long.class)).thenReturn(3L);

		assertThat(repository.isEmpty()).isFalse();
	}

}
