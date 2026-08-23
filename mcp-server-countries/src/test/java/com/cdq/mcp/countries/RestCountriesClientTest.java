package com.cdq.mcp.countries;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RestCountriesClientTest {

	private static final String BASE_URL = "https://api.restcountries.com/countries/v5";
	private static final String FIELDS = "names.common,capitals.name,capitals.coordinates,region,population,currencies";
	private static final String API_KEY = "test-api-key";
	private static final String COUNTRY_NAME = "Poland";

	private static final String RESPONSE_BODY = """
			{
			  "data": {
			    "objects": [
			      {
			        "names": { "common": "Poland" },
			        "capitals": [ { "name": "Warsaw", "coordinates": { "lat": 52.23, "lng": 21.01 } } ],
			        "region": "Europe",
			        "population": 37846611,
			        "currencies": [ { "code": "PLN", "name": "Polish złoty", "symbol": "zł" } ]
			      }
			    ]
			  }
			}
			""";

	private MockRestServiceServer mockServer;
	private RestCountriesClient client;

	@BeforeEach
	void setUp() {
		RestClient.Builder restClientBuilder = RestClient.builder().baseUrl(BASE_URL);
		mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
		client = new RestCountriesClient(restClientBuilder.build(), API_KEY, FIELDS);
	}

	@Test
	void findByNameReturnsCountriesMatchingTheName() {
		mockServer
				.expect(requestTo(
						BASE_URL + "/names.common?q=" + COUNTRY_NAME + "&response_fields=" + FIELDS))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header("Authorization", "Bearer " + API_KEY))
				.andRespond(withSuccess(RESPONSE_BODY, MediaType.APPLICATION_JSON));

		List<CountryApiResponse> result = client.findByName(COUNTRY_NAME);

		assertThat(result).hasSize(1);
		CountryApiResponse poland = result.get(0);
		assertThat(poland.names().common()).isEqualTo("Poland");
		CountryApiResponse.Capital warsaw = poland.capitals().get(0);
		assertThat(warsaw.name()).isEqualTo("Warsaw");
		assertThat(warsaw.coordinates()).isEqualTo(new CountryApiResponse.Coordinates(52.23, 21.01));
		assertThat(poland.region()).isEqualTo("Europe");
		assertThat(poland.population()).isEqualTo(37846611);
		assertThat(poland.currencies()).extracting(CountryApiResponse.Currency::code).containsExactly("PLN");
	}

	@Test
	void findByNameReturnsEmptyListWhenTheApiRespondsWithUnexpectedContentType() {
		mockServer
				.expect(requestTo(
						BASE_URL + "/names.common?q=" + COUNTRY_NAME + "&response_fields=" + FIELDS))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("<html>rate limited</html>", MediaType.TEXT_HTML));

		List<CountryApiResponse> result = client.findByName(COUNTRY_NAME);

		assertThat(result).isEmpty();
	}

	@Test
	void findByNameRejectsBlankName() {
		assertThatIllegalArgumentException().isThrownBy(() -> client.findByName("   "));
	}

}
