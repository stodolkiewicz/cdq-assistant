package com.cdq.assistant.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the {@link ChatClientConfig}'s in-memory chat memory window.
 */
@ConfigurationProperties(prefix = "app.chat.memory")
public record ChatMemoryProperties(int maxMessages) {
}
