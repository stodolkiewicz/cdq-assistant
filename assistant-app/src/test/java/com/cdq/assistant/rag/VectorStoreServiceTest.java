package com.cdq.assistant.rag;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VectorStoreServiceTest {

	@Mock
	private VectorStore vectorStore;

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Captor
	private ArgumentCaptor<SearchRequest> searchRequestCaptor;

	private VectorStoreService service;

	@BeforeEach
	void setUp() {
		service = new VectorStoreService(vectorStore, jdbcTemplate);
	}

	@Test
	void ingestAddsDocumentsToTheVectorStore() {
		final String DOCUMENT_CONTENT = "content";
		List<Document> documents = List.of(new Document(DOCUMENT_CONTENT));

		service.ingest(documents);

		verify(vectorStore).add(documents);
	}

	@Test
	void ingestRejectsNullDocuments() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> service.ingest(null));
		verifyNoInteractions(vectorStore);
	}

	@Test
	void ingestRejectsEmptyDocuments() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> service.ingest(List.of()));
		verifyNoInteractions(vectorStore);
	}

	@Test
	void searchDelegatesToVectorStoreWithTheGivenQuery() {
		final String QUERY = "capital of Germany";
		final String MATCH_CONTENT = "Berlin is the capital of Germany";
		Document match = new Document(MATCH_CONTENT);
		when(vectorStore.similaritySearch(searchRequestCaptor.capture())).thenReturn(List.of(match));

		List<Document> result = service.search(QUERY);

		assertThat(result).containsExactly(match);
		assertThat(searchRequestCaptor.getValue().getQuery()).isEqualTo(QUERY);
	}

	@Test
	void searchRejectsNullQuery() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> service.search(null));
		verifyNoInteractions(vectorStore);
	}

	@Test
	void searchRejectsBlankQuery() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> service.search("   "));
		verifyNoInteractions(vectorStore);
	}

	@Test
	void isEmptyReturnsTrueWhenTheTableHasNoRows() {
		when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Long.class)).thenReturn(0L);

		assertThat(service.isEmpty()).isTrue();
	}

	@Test
	void isEmptyReturnsFalseWhenTheTableHasRows() {
		when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Long.class)).thenReturn(3L);

		assertThat(service.isEmpty()).isFalse();
	}

}
