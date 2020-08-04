package org.sharesquare.hub.model.item.targetsystem.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TripReoccur {

	public enum Option {
		YES(1);

		private int value;

		Option(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
	}

	@JsonProperty("Monday")
	private Integer monday;

	@JsonProperty("Tuesday")
	private Integer tuesday;

	@JsonProperty("Wednesday")
	private Integer wednesday;

	@JsonProperty("Thursday")
	private Integer thursday;

	@JsonProperty("Friday")
	private Integer friday;

	@JsonProperty("Saturday")
	private Integer saturday;

	@JsonProperty("Sunday")
	private Integer sunday;
}
