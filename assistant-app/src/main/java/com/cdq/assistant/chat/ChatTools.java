package com.cdq.assistant.chat;

import java.util.List;

import com.cdq.assistant.rag.VectorStoreService;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Exposes chunk search over the vector store as an LLM-callable tool, so the chat
 * model can retrieve relevant CDQ product information on demand.
 */
@Component
public class ChatTools {

	private final VectorStoreService vectorStoreService;

	public ChatTools(VectorStoreService vectorStoreService) {
		this.vectorStoreService = vectorStoreService;
	}

	@Tool(description = "Searches the CDQ product information knowledge base for chunks relevant to the given query.")
	public List<String> searchChunks(String query) {
		return vectorStoreService.search(query).stream()
				.map(Document::getText)
				.toList();
	}

}
