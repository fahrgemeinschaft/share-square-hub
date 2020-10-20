package org.sharesquare.hub.model.item.targetsystem.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TripRequest {

	@JsonProperty("TripID")
	private String tripId;

	public enum TripType {
		offer,
		search
	}

	@JsonProperty("Triptype")
	private TripType tripType = TripType.offer;

	@JsonProperty("Enterdate")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private long enterDateEpoch;

	@JsonProperty("Startdate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private LocalDate startDate;

	@JsonProperty("Starttime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HHmm")
	private LocalTime startTime;

	public enum Currency {
		EUR,
		CHF
	}

	@JsonProperty("Currency")
	private Currency currency = Currency.EUR;

	@JsonProperty("IDuser")
	private String userId;

	@JsonProperty("Places")
	private int places = 3;

	@JsonProperty("Privacy")
	private TripPrivacy privacy;

	public enum Relevance {
		NOT_SEARCHABLE(0),
		SEARCHABLE(10);

		private int value;

		Relevance(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
	}

	@JsonProperty("Relevance")
	private int relevance = Relevance.SEARCHABLE.value();

	@JsonProperty("Reoccur")
	private TripReoccur reoccur;

	@JsonProperty("Routings")
	private List<TripRouting> routings;

	@JsonProperty("Smoker")
	private String smoker;

	private String car;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("NumberPlate")
	private String numberPlate;

	@JsonProperty("Contactlandline")
	private String contactLandline;

	@JsonProperty("Contactmail")
	private String contactMail;

	@JsonProperty("Contactmobile")
	private String contactMobile;

	@JsonProperty("Price")
	private String price;

	@JsonProperty("from_seotitle")
	private String fromSeoTitle;

	@JsonProperty("from_title")
	private String fromTitle;

	@JsonProperty("to_seotitle")
	private String toSeoTitle;

	@JsonProperty("to_title")
	private String toTitle;

	public enum PrefGender {
		woman,
		man
	}

	@JsonProperty("Prefgender")
	private PrefGender prefGender;

	private String baggage;

	private String animals;

	@JsonProperty("ExclusiveAssociations")
	private List<String> exclusiveAssociations;

	@JsonProperty("Associations")
	private List<String> associations;
}
