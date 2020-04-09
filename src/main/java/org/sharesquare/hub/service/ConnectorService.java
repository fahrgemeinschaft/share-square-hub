package org.sharesquare.hub.service;



import org.sharesquare.model.Connector;
import org.sharesquare.model.Offer;
import org.sharesquare.model.connector.ConnectorState;
import org.sharesquare.model.connector.ConnectorStateValues;
import org.sharesquare.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Service
public class ConnectorService {

    @Autowired
    IRepository<Connector> connectorRepo;

    @Autowired
    IRepository<ConnectorState> connectorStateRepo;

  //  @Autowired
   // WebClient oauthWebClient;

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
