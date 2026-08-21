package com.cdq.assistant;

import org.springframework.boot.SpringApplication;

public class TestAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.from(AssistantApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
