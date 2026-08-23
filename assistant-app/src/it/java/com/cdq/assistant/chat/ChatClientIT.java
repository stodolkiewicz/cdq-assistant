package com.cdq.assistant.chat;

import com.cdq.assistant.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the real chat stack end-to-end (Ollama qwen3:4b, pgvector RAG, the countries
 * MCP server, and the weather MCP server) against the task.md example questions. Requires
 * {@code COUNTRIES_API_KEY} and {@code WEATHER_API_KEY} set, and the {@code mcp-weather-local}
 * Docker image built — see README.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ChatClientIT {

	private static final String MUNICH_TEMPERATURE_QUESTION = "What is the temperature currently in Munich?";

	private static final String CAPITAL_OF_GERMANY_TEMPERATURE_QUESTION =
			"What is the temperature of the capital of Germany currently?";

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

	@Test
	void answersTheCapitalOfGermany() {
		String response = chatClient.prompt()
				.user("What is the capital city of Germany?")
				.call()
				.content();

		assertThat(response).containsIgnoringCase("Berlin");
	}

	@Test
	void answersWhatItKnowsAboutBerlin() {
		String response = chatClient.prompt()
				.user("What do you know about Berlin?")
				.call()
				.content();

		assertThat(response).containsIgnoringCase("Germany");
	}

	@Test
	void answersACustomQuestionUsingTheCountriesMcpTool() {
		String response = chatClient.prompt()
				.user("What currency does Poland use?")
				.call()
				.content();

		assertThat(response).containsIgnoringCase("PLN");
	}

	@Test
	void answersTemperatureInMunich() {
		String response = chatClient.prompt()
				.user(MUNICH_TEMPERATURE_QUESTION)
				.call()
				.content();

		assertThat(response).containsIgnoringCase("Munich").containsPattern("\\d");
	}

	@Test
	void answersTemperatureOfTheCapitalOfGermany() {
		String response = chatClient.prompt()
				.user(CAPITAL_OF_GERMANY_TEMPERATURE_QUESTION)
				.call()
				.content();

		assertThat(response).containsIgnoringCase("Berlin").containsPattern("\\d");
	}

}
