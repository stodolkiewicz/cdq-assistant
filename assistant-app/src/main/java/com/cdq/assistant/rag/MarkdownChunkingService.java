package com.cdq.assistant.rag;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Splits a markdown resource into chunks in two steps: {@link MarkdownDocumentReader}
 * first splits by document structure (headings become metadata, so sections aren't
 * cut mid-way), then {@link TokenTextSplitter} caps any oversized section to a
 * sensible token size. Both steps are configured via {@link ChunkingProperties}
 * (see {@code app.rag.chunking} in application.yaml). Single responsibility:
 * producing chunks, not storing them — see {@link VectorStoreService}.
 */
@Service
public class MarkdownChunkingService {

	private static final String PARENT_DOCUMENT_ID_METADATA_KEY = "parent_document_id";

	private final MarkdownDocumentReaderConfig readerConfig;
	private final TokenTextSplitter splitter;

	public MarkdownChunkingService(ChunkingProperties properties) {
		ChunkingProperties.MarkdownReader markdownReader = properties.markdownReader();
		this.readerConfig = MarkdownDocumentReaderConfig.builder()
				.withHorizontalRuleCreateDocument(markdownReader.horizontalRuleCreateDocument())
				.build();
		this.splitter = TokenTextSplitter.builder()
				.withChunkSize(properties.maxChunkSize())
				.withMinChunkSizeChars(properties.minChunkSizeChars())
				.withMinChunkLengthToEmbed(properties.minChunkLengthToEmbed())
				.withMaxNumChunks(properties.maxNumChunks())
				.build();
	}

	/**
	 * Reads the given markdown resource and splits it into chunks.
	 *
	 * @param resource markdown file to chunk; must not be null
	 */
	public List<Document> chunk(Resource resource) {
		Assert.notNull(resource, "resource must not be null");

		List<Document> sections = new MarkdownDocumentReader(resource, readerConfig).get();
		List<Document> chunks = splitter.apply(sections);
		chunks.forEach(chunk -> chunk.getMetadata().remove(PARENT_DOCUMENT_ID_METADATA_KEY));
		return chunks;
	}

}
