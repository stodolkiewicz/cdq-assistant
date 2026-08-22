package com.cdq.assistant.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

	private static final String DEFAULT_CONVERSATION_ID = "1";

	private final ChatClient chatClient;

	public ChatController(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@GetMapping("/api/chat")
	public String chat(@RequestParam String message,
			@RequestHeader(name = "X-Conversation-Id", required = false) String conversationId) {
		String resolvedConversationId = conversationId != null ? conversationId : DEFAULT_CONVERSATION_ID;
		return chatClient.prompt()
				.user(message)
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, resolvedConversationId))
				.call()
				.content();
	}

}
