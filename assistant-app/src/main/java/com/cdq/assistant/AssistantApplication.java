package com.cdq.assistant;

import java.util.List;

import com.cdq.assistant.rag.MarkdownChunkingService;
import com.cdq.assistant.rag.VectorStoreService;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssistantApplication.class, args);
	}

	/**
	 * On startup, ingests {@code cdq_product_information.md} into the vector store
	 * if it's still empty. Skips ingestion otherwise, so restarts don't re-embed
	 * the same content.
	 */
	@Bean
	ApplicationRunner ingestCdqProductInformation(VectorStoreService vectorStoreService,
			MarkdownChunkingService markdownChunkingService,
			@Value("classpath:cdq_product_information.md") Resource cdqProductInformation) {
		return args -> {
			if (vectorStoreService.isEmpty()) {
				List<Document> chunks = markdownChunkingService.chunk(cdqProductInformation);
				vectorStoreService.ingest(chunks);
			}
		};
	}

}
