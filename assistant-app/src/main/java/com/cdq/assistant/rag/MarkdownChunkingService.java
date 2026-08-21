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
 * sensible token size. Single responsibility: producing chunks, not storing them —
 * see {@link VectorStoreService}.
 */
@Service
public class MarkdownChunkingService {

	private final MarkdownDocumentReaderConfig readerConfig;
	private final TokenTextSplitter splitter;

	public MarkdownChunkingService() {
		this.readerConfig = MarkdownDocumentReaderConfig.defaultConfig();
		this.splitter = new TokenTextSplitter();
	}

	/**
	 * Reads the given markdown resource and splits it into chunks.
	 *
	 * @param resource markdown file to chunk; must not be null
	 */
	public List<Document> chunk(Resource resource) {
		Assert.notNull(resource, "resource must not be null");

		List<Document> sections = new MarkdownDocumentReader(resource, readerConfig).get();
		return splitter.apply(sections);
	}

}
