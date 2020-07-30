package org.sharesquare.hub.conversion;

import java.util.ArrayList;
import java.util.List;

import org.sharesquare.hub.model.data.EntityClient;
import org.sharesquare.hub.model.data.EntityConnector;
import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.sharesquare.model.Client;
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
		entityTargetSystem.setActive(targetSystem.isActive());
		return entityTargetSystem;
	}

	private EntityConnector apiToEntity(final Connector connector) {
		if (connector != null) {
			EntityConnector entityConnector = new EntityConnector();
			entityConnector.setId(connector.getId());
			if (connector.getOfferUpdateWebhook() != null) {
				entityConnector.setOfferUpdateWebhook(
						connector.getOfferUpdateWebhook().toString());
			}
			if (connector.getAliveCheckWebhook() != null) {
				entityConnector.setAliveCheckWebhook(
						connector.getAliveCheckWebhook().toString());
			}
			entityConnector.setApikey(connector.getApikey());
			if (connector.getClients() != null) {
				List<EntityClient> entityClients = new ArrayList<>();
				for (Client client : connector.getClients()) {
					if (client != null) {
						entityClients.add(apiToEntity(client));
					}
				}
				entityConnector.setClients(entityClients);
			}
			return entityConnector;
		}
		return null;
	}

	private EntityClient apiToEntity(final Client client) {
		EntityClient entityClient = new EntityClient();
		entityClient.setId(client.getId());
		entityClient.setName(client.getName());
		entityClient.setAuthkey(client.getAuthkey());
		return entityClient;
	}

	public TargetSystem entityToApi(final EntityTargetSystem entityTargetSystem) {
		TargetSystem targetSystem = new TargetSystem();
		targetSystem.setId(entityTargetSystem.getId());
		targetSystem.setName(entityTargetSystem.getName());
		targetSystem.setDescription(entityTargetSystem.getDescription());
		targetSystem.setVanityUrl(entityTargetSystem.getVanityUrl());
		targetSystem.setContentLanguage(entityTargetSystem.getContentLanguage());
		targetSystem.setDataProtectionRegulations(entityTargetSystem.getDataProtectionRegulations());
		targetSystem.setActive(entityTargetSystem.isActive());
		return targetSystem;
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
