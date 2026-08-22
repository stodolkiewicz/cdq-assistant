package com.cdq.assistant.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the {@link ChatClient} with an in-memory {@link ChatMemory}, so the assistant
 * remembers the last few turns of a conversation. The memory is process-local and lost
 * on restart, which is fine for this task's scope.
 */
@Configuration
public class ChatClientConfig {

	@Bean
	ChatMemory chatMemory(ChatMemoryProperties chatMemoryProperties) {
		return MessageWindowChatMemory.builder()
				.chatMemoryRepository(new InMemoryChatMemoryRepository())
				.maxMessages(chatMemoryProperties.maxMessages())
				.build();
	}

	@Bean
	ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, ChatTools chatTools,
			@Value("${app.chat.memory.default-conversation-id}") String defaultConversationId) {
		return chatClientBuilder
				.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
				.defaultAdvisors(a -> a.param(ChatMemory.CONVERSATION_ID, defaultConversationId))
				.defaultTools(chatTools)
				.build();
	}

}
