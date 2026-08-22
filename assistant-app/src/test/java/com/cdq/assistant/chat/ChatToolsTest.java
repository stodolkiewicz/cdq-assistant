package com.cdq.assistant.chat;

import java.util.List;

import com.cdq.assistant.rag.VectorStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatToolsTest {

	private static final String QUERY = "what is CDQ";

	@Mock
	private VectorStoreService vectorStoreService;

	private ChatTools chatTools;

	@BeforeEach
	void setUp() {
		chatTools = new ChatTools(vectorStoreService);
	}

	@Test
	void searchChunksReturnsTheTextOfEachMatchingDocument() {
		final String FIRST_CHUNK_TEXT = "CDQ is a data quality platform";
		final String SECOND_CHUNK_TEXT = "CDQ integrates with SAP and Salesforce";
		when(vectorStoreService.search(QUERY))
				.thenReturn(List.of(new Document(FIRST_CHUNK_TEXT), new Document(SECOND_CHUNK_TEXT)));

		List<String> result = chatTools.searchChunks(QUERY);

		assertThat(result).containsExactly(FIRST_CHUNK_TEXT, SECOND_CHUNK_TEXT);
	}

	@Test
	void searchChunksReturnsEmptyListWhenNoDocumentsMatch() {
		when(vectorStoreService.search(QUERY)).thenReturn(List.of());

		List<String> result = chatTools.searchChunks(QUERY);

		assertThat(result).isEmpty();
	}

}
