package com.cdq.assistant.rag;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Explicit configuration for {@link MarkdownChunkingService}'s two chunking steps:
 * {@code TokenTextSplitter} and {@code MarkdownDocumentReader}.
 */
@ConfigurationProperties(prefix = "app.rag.chunking")
public record ChunkingProperties(
		int maxChunkSize,
		int minChunkSizeChars,
		int minChunkLengthToEmbed,
		int maxNumChunks,
		MarkdownReader markdownReader) {

	public record MarkdownReader(boolean horizontalRuleCreateDocument) {
	}

}
