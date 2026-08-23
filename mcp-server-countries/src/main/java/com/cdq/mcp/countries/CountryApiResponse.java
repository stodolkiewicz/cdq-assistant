package com.cdq.mcp.countries;

import java.util.List;

/**
 * Shape of a single country record from the restcountries.com v5 API
 * (see https://restcountries.com/docs/countries#field-reference). Trimmed server-side to
 * exactly these fields via the {@code response_fields} query param (see
 * {@link RestCountriesClient}), so no unknown properties are expected here.
 */
public record CountryApiResponse(Names names, List<Capital> capitals, String region, long population,
		List<Currency> currencies) {

	public record Names(String common) {
	}

	public record Capital(String name, Coordinates coordinates) {
	}

	public record Coordinates(double lat, double lng) {
	}

	public record Currency(String code, String name, String symbol) {
	}

}
