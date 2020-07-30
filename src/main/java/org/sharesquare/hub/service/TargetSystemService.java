package org.sharesquare.hub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sharesquare.hub.conversion.TargetSystemConverter;
import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.sharesquare.hub.repository.TargetSystemRepository;
import org.sharesquare.model.TargetSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetSystemService {

	@Autowired
	private TargetSystemRepository targetSystemRepository;

	@Autowired
	private TargetSystemConverter targetSystemConverter;

	@Autowired
	private OfferTargetStatusService offerTargetStatusService;

	public TargetSystem addTargetSystem(final TargetSystem targetSystem) {
		EntityTargetSystem entityTargetSystem = targetSystemConverter.apiToEntity(targetSystem);
		removeIds(entityTargetSystem);
		EntityTargetSystem savedTargetSystem = targetSystemRepository.save(entityTargetSystem);
		return targetSystemConverter.entityToApi(savedTargetSystem);
	}

	private void removeIds(EntityTargetSystem entityTargetSystem) {
		entityTargetSystem.setId(null);
		if (entityTargetSystem.getConnector() != null) {
			entityTargetSystem.getConnector().setId(null);
		}
	}

	public boolean deleteTargetSystem(final UUID id) {
		Optional<EntityTargetSystem> entityTargetSystem = targetSystemRepository.findById(id);
		if (entityTargetSystem.isPresent()) {
			offerTargetStatusService.remove(entityTargetSystem.get());
			targetSystemRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public List<TargetSystem> getTargetSystems() {
		List<EntityTargetSystem> entityTargetSystems = getEntityTargetSystems();
		return targetSystemConverter.entityToApi(entityTargetSystems);
	}

	protected List<EntityTargetSystem> getEntityTargetSystems() {
		Iterable<EntityTargetSystem> entityTargetSystems = targetSystemRepository.findAll();
		List<EntityTargetSystem> entityTargetSystemList = new ArrayList<>();
		entityTargetSystems.forEach(entityTargetSystemList::add);
		return entityTargetSystemList;
	}
}
