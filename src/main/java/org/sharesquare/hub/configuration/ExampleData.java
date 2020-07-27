package org.sharesquare.hub.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.sharesquare.hub.model.data.EntityConnector;
import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExampleData {

	private static final Logger log = LoggerFactory.getLogger(ExampleData.class);

	private ExampleData() {
	}

	protected static List<EntityTargetSystem> getEntityTargetSystems() {
		List<EntityTargetSystem> exampleTargetSystems = new ArrayList<>();

		try {
			EntityConnector exampleConnector = new EntityConnector();
			exampleConnector.setAliveCheckWebhook(new URL("https://www.fahrgemeinschaft.de/api/alivecheck"));
			exampleConnector.setOfferUpdateWebhook(new URL("https://www.fahrgemeinschaft.de/api/offers"));

			EntityTargetSystem exampleTargetSystem = new EntityTargetSystem();
			exampleTargetSystem.setName("Fahrgemeinschaft.de");
			exampleTargetSystem.setDescription("Kostenlose Mitfahrgelegenheit");
			exampleTargetSystem.setVanityUrl(new URL("http://www.fahrgemeinschaft.de"));
			exampleTargetSystem.setContentLanguage("de");
			exampleTargetSystem.setDataProtectionRegulations("(hier Text einf\u00fcgen)");
			exampleTargetSystem.setConnector(exampleConnector);

			exampleTargetSystems.add(exampleTargetSystem);

			exampleConnector = new EntityConnector();
			exampleConnector.setAliveCheckWebhook(new URL("https://www.ride2go.com/api/alivecheck"));
			exampleConnector.setOfferUpdateWebhook(new URL("https://www.ride2go.com/api/offers"));

			exampleTargetSystem = new EntityTargetSystem();
			exampleTargetSystem.setName("ride2Go");
			exampleTargetSystem.setDescription("Fahrgemeinschaften und Mitfahrgelegenheiten");
			exampleTargetSystem.setVanityUrl(new URL("http://www.ride2go.com"));
			exampleTargetSystem.setContentLanguage("de");
			exampleTargetSystem.setDataProtectionRegulations("(hier Text einf\u00fcgen)");
			exampleTargetSystem.setConnector(exampleConnector);

			exampleTargetSystems.add(exampleTargetSystem);

			exampleConnector = new EntityConnector();
			exampleConnector.setAliveCheckWebhook(new URL("https://www.mifaz.de/api/alivecheck"));
			exampleConnector.setOfferUpdateWebhook(new URL("https://www.mifaz.de/api/offers"));

			exampleTargetSystem = new EntityTargetSystem();
			exampleTargetSystem.setName("Mitfahrzentrale MiFaZ");
			exampleTargetSystem.setDescription("Mitfahrgelegenheit und Fahrgemeinschaft");
			exampleTargetSystem.setVanityUrl(new URL("http://www.mifaz.de"));
			exampleTargetSystem.setContentLanguage("de");
			exampleTargetSystem.setDataProtectionRegulations("(hier Text einf\u00fcgen)");
			exampleTargetSystem.setConnector(exampleConnector);

			exampleTargetSystems.add(exampleTargetSystem);
		} catch (MalformedURLException e) {
			log.warn("Example data has malformed URL: " + e.getMessage());
		}
		return exampleTargetSystems;
	}
}
