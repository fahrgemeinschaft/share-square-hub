package org.sharesquare.hub.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.sharesquare.hub.conversion.OfferConverter;
import org.sharesquare.hub.model.data.*;
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

    /**
     * Persist offer locally, then trigger connectors to forward to other portals.
     * @param offer
     * @return the created offer with an ID set.
     */
    public Optional<Offer> create(Offer offer){
        //any uuid will do.
        if(offer.getId()==null){
            offer.setId(UUID.randomUUID());
        }
        /*
        final Optional<Offer> result = offerRepository.create(offer);
        if(result.isPresent()) {
            connectorService.updateOffer(result.get());
        }
        return result;
        */
        return null;
    }

	public Offer getOffer(final UUID id) {
		Optional<EntityOffer> entityOffer = offerRepository.findById(id);
		if (entityOffer.isPresent()) {
			Offer offer = offerConverter.entityToApi(entityOffer.get());
			return offer;
		}
		return null;
	}

	public Offer addOffer(final Offer offer) {
		EntityOffer savedOffer = save(null, offer);
		return offerConverter.entityToApi(savedOffer);
	}

	public boolean updateOffer(final UUID id, final Offer offer) {
		if (offerRepository.existsById(id)) {
			save(id, offer);
			return true;
		}
		return false;
	}

	public Page<Offer> getOffers(final String userId, final Pageable pageable) {
		Page<EntityOffer> entityOffers = offerRepository.findByUserId(userId, pageable);
		Page<Offer> offers = entityOffers.map(new Function<EntityOffer, Offer>() {
			@Override
			public Offer apply(EntityOffer entity) {
				return offerConverter.entityToApi(entity);
			}
		});
		return offers;
	}

	public boolean deleteOffer(final UUID id) {
		if (offerRepository.existsById(id)) {
			offerRepository.deleteById(id);
			return true;
		}
		return false;
	}

	private EntityOffer save(final UUID id, final Offer offer) {
		EntityOffer entityOffer = offerConverter.apiToEntity(offer);
		removeIds(entityOffer);
		setPreferenceIds(entityOffer);
		entityOffer.setId(id);
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
			for (EntityPreference<?> entityPreference : entityOffer.getPreferences()) {
				if (entityPreference != null) {
					entityPreference.setId(UUID.randomUUID());
				}
			}
		}
	}
}
