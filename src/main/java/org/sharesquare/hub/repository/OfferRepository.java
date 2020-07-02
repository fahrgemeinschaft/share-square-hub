package org.sharesquare.hub.repository;

import java.util.UUID;

import org.sharesquare.hub.model.data.EntityOffer;
import org.springframework.data.repository.CrudRepository;

public interface OfferRepository extends CrudRepository<EntityOffer, UUID> {
}
