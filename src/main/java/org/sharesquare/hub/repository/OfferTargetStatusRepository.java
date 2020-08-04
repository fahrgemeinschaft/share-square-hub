package org.sharesquare.hub.repository;

import java.util.UUID;

import org.sharesquare.hub.model.data.EntityOfferTargetStatus;
import org.sharesquare.hub.model.data.EntityOfferTargetStatusKey;
import org.springframework.data.repository.CrudRepository;

public interface OfferTargetStatusRepository
		extends CrudRepository<EntityOfferTargetStatus, EntityOfferTargetStatusKey> {

	void deleteAllByTargetSystemId(UUID targetSystemId);

	Iterable<EntityOfferTargetStatus> findByOfferId(UUID id);
}
