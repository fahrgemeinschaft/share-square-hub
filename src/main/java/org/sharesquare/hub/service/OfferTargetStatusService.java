package org.sharesquare.hub.service;

import java.util.ArrayList;
import java.util.List;

import org.sharesquare.hub.model.data.EntityOffer;
import org.sharesquare.hub.model.data.EntityOfferTargetStatus;
import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.sharesquare.hub.repository.OfferTargetStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferTargetStatusService {

	@Autowired
	private OfferTargetStatusRepository offerTargetStatusRepository;

	protected void init(final EntityOffer entityOffer) {
		List<EntityOfferTargetStatus> statusList = new ArrayList<>();
		EntityOfferTargetStatus status;
		for (EntityTargetSystem targetSystem : entityOffer.getTargetSystems()) {
			status = new EntityOfferTargetStatus();
			status.setOfferId(entityOffer.getId());
			status.setTargetSystemId(targetSystem.getId());
			statusList.add(status);
		}
		offerTargetStatusRepository.saveAll(statusList);
	}

	protected void setStatus(final EntityOffer entityOffer, final EntityTargetSystem entityTargetSystem,
			final EntityOfferTargetStatus.Status status) {
		EntityOfferTargetStatus entityStatus = new EntityOfferTargetStatus();
		entityStatus.setOfferId(entityOffer.getId());
		entityStatus.setTargetSystemId(entityTargetSystem.getId());
		entityStatus.setStatus(status);
		offerTargetStatusRepository.save(entityStatus);
	}

	protected void remove(final EntityOffer entityOffer) {
		Iterable<EntityOfferTargetStatus> statusIterable = offerTargetStatusRepository
				.findByOfferId(entityOffer.getId());
		offerTargetStatusRepository.deleteAll(statusIterable);
	}

	protected void remove(final EntityTargetSystem entityTargetSystem) {
		offerTargetStatusRepository.deleteAllByTargetSystemId(entityTargetSystem.getId());
	}
}
