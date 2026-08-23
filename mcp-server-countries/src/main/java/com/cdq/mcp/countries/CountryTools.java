package com.cdq.mcp.countries;

import java.util.List;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

/**
 * MCP tools exposing country information backed by {@link RestCountriesClient}.
 */
@Component
public class CountryTools {

	private final RestCountriesClient restCountriesClient;

	public CountryTools(RestCountriesClient restCountriesClient) {
		this.restCountriesClient = restCountriesClient;
	}

	@McpTool(name = "get-country-info",
			description = "Get information (capital, region, population, currencies) about countries matching the given name")
	public List<CountryApiResponse> getCountryInfo(
			@McpToolParam(description = "Country name to search for, full or partial", required = true) String name) {
		return restCountriesClient.findByName(name);
	}

}
