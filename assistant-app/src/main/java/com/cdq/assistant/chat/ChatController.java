package com.cdq.assistant.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

	private final ChatClient chatClient;

	public ChatController(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@GetMapping("/api/chat")
	public String chat(@RequestParam String message,
			@RequestHeader(name = "X-Conversation-Id",
					defaultValue = "${app.chat.memory.default-conversation-id}") String conversationId) {
		return chatClient.prompt()
				.user(message)
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
				.call()
				.content();
	}

}
