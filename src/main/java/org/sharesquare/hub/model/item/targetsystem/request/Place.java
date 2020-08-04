package org.sharesquare.hub.model.item.targetsystem.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Place {

	@JsonProperty("placeID")
	private String id;

	@JsonProperty("Address")
	private String address;

	@JsonProperty("CountryName")
	private String countryName = "Deutschland";

	@JsonProperty("CountryCode")
	private String countryCode = "DE";

	@JsonProperty("Latitude")
	private double latitude;

	@JsonProperty("Longitude")
	private double longitude;

	@JsonProperty("StopPrice")
	private Integer stopPrice;
}
