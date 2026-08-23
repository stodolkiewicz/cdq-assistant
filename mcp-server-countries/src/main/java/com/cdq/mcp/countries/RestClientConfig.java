package com.cdq.mcp.countries;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Bean
	RestClient countriesRestClient(@Value("${app.restcountries.base-url}") String baseUrl) {
		return RestClient.builder().baseUrl(baseUrl).build();
	}

}
