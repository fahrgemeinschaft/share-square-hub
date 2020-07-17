package org.sharesquare.hub.conversion;

import java.util.ArrayList;
import java.util.List;

import org.sharesquare.hub.model.data.EntityConnector;
import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.sharesquare.model.Connector;
import org.sharesquare.model.TargetSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TargetSystemConverter {

	private static final Logger log = LoggerFactory.getLogger(TargetSystemConverter.class);

	@Autowired
	private ObjectMapper objectMapper;

	public EntityTargetSystem apiToEntity(TargetSystem targetSystem) {
		EntityTargetSystem entityTargetSystem = new EntityTargetSystem();
		entityTargetSystem.setId(targetSystem.getId());
		entityTargetSystem.setName(targetSystem.getName());
		entityTargetSystem.setDescription(targetSystem.getDescription());
		entityTargetSystem.setVanityUrl(targetSystem.getVanityUrl());
		entityTargetSystem.setContentLanguage(targetSystem.getContentLanguage());
		entityTargetSystem.setDataProtectionRegulations(targetSystem.getDataProtectionRegulations());
		entityTargetSystem.setConnector(
				apiToEntity(targetSystem.getConnector()));
		return entityTargetSystem;
	}

	private EntityConnector apiToEntity(final Connector connector) {
		if (connector != null) {
			EntityConnector entityConnector = new EntityConnector();
			entityConnector.setId(connector.getId());
			entityConnector.setOfferUpdateWebhook(connector.getOfferUpdateWebhook());
			entityConnector.setAliveCheckWebhook(connector.getAliveCheckWebhook());
			return entityConnector;
		}
		return null;
	}

	public TargetSystem entityToApi(final EntityTargetSystem entityTargetSystem) {
		TargetSystem targetSystem = new TargetSystem();
		targetSystem.setId(entityTargetSystem.getId());
		targetSystem.setName(entityTargetSystem.getName());
		targetSystem.setDescription(entityTargetSystem.getDescription());
		targetSystem.setVanityUrl(entityTargetSystem.getVanityUrl());
		targetSystem.setContentLanguage(entityTargetSystem.getContentLanguage());
		targetSystem.setDataProtectionRegulations(entityTargetSystem.getDataProtectionRegulations());
		targetSystem.setConnector(
				entityToApi(entityTargetSystem.getConnector()));
		return targetSystem;
	}

	private Connector entityToApi(final EntityConnector entityConnector) {
		if (entityConnector != null) {
			Connector connector = new Connector();
			connector.setId(entityConnector.getId());
			connector.setOfferUpdateWebhook(entityConnector.getOfferUpdateWebhook());
			connector.setAliveCheckWebhook(entityConnector.getAliveCheckWebhook());
			return connector;
		}
		return null;
	}

	public List<TargetSystem> entityToApi(final List<EntityTargetSystem> entityTargetSystems) {
		List<TargetSystem> targetSystems = new ArrayList<>();
		for (EntityTargetSystem entityTargetSystem : entityTargetSystems) {
			targetSystems.add(entityToApi(entityTargetSystem));
		}
		return targetSystems;
	}

	public String apiToJSONString(final TargetSystem targetSystem) {
		try {
			return objectMapper.writeValueAsString(targetSystem);
		} catch (JsonProcessingException e) {
			log.warn("JSON processing problem: " + e.getMessage());
		}
		return "";
	}
}
