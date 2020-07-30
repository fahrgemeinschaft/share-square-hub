package org.sharesquare.hub.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.sharesquare.hub.model.data.EntityClient;
import org.sharesquare.hub.model.data.EntityConnector;
import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExampleData {

	private static final Logger log = LoggerFactory.getLogger(ExampleData.class);

	@Value("${custom.data.example.target.name}")
	private String exampleTargetName;

	@Value("${custom.data.example.target.connector.update.webhook}")
	private String exampleTargetConnectorUpdateWebhook;

	@Value("${custom.data.example.target.connector.apikey}")
	private String exampleTargetConnectorApikey;

	@Value("${custom.data.example.target.connector.client.name}")
	private String exampleTargetConnectorClientName;

	@Value("${custom.data.example.target.connector.client.authkey}")
	private String exampleTargetConnectorClientAuthkey;

	protected List<EntityTargetSystem> getEntityTargetSystems() {
		List<EntityTargetSystem> exampleTargetSystems = new ArrayList<>();

		try {
			EntityConnector exampleConnector = new EntityConnector();
			exampleConnector.setAliveCheckWebhook("https://www.fahrgemeinschaft.de/api/alivecheck");
			exampleConnector.setOfferUpdateWebhook("https://www.fahrgemeinschaft.de/api/offers");

			EntityTargetSystem exampleTargetSystem = new EntityTargetSystem();
			exampleTargetSystem.setName("Fahrgemeinschaft.de");
			exampleTargetSystem.setDescription("Kostenlose Mitfahrgelegenheit");
			exampleTargetSystem.setVanityUrl(new URL("http://www.fahrgemeinschaft.de"));
			exampleTargetSystem.setContentLanguage("de");
			exampleTargetSystem.setDataProtectionRegulations("(hier Text einf\u00fcgen)");
			exampleTargetSystem.setConnector(exampleConnector);

			exampleTargetSystems.add(exampleTargetSystem);

			exampleConnector = new EntityConnector();
			exampleConnector.setAliveCheckWebhook("https://www.ride2go.com/api/alivecheck");
			exampleConnector.setOfferUpdateWebhook("https://www.ride2go.com/api/offers");

			exampleTargetSystem = new EntityTargetSystem();
			exampleTargetSystem.setName("ride2Go");
			exampleTargetSystem.setDescription("Fahrgemeinschaften und Mitfahrgelegenheiten");
			exampleTargetSystem.setVanityUrl(new URL("http://www.ride2go.com"));
			exampleTargetSystem.setContentLanguage("de");
			exampleTargetSystem.setDataProtectionRegulations("(hier Text einf\u00fcgen)");
			exampleTargetSystem.setConnector(exampleConnector);

			exampleTargetSystems.add(exampleTargetSystem);

			exampleConnector = new EntityConnector();
			exampleConnector.setAliveCheckWebhook("https://www.mifaz.de/api/alivecheck");
			exampleConnector.setOfferUpdateWebhook("https://www.mifaz.de/api/offers");

			exampleTargetSystem = new EntityTargetSystem();
			exampleTargetSystem.setName("Mitfahrzentrale MiFaZ");
			exampleTargetSystem.setDescription("Mitfahrgelegenheit und Fahrgemeinschaft");
			exampleTargetSystem.setVanityUrl(new URL("http://www.mifaz.de"));
			exampleTargetSystem.setContentLanguage("de");
			exampleTargetSystem.setDataProtectionRegulations("(hier Text einf\u00fcgen)");
			exampleTargetSystem.setConnector(exampleConnector);

			exampleTargetSystems.add(exampleTargetSystem);

			List<EntityClient> exampleClients = new ArrayList<>();
			EntityClient exampleClient = new EntityClient();
			exampleClient.setName(exampleTargetConnectorClientName);
			exampleClient.setAuthkey(exampleTargetConnectorClientAuthkey);
			exampleClients.add(exampleClient);

			exampleConnector = new EntityConnector();
			exampleConnector.setOfferUpdateWebhook(exampleTargetConnectorUpdateWebhook);
			exampleConnector.setApikey(exampleTargetConnectorApikey);
			exampleConnector.setClients(exampleClients);

			exampleTargetSystem = new EntityTargetSystem();
			exampleTargetSystem.setName(exampleTargetName);
			exampleTargetSystem.setConnector(exampleConnector);
			exampleTargetSystem.setActive(true);

			exampleTargetSystems.add(exampleTargetSystem);
		} catch (MalformedURLException e) {
			log.warn("Example data has malformed URL: " + e.getMessage());
		}
		return exampleTargetSystems;
	}
}
