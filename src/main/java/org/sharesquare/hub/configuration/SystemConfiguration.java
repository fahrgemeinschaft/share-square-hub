package org.sharesquare.hub.configuration;

import lombok.Data;
import org.sharesquare.commons.sanity.OfferSanitizer;
import org.sharesquare.model.Connector;
import org.sharesquare.model.Offer;
import org.sharesquare.model.connector.ConnectorState;
import org.sharesquare.repository.IRepository;
import org.sharesquare.repository.SimpleInMemoryRepository;
import org.sharesquare.sanity.IShareSquareSanitizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("system")
@Validated
@Data
public class SystemConfiguration {

    private String appName;



    @Bean
    IRepository<Offer> createOfferRepo(){
        return new SimpleInMemoryRepository<Offer>();
    }

    @Bean
    IRepository<Connector> createConnectorRepo(){
        return new SimpleInMemoryRepository<Connector>();
    }

    @Bean
    IRepository<ConnectorState> createConnectorStateRepo(){
        return new SimpleInMemoryRepository<ConnectorState>();
    }

    @Bean
    IShareSquareSanitizer<Offer> createOfferSanitizer(){
        return new OfferSanitizer();
    }
}
