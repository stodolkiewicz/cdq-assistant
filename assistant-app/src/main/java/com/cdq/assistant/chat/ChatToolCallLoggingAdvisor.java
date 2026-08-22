package com.cdq.assistant.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Logs every tool call the {@code ChatClient} makes. Its order places it inside
 * {@code ToolCallingAdvisor} (order 300), so each iteration of the tool-calling loop
 * passes through here, including the {@link ToolResponseMessage}s produced by tool
 * invocations from prior iterations.
 */
@Slf4j
@Component
public class ChatToolCallLoggingAdvisor implements CallAdvisor {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 400; // after ToolCallingAdvisor (order 300)
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
		logToolCalls(chatClientRequest);
		return callAdvisorChain.nextCall(chatClientRequest);
	}

	private void logToolCalls(ChatClientRequest chatClientRequest) {
		chatClientRequest.prompt().getInstructions().stream()
				.filter(ToolResponseMessage.class::isInstance)
				.map(ToolResponseMessage.class::cast)
				.flatMap(message -> message.getResponses().stream())
				.forEach(response -> log.info("Tool call: {} -> {}", response.name(), response.responseData()));
	}

}
