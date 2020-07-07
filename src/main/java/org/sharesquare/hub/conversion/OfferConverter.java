package org.sharesquare.hub.conversion;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.sharesquare.hub.model.data.EntityContactOption;
import org.sharesquare.hub.model.data.EntityLocation;
import org.sharesquare.hub.model.data.EntityOffer;
import org.sharesquare.hub.model.data.EntityPreference;
import org.sharesquare.hub.model.data.preferences.EntityBooleanPreference;
import org.sharesquare.hub.model.data.preferences.EntityDoublePreference;
import org.sharesquare.hub.model.data.preferences.EntityIntegerPreference;
import org.sharesquare.hub.model.data.preferences.EntityPaxGenderPreference;
import org.sharesquare.hub.model.data.preferences.EntityPaxPetsPreference;
import org.sharesquare.hub.model.data.preferences.EntityPaxSmokerPreference;
import org.sharesquare.hub.model.data.preferences.EntityStringPreference;
import org.sharesquare.model.ContactOption;
import org.sharesquare.model.Location;
import org.sharesquare.model.Offer;
import org.sharesquare.model.Preference;
import org.sharesquare.model.preferences.BooleanPreference;
import org.sharesquare.model.preferences.DoublePreference;
import org.sharesquare.model.preferences.IntegerPreference;
import org.sharesquare.model.preferences.PaxGenderPreference;
import org.sharesquare.model.preferences.PaxPetsPreference;
import org.sharesquare.model.preferences.PaxSmokerPreference;
import org.sharesquare.model.preferences.StringPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OfferConverter {

	private static final Logger log = LoggerFactory.getLogger(OfferConverter.class);

	public EntityOffer apiToEntity(Offer offer) {
		EntityOffer entityOffer = new EntityOffer();
		entityOffer.setId(offer.getId());
		entityOffer.setUserId(offer.getUserId());
		entityOffer.setStartDate(offer.getStartDate());
		entityOffer.setStartTime(offer.getStartTime());
		entityOffer.setStartTimezone(offer.getStartTimezone());
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
		if (offer.getTargetPlatforms() != null) {
			List<UUID> entityTargetPlatformIds = new ArrayList<>();
			for (UUID targetPlatformId : offer.getTargetPlatforms()) {
				if (targetPlatformId != null) {
					entityTargetPlatformIds.add(targetPlatformId);
				}
			}
			entityOffer.setTargetPlatforms(entityTargetPlatformIds);
		}
		if (offer.getPreferences() != null) {
			List<EntityPreference<?>> entityPreferences = new ArrayList<>();
			for (Preference<?> preference : offer.getPreferences()) {
				if (preference != null) {
					EntityPreference<?> entityPreference = null;
					String className = preference.getClass().getSimpleName();
					switch (className) {
					case "BooleanPreference":
						entityPreference = new EntityBooleanPreference(preference);
						break;
					case "DoublePreference":
						entityPreference = new EntityDoublePreference(preference);
						break;
					case "IntegerPreference":
						entityPreference = new EntityIntegerPreference(preference);
						break;
					case "PaxGenderPreference":
						entityPreference = new EntityPaxGenderPreference(preference);
						break;
					case "PaxPetsPreference":
						entityPreference = new EntityPaxPetsPreference(preference);
						break;
					case "PaxSmokerPreference":
						entityPreference = new EntityPaxSmokerPreference(preference);
						break;
					case "StringPreference":
						entityPreference = new EntityStringPreference(preference);
						break;
					default:
						log.error("Unknown Preference: " + className);
						break;
					}
					if (entityPreference != null) {
						entityPreferences.add(entityPreference);
					}
				}
			}
			entityOffer.setPreferences(entityPreferences);
		}
		entityOffer.setAdditionalInfo(offer.getAdditionalInfo());
		return entityOffer;
	}

	private EntityLocation apiToEntity(final Location location) {
		if (location != null) {
			EntityLocation entityLocation = new EntityLocation();
			entityLocation.setId(location.getId());
			entityLocation.setLatitude(location.getLatitude());
			entityLocation.setLongitude(location.getLongitude());
			entityLocation.setName(location.getName());
			entityLocation.setType(location.getType());
			return entityLocation;
		}
		return null;
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
		offer.setStartTimezone(entityOffer.getStartTimezone());
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
		if (entityOffer.getTargetPlatforms() != null) {
			offer.setTargetPlatforms(entityOffer.getTargetPlatforms());
		}
		if (entityOffer.getPreferences() != null) {
			List<Preference> preferences = new ArrayList<>();
			for (EntityPreference<?> entityPreference : entityOffer.getPreferences()) {
				if (entityPreference != null) {
					Preference<?> preference = null;
					String className = entityPreference.getClass().getSimpleName();
					switch (className) {
					case "EntityBooleanPreference":
						preference = new BooleanPreference();
						entityToApi(preference, entityPreference);
						break;
					case "EntityDoublePreference":
						preference = new DoublePreference();
						entityToApi(preference, entityPreference);
						break;
					case "EntityIntegerPreference":
						preference = new IntegerPreference();
						entityToApi(preference, entityPreference);
						break;
					case "EntityPaxGenderPreference":
						preference = new PaxGenderPreference();
						entityToApi(preference, entityPreference);
						break;
					case "EntityPaxPetsPreference":
						preference = new PaxPetsPreference();
						entityToApi(preference, entityPreference);
						break;
					case "EntityPaxSmokerPreference":
						preference = new PaxSmokerPreference();
						entityToApi(preference, entityPreference);
						break;
					case "EntityStringPreference":
						preference = new StringPreference();
						entityToApi(preference, entityPreference);
						break;
					default:
						log.error("Unknown EntityPreference: " + className);
						break;
					}
					if (preference != null) {
						preferences.add(preference);
					}
				}
			}
			offer.setPreferences(preferences);
		}
		offer.setAdditionalInfo(entityOffer.getAdditionalInfo());
		return offer;
	}

	private Location entityToApi(final EntityLocation entitylocation) {
		if (entitylocation != null) {
			Location location = new Location();
			location.setId(entitylocation.getId());
			location.setLatitude(entitylocation.getLatitude());
			location.setLongitude(entitylocation.getLongitude());
			location.setName(entitylocation.getName());
			location.setType(entitylocation.getType());
			return location;
		}
		return null;
	}

	private ContactOption entityToApi(final EntityContactOption entityContactOption) {
		ContactOption contactOption = new ContactOption();
		contactOption.setId(entityContactOption.getId());
		contactOption.setContactType(entityContactOption.getContactType());
		contactOption.setContactIdentifier(entityContactOption.getContactIdentifier());
		return contactOption;
	}

	private <T> Preference<T> entityToApi(Preference<T> preference, final EntityPreference<?> entityPreference) {
		preference.setId(entityPreference.getId());
		preference.setKey(entityPreference.getKey());
		preference.setValue((T) entityPreference.getValue());
		return preference;
	}
}
