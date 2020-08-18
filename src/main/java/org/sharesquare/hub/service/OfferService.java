package org.sharesquare.hub.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.sharesquare.hub.conversion.OfferConverter;
import org.sharesquare.hub.model.data.*;
import org.sharesquare.hub.model.data.preferences.EntityPreference;
import org.sharesquare.hub.repository.OfferRepository;
import org.sharesquare.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private OfferConverter offerConverter;

    @Autowired
    private ConnectorService connectorService;
    
    @Autowired
    private OfferTargetStatusService offerTargetStatusService;

    @Autowired
    private AuthorizationService authorizationService;

	public Offer getOffer(final UUID id) {
		String clientId = authorizationService.getClientId();
		Optional<EntityOffer> entityOffer = offerRepository.findByIdAndClientId(id, clientId);
		if (entityOffer.isPresent()) {
			Offer offer = offerConverter.entityToApi(entityOffer.get());
			return offer;
		}
		return null;
	}

	public Offer addOffer(final Offer offer) {
		String clientId = authorizationService.getClientId();
		EntityOffer savedOffer = save(null, offer, clientId);
		offerTargetStatusService.init(savedOffer);
		connectorService.addOffer(savedOffer);
		return offerConverter.entityToApi(savedOffer);
	}

	public boolean updateOffer(final UUID id, final Offer offer) {
		String clientId = authorizationService.getClientId();
		if (offerRepository.existsByIdAndClientId(id, clientId)) {
			save(id, offer, clientId);
			return true;
		}
		return false;
	}

	public Page<Offer> getOffers(final String userId, final Pageable pageable) {
		String clientId = authorizationService.getClientId();
		Page<EntityOffer> entityOffers = offerRepository.findByUserIdAndClientId(userId, clientId, pageable);
		Page<Offer> offers = entityOffers.map(new Function<EntityOffer, Offer>() {
			@Override
			public Offer apply(EntityOffer entity) {
				return offerConverter.entityToApi(entity);
			}
		});
		return offers;
	}

	public boolean deleteOffer(final UUID id) {
		String clientId = authorizationService.getClientId();
		Optional<EntityOffer> entityOffer = offerRepository.findByIdAndClientId(id, clientId);
		if (entityOffer.isPresent()) {
			offerTargetStatusService.remove(entityOffer.get());
			offerRepository.deleteById(id);
			return true;
		}
		return false;
	}

	private EntityOffer save(final UUID id, final Offer offer, final String clientId) {
		EntityOffer entityOffer = offerConverter.apiToEntity(offer);
		removeIds(entityOffer);
		setPreferenceIds(entityOffer);
		entityOffer.setId(id);
		entityOffer.setClientId(clientId);
		return offerRepository.save(entityOffer);
	}

	private void removeIds(EntityOffer entityOffer) {
		removeIds(entityOffer.getOrigin());
		removeIds(entityOffer.getDestination());
		removeIds(entityOffer.getContactOptions());
	}

	private void removeIds(EntityLocation entityLocation) {
		if (entityLocation != null) {
			entityLocation.setId(null);
		}
	}

	private void removeIds(List<EntityContactOption> entityContactOptions) {
		if (entityContactOptions != null) {
			for (EntityContactOption entityContactOption : entityContactOptions) {
				if (entityContactOption != null) {
					entityContactOption.setId(null);
				}
			}
		}
	}

	private void setPreferenceIds(EntityOffer entityOffer) {
		if (entityOffer.getPreferences() != null) {
			EntityPreferences entityPreferences = entityOffer.getPreferences();
			entityPreferences.setId(null);
			setPreferenceItemIds(entityPreferences.getBooleanPreferences());
			setPreferenceItemIds(entityPreferences.getDoublePreferences());
			setPreferenceItemIds(entityPreferences.getIntegerPreferences());
			setPreferenceItemIds(entityPreferences.getPaxGenderPreferences());
			setPreferenceItemIds(entityPreferences.getPaxPetsPreferences());
			setPreferenceItemIds(entityPreferences.getPaxSmokerPreferences());
			setPreferenceItemIds(entityPreferences.getStringPreferences());
		}
	}

	private <T> void setPreferenceItemIds(List<EntityPreference<T>> entityPreferenceItems) {
		if (entityPreferenceItems != null) {
			for (EntityPreference<?> entityPreference : entityPreferenceItems) {
				if (entityPreference != null) {
					entityPreference.setId(UUID.randomUUID());
				}
			}
		}
	}
}
