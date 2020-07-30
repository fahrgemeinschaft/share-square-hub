package org.sharesquare.hub.model.item.targetsystem.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TripPrivacy {

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Mobile")
	private String mobile;

	@JsonProperty("Email")
	private String email;

	@JsonProperty("Landline")
	private String landline;

	@JsonProperty("Licenseplate")
	private String licenseplate;

	public enum Car {
		PRIVATE(0),
		PUBLIC(1),
		MEMBERS_ONLY(4);

		private int value;

		Car(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
	}

	@JsonProperty("Car")
	private int car = Car.PRIVATE.value();
}
