package com.cdq.mcp.countries;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Hits the real restcountries.com v5 API, so it catches response-shape mismatches that
 * a mocked unit test (see {@link RestCountriesClientTest}) can't. Requires a real
 * {@code COUNTRIES_API_KEY} env var — see README.md.
 */
@SpringBootTest
class RestCountriesClientIT {

	private static final String COUNTRY_NAME = "Poland";

	@Autowired
	private RestCountriesClient restCountriesClient;

	@Test
	void findByNameReturnsRealDataForAKnownCountry() {
		List<CountryApiResponse> result = restCountriesClient.findByName(COUNTRY_NAME);

		assertThat(result).isNotEmpty();
		CountryApiResponse poland = result.get(0);
		assertThat(poland.names().common()).isEqualTo(COUNTRY_NAME);
		assertThat(poland.capitals()).isNotEmpty();
		assertThat(poland.capitals().get(0).name()).isEqualTo("Warsaw");
		assertThat(poland.region()).isEqualTo("Europe");
		assertThat(poland.population()).isGreaterThan(0);
		assertThat(poland.currencies()).extracting(CountryApiResponse.Currency::code).contains("PLN");
	}

	@Test
	void findByNameReturnsEmptyListForANonExistentCountry() {
		List<CountryApiResponse> result = restCountriesClient.findByName("Atlantis");

		assertThat(result).isEmpty();
	}

}
