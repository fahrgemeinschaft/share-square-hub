package org.sharesquare.hub.service;

import org.sharesquare.hub.conversion.TargetSystemTripConverter;
import org.sharesquare.hub.model.data.EntityClient;
import org.sharesquare.hub.model.data.EntityConnector;
import org.sharesquare.hub.model.data.EntityOffer;
import org.sharesquare.hub.model.data.EntityOfferTargetStatus.Status;
import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.sharesquare.hub.model.item.targetsystem.request.TripRequest;
import org.sharesquare.model.Connector;
import org.sharesquare.model.Offer;
import org.sharesquare.model.connector.ConnectorState;
import org.sharesquare.model.connector.ConnectorStateValues;
import org.sharesquare.repository.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.sun.xml.bind.v2.TODO;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class ConnectorService {
	
	private static final Logger log = LoggerFactory.getLogger(ConnectorService.class);

    @Autowired
    IRepository<Connector> connectorRepo;

    @Autowired
    IRepository<ConnectorState> connectorStateRepo;
    
    @Autowired
    private TargetSystemTripConverter targetSystemTripConverter;
    
    @Autowired
    private TargetSystemService targetSystemService;
    
    @Autowired
    private OfferTargetStatusService offerTargetStatusService;
    
    @Value("${custom.data.example.target.connector.client.name}")
    private String exampleTargetConnectorClientName;

  //  @Autowired
   // WebClient oauthWebClient;

	protected void addOffer(final EntityOffer entityOffer) {
		TripRequest trip = targetSystemTripConverter.entityToApi(entityOffer);
		log.info("Target systems POST request for Trip instance '{}'", targetSystemTripConverter.apiToJSONString(trip));
		List<EntityTargetSystem> targetSystems = targetSystemService.getEntityTargetSystems();
		EntityConnector connector;
		ClientResponse response;

		TODO.checkSpec("https://github.com/fahrgemeinschaft/share-square-hub/issues/27");
		entityOffer.setClientId(exampleTargetConnectorClientName);

		for (EntityTargetSystem targetSystem : targetSystems) {
			if (targetSystem.isActive()) {
				connector = targetSystem.getConnector();
				if (connector != null) {
					for (EntityClient client : connector.getClients()) {
						if (entityOffer.getClientId().equals(client.getName())) {
							try {
								response = WebClient.builder().build()
										.post()
										.uri(connector.getOfferUpdateWebhook())
										.contentType(APPLICATION_JSON)
										.bodyValue(trip)
										.header("apikey", connector.getApikey())
										.header("authkey", client.getAuthkey())
										.exchange()
										.block();
								if (response != null) {
									log.info("Target system '{}' POST response status code: {}",
											targetSystem.getName(), response.statusCode());
									log.info("Target system '{}' POST response body: {}",
											targetSystem.getName(), response.bodyToMono(Map.class).block());
								}
								if (response != null
										&& (response.statusCode() == OK || response.statusCode() == CREATED)) {
									offerTargetStatusService.setStatus(entityOffer, targetSystem, Status.SUCCESS);
								} else {
									offerTargetStatusService.setStatus(entityOffer, targetSystem, Status.FAILED);
								}
							} catch (Exception e) {
								log.error("WebClient problem: ", e);
								offerTargetStatusService.setStatus(entityOffer, targetSystem, Status.FAILED);
							}
						}
					}
				}
			}
		}
	}
    
    public void updateOffer(final Offer offer){
        /*
        assert offer!=null;
        connectorStateRepo.getAll().stream()
                .filter(connectorState -> connectorState.getState().equals(ConnectorStateValues.ALIVE))
                .map(connectorState -> connectorRepo.findById(connectorState.getConnectorId().toString()))
                .map(connector -> connector.map(Connector::getOfferUpdateWebhook).orElse(null))
                .forEach(url -> sendOfferUpdate(url,offer));
*/
    }

    protected void sendOfferUpdate(final URL url, final Offer offer)  {
        /*
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        oauthWebClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(offer)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                .subscribe();//TODO

*/
    }
}
