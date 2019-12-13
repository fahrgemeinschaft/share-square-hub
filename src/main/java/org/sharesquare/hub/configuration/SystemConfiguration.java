package org.sharesquare.hub.configuration;

import lombok.Data;
import org.sharesquare.model.Offer;
import org.sharesquare.repository.SimpleInMemoryRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties("system")
@Validated
@Data
public class SystemConfiguration {

    private String appName;


    @Bean
    SimpleInMemoryRepository<Offer> createOfferRepo(){
        return new SimpleInMemoryRepository<Offer>();
    }
}
