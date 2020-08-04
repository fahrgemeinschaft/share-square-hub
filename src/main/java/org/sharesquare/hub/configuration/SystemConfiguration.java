package org.sharesquare.hub.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.sharesquare.hub.repository.TargetSystemRepository;
import org.sharesquare.model.Connector;
import org.sharesquare.model.connector.ConnectorState;
import org.sharesquare.repository.IRepository;
import org.sharesquare.repository.SimpleInMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
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

    @Value("${custom.data.example.usage}")
	private boolean useExamples;

    @Autowired
    private ExampleData exampleData;

    @Bean
    IRepository<Connector> createConnectorRepo(){
        return new SimpleInMemoryRepository<Connector>();
    }

    @Bean
    IRepository<ConnectorState> createConnectorStateRepo(){
        return new SimpleInMemoryRepository<ConnectorState>();
    }

	@Bean
	public ApplicationRunner initializer(TargetSystemRepository repository) {
		final List<EntityTargetSystem> exampleTargetSystems = useExamples ?
				exampleData.getEntityTargetSystems() : new ArrayList<>();
		return args -> repository.saveAll(exampleTargetSystems);
	}
}
