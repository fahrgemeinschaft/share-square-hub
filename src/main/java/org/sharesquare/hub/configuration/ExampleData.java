package org.sharesquare.hub.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.sharesquare.model.Connector;
import org.sharesquare.model.TargetSystem;
import org.sharesquare.repository.IRepository;
import org.sharesquare.repository.SimpleInMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExampleData {

	private static final Logger log = LoggerFactory.getLogger(ExampleData.class);

	private ExampleData() {
	}

	protected static IRepository<Connector> connectorRepo() {
		IRepository<Connector> connectorRepo = new SimpleInMemoryRepository<Connector>();

		try {
			TargetSystem exampleTargetSystem = new TargetSystem();
			exampleTargetSystem.setId(UUID.randomUUID());
			exampleTargetSystem.setName("Fahrgemeinschaft.de");
			exampleTargetSystem.setDescription("Kostenlose Mitfahrgelegenheit");

			exampleTargetSystem.setVanityUrl(new URL("http://www.fahrgemeinschaft.de"));

			Connector exampleConnector = new Connector();
			exampleConnector.setTargetSystem(exampleTargetSystem);
			exampleConnector.setAliveCheckWebhook(new URL("https://www.fahrgemeinschaft.de/api/alivecheck"));
			exampleConnector.setOfferUpdateWebhook(new URL("https://www.fahrgemeinschaft.de/api/offers"));

			connectorRepo.create(exampleConnector);

			exampleTargetSystem = new TargetSystem();
			exampleTargetSystem.setId(UUID.randomUUID());
			exampleTargetSystem.setName("ride2Go");
			exampleTargetSystem.setDescription("Fahrgemeinschaften und Mitfahrgelegenheiten");
			exampleTargetSystem.setVanityUrl(new URL("http://www.ride2go.com"));

			exampleConnector = new Connector();
			exampleConnector.setTargetSystem(exampleTargetSystem);
			exampleConnector.setAliveCheckWebhook(new URL("https://www.ride2go.com/api/alivecheck"));
			exampleConnector.setOfferUpdateWebhook(new URL("https://www.ride2go.com/api/offers"));

			connectorRepo.create(exampleConnector);

			exampleTargetSystem = new TargetSystem();
			exampleTargetSystem.setId(UUID.randomUUID());
			exampleTargetSystem.setName("Mitfahrzentrale MiFaZ");
			exampleTargetSystem.setDescription("Mitfahrgelegenheit und Fahrgemeinschaft");
			exampleTargetSystem.setVanityUrl(new URL("http://www.mifaz.de"));

			exampleConnector = new Connector();
			exampleConnector.setTargetSystem(exampleTargetSystem);
			exampleConnector.setAliveCheckWebhook(new URL("https://www.mifaz.de/api/alivecheck"));
			exampleConnector.setOfferUpdateWebhook(new URL("https://www.mifaz.de/api/offers"));

			connectorRepo.create(exampleConnector);
		} catch (MalformedURLException e) {
			log.warn("Example data has malformed URL: " + e.getMessage());
		}
		return connectorRepo;
	}
}
