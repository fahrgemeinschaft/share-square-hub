package org.sharesquare.hub.repository;

import java.util.Optional;
import java.util.UUID;

import org.sharesquare.hub.model.data.EntityOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface OfferRepository extends CrudRepository<EntityOffer, UUID> {

	boolean existsByIdAndClientId(UUID id, String clientId);

	Optional<EntityOffer> findByIdAndClientId(UUID id, String clientId);

	Page<EntityOffer> findByUserIdAndClientId(String userId, String clientId, Pageable pageable);
}
