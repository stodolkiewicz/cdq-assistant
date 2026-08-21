package com.cdq.assistant.rag;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class MarkdownChunkingServiceTest {

	private final MarkdownChunkingService service = new MarkdownChunkingService();

	private Resource cdqProductInformation;

	@BeforeEach
	void setUp() {
		cdqProductInformation = new ClassPathResource("cdq_product_information.md");
	}

	@Test
	void chunkProducesOneChunkPerSection() {
		List<Document> chunks = service.chunk(cdqProductInformation);

		assertThat(chunks).extracting(document -> document.getMetadata().get("title"))
				.containsExactly(
						"Overview",
						"The problem it solves",
						"Key features",
						"Benefits",
						"Customer experience",
						"About CDQ");
	}

	@Test
	void chunkKeepsSectionContentInASingleChunk() {
		List<Document> chunks = service.chunk(cdqProductInformation);

		Document keyFeatures = chunks.get(2);
		assertThat(keyFeatures.getText()).contains("Bank Account Verification");
		assertThat(keyFeatures.getText()).contains("Trust Score");
		assertThat(keyFeatures.getText()).contains("Seamless Integration");
	}

	@Test
	void chunkRejectsNullResource() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> service.chunk(null));
	}

}
