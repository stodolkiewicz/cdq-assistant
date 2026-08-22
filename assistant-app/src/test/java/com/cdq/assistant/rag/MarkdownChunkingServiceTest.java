package com.cdq.assistant.rag;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class MarkdownChunkingServiceTest {

	private final ChunkingProperties properties = new ChunkingProperties(
			800, 350, 5, 10000, new ChunkingProperties.MarkdownReader(false));

	private final MarkdownChunkingService service = new MarkdownChunkingService(properties);

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

	@Test
	void chunkRemovesParentDocumentIdFromMetadata() {
		List<Document> chunks = service.chunk(cdqProductInformation);

		assertThat(chunks).allSatisfy(chunk -> assertThat(chunk.getMetadata()).doesNotContainKey("parent_document_id"));
	}

	@Test
	void chunkSplitsASectionLargerThanMaxChunkSize() {
		ChunkingProperties smallChunks = new ChunkingProperties(
				10, 1, 1, 10000, new ChunkingProperties.MarkdownReader(false));
		MarkdownChunkingService smallChunkingService = new MarkdownChunkingService(smallChunks);
		Resource longSection = markdownResource("""
				## Section
				One two three four five six seven eight nine ten eleven twelve \
				thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty.
				""");

		List<Document> chunks = smallChunkingService.chunk(longSection);

		assertThat(chunks).hasSizeGreaterThan(1);
	}

	@Test
	void chunkDoesNotSplitOnHorizontalRuleWhenDisabled() {
		Resource withHorizontalRule = markdownResource("""
				## Section
				First part.

				---

				Second part.
				""");

		List<Document> chunks = service.chunk(withHorizontalRule);

		assertThat(chunks).hasSize(1);
	}

	@Test
	void chunkSplitsOnHorizontalRuleWhenEnabled() {
		ChunkingProperties horizontalRuleEnabled = new ChunkingProperties(
				800, 350, 5, 10000, new ChunkingProperties.MarkdownReader(true));
		MarkdownChunkingService horizontalRuleService = new MarkdownChunkingService(horizontalRuleEnabled);
		Resource withHorizontalRule = markdownResource("""
				## Section
				First part.

				---

				Second part.
				""");

		List<Document> chunks = horizontalRuleService.chunk(withHorizontalRule);

		assertThat(chunks).hasSizeGreaterThan(1);
	}

	private Resource markdownResource(String content) {
		return new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
	}

}
