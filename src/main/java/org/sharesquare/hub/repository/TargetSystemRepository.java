package org.sharesquare.hub.repository;

import java.util.Optional;
import java.util.UUID;

import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.springframework.data.repository.CrudRepository;

public interface TargetSystemRepository extends CrudRepository<EntityTargetSystem, UUID> {

	Optional<EntityTargetSystem> findByIdAndClientId(UUID id, String clientId);
}
