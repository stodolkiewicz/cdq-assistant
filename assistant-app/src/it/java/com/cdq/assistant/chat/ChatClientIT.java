package com.cdq.assistant.chat;

import com.cdq.assistant.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ChatClientIT {

	@Autowired
	private ChatClient chatClient;

	@Test
	void chatClientRespondsToAPrompt() {
		String response = chatClient.prompt()
				.user("what is CDQ Fraud Guard?")
				.call()
				.content();

		assertThat(response).contains("payment data");
	}

}
