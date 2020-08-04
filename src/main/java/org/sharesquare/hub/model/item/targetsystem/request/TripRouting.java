package org.sharesquare.hub.model.item.targetsystem.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TripRouting {

	@JsonProperty("RoutingID")
	private String id;

	@JsonProperty("Origin")
	private Place origin;

	@JsonProperty("Destination")
	private Place destination;

	@JsonProperty("RoutingIndex")
	private int routingIndex;
}
