package org.sharesquare.hub.conversion;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.sharesquare.hub.model.data.EntityLocation;
import org.sharesquare.hub.model.data.EntityOffer;
import org.sharesquare.hub.model.data.preferences.EntityPreference;
import org.sharesquare.hub.model.item.targetsystem.request.Place;
import org.sharesquare.hub.model.item.targetsystem.request.TripPrivacy;
import org.sharesquare.hub.model.item.targetsystem.request.TripReoccur;
import org.sharesquare.hub.model.item.targetsystem.request.TripRequest;
import org.sharesquare.hub.model.item.targetsystem.request.TripRouting;
import org.sharesquare.model.preferences.PaxGenderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TargetSystemTripConverter {

	private static final Logger log = LoggerFactory.getLogger(TargetSystemTripConverter.class);

	@Autowired
	private ObjectMapper objectMapper;

	public TripRequest entityToApi(final EntityOffer entityOffer) {
		TripRequest trip = new TripRequest();
		long nowInEpochSecond = LocalDateTime.now(ZoneId.of(entityOffer.getStartTimezone()))
				.toInstant(ZoneOffset.UTC).getEpochSecond();
		trip.setEnterDateEpoch(nowInEpochSecond);
		trip.setStartDate(entityOffer.getStartDate());
		trip.setStartTime(entityOffer.getStartTime());
		trip.setUserId(" ");
		TripPrivacy privacy = new TripPrivacy();
		trip.setPrivacy(privacy);
		TripReoccur reoccur = new TripReoccur();
		trip.setReoccur(reoccur);
		List<TripRouting> routings = new ArrayList<>();
		TripRouting routing = new TripRouting();
		routing.setOrigin(getPlace(entityOffer.getOrigin()));
		routing.setDestination(getPlace(entityOffer.getDestination()));
		routings.add(routing);
		trip.setRoutings(routings);
		trip.setDescription(getDescription(entityOffer));
		if (entityOffer.getPreferences() != null) {
			List<EntityPreference<PaxGenderValues>> paxGenderPreferences = entityOffer.getPreferences()
					.getPaxGenderPreferences();
			if (paxGenderPreferences != null && paxGenderPreferences.size() == 1
					&& paxGenderPreferences.get(0) != null) {
				if (paxGenderPreferences.get(0).getValue() == PaxGenderValues.FEMALE) {
					trip.setPrefGender(TripRequest.PrefGender.woman);
				} else if (paxGenderPreferences.get(0).getValue() == PaxGenderValues.MALE) {
					trip.setPrefGender(TripRequest.PrefGender.man);
				}
			}
		}
		return trip;
	}

	private static int MAX_ADDRESS_LENGTH = 100; // target system mysql type varchar(100)

	private Place getPlace(final EntityLocation location) {
		Place place = new Place();
		String address = location.getName();
		if (address != null) {
			address = address.trim();
			if (address.length() > MAX_ADDRESS_LENGTH) {
				address = address.substring(0, MAX_ADDRESS_LENGTH - 3) + "...";
			}
		}
		if (address == null || address.isEmpty()) {
			address = " ";
		}
		place.setAddress(address);
		place.setLatitude(location.getLatitude());
		place.setLongitude(location.getLongitude());
		return place;
	}

	private static int MAX_DESCRIPTION_LENGTH = 65535; // target system mysql type text

	private String getDescription(final EntityOffer entityOffer) {
		String description = entityOffer.getAdditionalInfo();
		if (description != null) {
			description = description.trim();
			if (description.length() > MAX_DESCRIPTION_LENGTH) {
				description = description.substring(0, MAX_DESCRIPTION_LENGTH - 3) + "...";
			}
			if (description.isEmpty()) {
				description = null;
			}
		}
		return description;
	}

	public String apiToJSONString(final TripRequest trip) {
		try {
			return objectMapper.writeValueAsString(trip);
		} catch (JsonProcessingException e) {
			log.warn("JSON processing problem: " + e.getMessage());
		}
		return "";
	}
}
