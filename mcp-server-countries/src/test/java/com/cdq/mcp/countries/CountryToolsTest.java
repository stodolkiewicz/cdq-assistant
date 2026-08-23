package com.cdq.mcp.countries;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryToolsTest {

	private static final String COUNTRY_NAME = "Poland";

	@Mock
	private RestCountriesClient restCountriesClient;

	private CountryTools countryTools;

	@BeforeEach
	void setUp() {
		countryTools = new CountryTools(restCountriesClient);
	}

	@Test
	void getCountryInfoReturnsEmptyListWhenNoCountryMatches() {
		when(restCountriesClient.findByName(COUNTRY_NAME)).thenReturn(List.of());

		List<CountryApiResponse> result = countryTools.getCountryInfo(COUNTRY_NAME);

		assertThat(result).isEmpty();
	}

}
