package com.cdq.mcp.countries;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Direct HTTP access to the restcountries.com v5 API.
 */
@Slf4j
@Repository
public class RestCountriesClient {

	private final RestClient restClient;
	private final String apiKey;
	private final String fields;

	public RestCountriesClient(RestClient countriesRestClient, @Value("${app.restcountries.api-key}") String apiKey,
			@Value("${app.restcountries.fields}") String fields) {
		this.restClient = countriesRestClient;
		this.apiKey = apiKey;
		this.fields = fields;
	}

	/**
	 * Looks up countries whose common name matches (fully or partially) the given name.
	 *
	 * @param name country name to search for; must not be null or blank
	 * @return matching countries, empty if none found or the API call failed
	 */
	public List<CountryApiResponse> findByName(String name) {
		Assert.hasText(name, "name must not be null or blank");
		try {
			CountriesEnvelope envelope = restClient.get()
					.uri(uriBuilder -> uriBuilder.path("/names.common")
							.queryParam("q", name)
							.queryParam("response_fields", fields)
							.build())
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.body(CountriesEnvelope.class);
			return envelope == null || envelope.data() == null || envelope.data().objects() == null ? List.of()
					: envelope.data().objects();
		}
		catch (RestClientException e) {
			log.warn("restcountries.com lookup failed for name '{}': {}", name, e.getMessage());
			return List.of();
		}
	}

	// v5 wraps every response in a {"data": {"objects": [...], "meta": {...}}} envelope;
	// this exists purely to unwrap that, callers only ever see the CountryApiResponse list.
	private record CountriesEnvelope(Data data) {

		private record Data(List<CountryApiResponse> objects, Meta meta) {
		}

		private record Meta(long total, @JsonProperty("request_id") String requestId) {
		}

	}

}
