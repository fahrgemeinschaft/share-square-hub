package org.sharesquare.hub.conversion;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.sharesquare.hub.model.data.EntityContactOption;
import org.sharesquare.hub.model.data.EntityLocation;
import org.sharesquare.hub.model.data.EntityOffer;
import org.sharesquare.hub.model.data.EntityPreferences;
import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.sharesquare.hub.model.data.preferences.EntityBooleanPreference;
import org.sharesquare.hub.model.data.preferences.EntityDoublePreference;
import org.sharesquare.hub.model.data.preferences.EntityIntegerPreference;
import org.sharesquare.hub.model.data.preferences.EntityPaxGenderPreference;
import org.sharesquare.hub.model.data.preferences.EntityPaxPetsPreference;
import org.sharesquare.hub.model.data.preferences.EntityPaxSmokerPreference;
import org.sharesquare.hub.model.data.preferences.EntityPreference;
import org.sharesquare.hub.model.data.preferences.EntityStringPreference;
import org.sharesquare.model.ContactOption;
import org.sharesquare.model.Location;
import org.sharesquare.model.Offer;
import org.sharesquare.model.Preferences;
import org.sharesquare.model.preferences.BooleanPreference;
import org.sharesquare.model.preferences.DoublePreference;
import org.sharesquare.model.preferences.IntegerPreference;
import org.sharesquare.model.preferences.PaxGenderPreference;
import org.sharesquare.model.preferences.PaxGenderValues;
import org.sharesquare.model.preferences.PaxPetsPreference;
import org.sharesquare.model.preferences.PaxPetsValues;
import org.sharesquare.model.preferences.PaxSmokerPreference;
import org.sharesquare.model.preferences.PaxSmokerValues;
import org.sharesquare.model.preferences.Preference;
import org.sharesquare.model.preferences.StringPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OfferConverter {

	private static final Logger log = LoggerFactory.getLogger(OfferConverter.class);

	@Autowired
	private ObjectMapper objectMapper;

	public EntityOffer apiToEntity(Offer offer) {
		EntityOffer entityOffer = new EntityOffer();
		entityOffer.setId(offer.getId());
		entityOffer.setUserId(offer.getUserId());
		entityOffer.setStartDate(offer.getStartDate());
		entityOffer.setStartTime(offer.getStartTime());
		entityOffer.setStartTimezone(offer.getStartTimezone().getId());
		entityOffer.setOrigin(
				apiToEntity(offer.getOrigin()));
		entityOffer.setDestination(
				apiToEntity(offer.getDestination()));
		if (offer.getContactOptions() != null) {
			List<EntityContactOption> entityContactOptions = new ArrayList<>();
			for (ContactOption contactOption : offer.getContactOptions()) {
				if (contactOption != null) {
					entityContactOptions.add(
							apiToEntity(contactOption));
				}
			}
			entityOffer.setContactOptions(entityContactOptions);
		}
		if (offer.getTargetSystemIds() != null) {
			removeDuplicates(offer.getTargetSystemIds());
			List<EntityTargetSystem> entityTargetSystems = new ArrayList<>();
			EntityTargetSystem entityTargetSystem;
			for (UUID targetSystemId : offer.getTargetSystemIds()) {
				if (targetSystemId != null) {
					entityTargetSystem = new EntityTargetSystem();
					entityTargetSystem.setId(targetSystemId);
					entityTargetSystems.add(entityTargetSystem);
				}
			}
			entityOffer.setTargetSystems(entityTargetSystems);
		}
		if (offer.getPreferences() != null) {
			Preferences preferences = offer.getPreferences();
			EntityPreferences entityPreferences = new EntityPreferences();
			entityPreferences.setId(preferences.getId());
			if (preferences.getBooleanPreferences() != null) {
				List<EntityPreference<Boolean>> entityPreferenceItems = new ArrayList<>();
				for (BooleanPreference preference : preferences.getBooleanPreferences()) {
					if (preference != null) {
						entityPreferenceItems.add(new EntityBooleanPreference(preference));
					}
				}
				entityPreferences.setBooleanPreferences(entityPreferenceItems);
			}
			if (preferences.getDoublePreferences() != null) {
				List<EntityPreference<Double>> entityPreferenceItems = new ArrayList<>();
				for (DoublePreference preference : preferences.getDoublePreferences()) {
					if (preference != null) {
						entityPreferenceItems.add(new EntityDoublePreference(preference));
					}
				}
				entityPreferences.setDoublePreferences(entityPreferenceItems);
			}
			if (preferences.getIntegerPreferences() != null) {
				List<EntityPreference<Integer>> entityPreferenceItems = new ArrayList<>();
				for (IntegerPreference preference : preferences.getIntegerPreferences()) {
					if (preference != null) {
						entityPreferenceItems.add(new EntityIntegerPreference(preference));
					}
				}
				entityPreferences.setIntegerPreferences(entityPreferenceItems);
			}
			if (preferences.getPaxGenderPreferences() != null) {
				List<EntityPreference<PaxGenderValues>> entityPreferenceItems = new ArrayList<>();
				for (PaxGenderPreference preference : preferences.getPaxGenderPreferences()) {
					if (preference != null) {
						entityPreferenceItems.add(new EntityPaxGenderPreference(preference));
					}
				}
				entityPreferences.setPaxGenderPreferences(entityPreferenceItems);
			}
			if (preferences.getPaxPetsPreferences() != null) {
				List<EntityPreference<PaxPetsValues>> entityPreferenceItems = new ArrayList<>();
				for (PaxPetsPreference preference : preferences.getPaxPetsPreferences()) {
					if (preference != null) {
						entityPreferenceItems.add(new EntityPaxPetsPreference(preference));
					}
				}
				entityPreferences.setPaxPetsPreferences(entityPreferenceItems);
			}
			if (preferences.getPaxSmokerPreferences() != null) {
				List<EntityPreference<PaxSmokerValues>> entityPreferenceItems = new ArrayList<>();
				for (PaxSmokerPreference preference : preferences.getPaxSmokerPreferences()) {
					if (preference != null) {
						entityPreferenceItems.add(new EntityPaxSmokerPreference(preference));
					}
				}
				entityPreferences.setPaxSmokerPreferences(entityPreferenceItems);
			}
			if (preferences.getStringPreferences() != null) {
				List<EntityPreference<String>> entityPreferenceItems = new ArrayList<>();
				for (StringPreference preference : preferences.getStringPreferences()) {
					if (preference != null) {
						entityPreferenceItems.add(new EntityStringPreference(preference));
					}
				}
				entityPreferences.setStringPreferences(entityPreferenceItems);
			}
			entityOffer.setPreferences(entityPreferences);
		}
		entityOffer.setAdditionalInfo(offer.getAdditionalInfo());
		return entityOffer;
	}

	private <T> List<T> removeDuplicates(List<T> list) {
		List<T> noDupList = list.stream()
				.distinct()
				.collect(Collectors.toList());
		return noDupList;
	}

	private EntityLocation apiToEntity(final Location location) {
		EntityLocation entityLocation = new EntityLocation();
		entityLocation.setId(location.getId());
		entityLocation.setLatitude(location.getLatitude());
		entityLocation.setLongitude(location.getLongitude());
		entityLocation.setName(location.getName());
		entityLocation.setType(location.getType());
		return entityLocation;
	}

	private EntityContactOption apiToEntity(final ContactOption contactOption) {
		EntityContactOption entityContactOption = new EntityContactOption();
		entityContactOption.setId(contactOption.getId());
		entityContactOption.setContactType(contactOption.getContactType());
		entityContactOption.setContactIdentifier(contactOption.getContactIdentifier());
		return entityContactOption;
	}

	public Offer entityToApi(final EntityOffer entityOffer) {
		Offer offer = new Offer();
		offer.setId(entityOffer.getId());
		offer.setUserId(entityOffer.getUserId());
		offer.setStartDate(entityOffer.getStartDate());
		offer.setStartTime(entityOffer.getStartTime());
		offer.setStartTimezone(ZoneId.of(entityOffer.getStartTimezone()));
		offer.setOrigin(
				entityToApi(entityOffer.getOrigin()));
		offer.setDestination(
				entityToApi(entityOffer.getDestination()));
		if (entityOffer.getContactOptions() != null) {
			List<ContactOption> contactOptions = new ArrayList<>();
			for (EntityContactOption entityContactOption : entityOffer.getContactOptions()) {
				if (entityContactOption != null) {
					contactOptions.add(entityToApi(entityContactOption));
				}
			}
			offer.setContactOptions(contactOptions);
		}
		if (entityOffer.getTargetSystems() != null) {
			List<UUID> targetSystemIds = new ArrayList<>();
			for (EntityTargetSystem entityTargetSystem: entityOffer.getTargetSystems()) {
				targetSystemIds.add(entityTargetSystem.getId());
			}
			offer.setTargetSystemIds(targetSystemIds);
		}
		if (entityOffer.getPreferences() != null) {
			EntityPreferences entityPreferences = entityOffer.getPreferences();
			Preferences preferences = new Preferences();
			preferences.setId(entityPreferences.getId());
			if (entityPreferences.getBooleanPreferences() != null) {
				List<BooleanPreference> preferenceItems = new ArrayList<>();
				for (EntityPreference<Boolean> entityPreference : entityPreferences.getBooleanPreferences()) {
					if (entityPreference != null) {
						preferenceItems.add(
								(BooleanPreference) entityToApi(new BooleanPreference(), entityPreference));
					}
				}
				preferences.setBooleanPreferences(preferenceItems);
			}
			if (entityPreferences.getDoublePreferences() != null) {
				List<DoublePreference> preferenceItems = new ArrayList<>();
				for (EntityPreference<Double> entityPreference : entityPreferences.getDoublePreferences()) {
					if (entityPreference != null) {
						preferenceItems.add(
								(DoublePreference) entityToApi(new DoublePreference(), entityPreference));
					}
				}
				preferences.setDoublePreferences(preferenceItems);
			}
			if (entityPreferences.getIntegerPreferences() != null) {
				List<IntegerPreference> preferenceItems = new ArrayList<>();
				for (EntityPreference<Integer> entityPreference : entityPreferences.getIntegerPreferences()) {
					if (entityPreference != null) {
						preferenceItems.add(
								(IntegerPreference) entityToApi(new IntegerPreference(), entityPreference));
					}
				}
				preferences.setIntegerPreferences(preferenceItems);
			}
			if (entityPreferences.getPaxGenderPreferences() != null) {
				List<PaxGenderPreference> preferenceItems = new ArrayList<>();
				for (EntityPreference<PaxGenderValues> entityPreference : entityPreferences.getPaxGenderPreferences()) {
					if (entityPreference != null) {
						preferenceItems.add(
								(PaxGenderPreference) entityToApi(new PaxGenderPreference(), entityPreference));
					}
				}
				preferences.setPaxGenderPreferences(preferenceItems);
			}
			if (entityPreferences.getPaxPetsPreferences() != null) {
				List<PaxPetsPreference> preferenceItems = new ArrayList<>();
				for (EntityPreference<PaxPetsValues> entityPreference : entityPreferences.getPaxPetsPreferences()) {
					if (entityPreference != null) {
						preferenceItems.add(
								(PaxPetsPreference) entityToApi(new PaxPetsPreference(), entityPreference));
					}
				}
				preferences.setPaxPetsPreferences(preferenceItems);
			}
			if (entityPreferences.getPaxSmokerPreferences() != null) {
				List<PaxSmokerPreference> preferenceItems = new ArrayList<>();
				for (EntityPreference<PaxSmokerValues> entityPreference : entityPreferences.getPaxSmokerPreferences()) {
					if (entityPreference != null) {
						preferenceItems.add(
								(PaxSmokerPreference) entityToApi(new PaxSmokerPreference(), entityPreference));
					}
				}
				preferences.setPaxSmokerPreferences(preferenceItems);
			}
			if (entityPreferences.getStringPreferences() != null) {
				List<StringPreference> preferenceItems = new ArrayList<>();
				for (EntityPreference<String> entityPreference : entityPreferences.getStringPreferences()) {
					if (entityPreference != null) {
						preferenceItems.add(
								(StringPreference) entityToApi(new StringPreference(), entityPreference));
					}
				}
				preferences.setStringPreferences(preferenceItems);
			}
			offer.setPreferences(preferences);
		}
		offer.setAdditionalInfo(entityOffer.getAdditionalInfo());
		return offer;
	}

	private Location entityToApi(final EntityLocation entitylocation) {
		Location location = new Location();
		location.setId(entitylocation.getId());
		location.setLatitude(entitylocation.getLatitude());
		location.setLongitude(entitylocation.getLongitude());
		location.setName(entitylocation.getName());
		location.setType(entitylocation.getType());
		return location;
	}

	private ContactOption entityToApi(final EntityContactOption entityContactOption) {
		ContactOption contactOption = new ContactOption();
		contactOption.setId(entityContactOption.getId());
		contactOption.setContactType(entityContactOption.getContactType());
		contactOption.setContactIdentifier(entityContactOption.getContactIdentifier());
		return contactOption;
	}

	private <T> Preference<T> entityToApi(Preference<T> preference, final EntityPreference<T> entityPreference) {
		preference.setId(entityPreference.getId());
		preference.setKey(entityPreference.getKey());
		preference.setValue(entityPreference.getValue());
		return preference;
	}

	public String apiToJSONString(final Offer offer) {
		try {
			return objectMapper.writeValueAsString(offer);
		} catch (JsonProcessingException e) {
			log.warn("JSON processing problem: " + e.getMessage());
		}
		return "";
	}
}
