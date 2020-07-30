package org.sharesquare.hub.conversion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.sharesquare.hub.model.data.EntityOffer;
import org.sharesquare.hub.model.item.targetsystem.request.*;
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
		LocalDate localToday = LocalDate.now();
		trip.setEnterDateEpoch(localToday);
		trip.setStartDate(entityOffer.getStartDate());
		trip.setStartTime(entityOffer.getStartTime());
		trip.setUserId(" ");
		TripPrivacy privacy = new TripPrivacy();
		trip.setPrivacy(privacy);
		TripReoccur reoccur = new TripReoccur();
		trip.setReoccur(reoccur);
		List<TripRouting> routings = new ArrayList<>();
		TripRouting routing = new TripRouting();
		Place place = new Place();
		place.setAddress("");
		place.setLatitude(entityOffer.getOrigin().getLatitude());
		place.setLongitude(entityOffer.getOrigin().getLongitude());
		routing.setOrigin(place);
		place = new Place();
		place.setAddress("");
		place.setLatitude(entityOffer.getDestination().getLatitude());
		place.setLongitude(entityOffer.getDestination().getLongitude());
		routing.setDestination(place);
		routings.add(routing);
		trip.setRoutings(routings);
		return trip;
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
