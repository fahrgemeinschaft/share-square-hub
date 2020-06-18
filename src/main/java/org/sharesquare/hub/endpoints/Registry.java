package org.sharesquare.hub.endpoints;


import org.sharesquare.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.sharesquare.model.Connector;

import java.util.Optional;

@RestController
public class Registry {

	@Qualifier("createConnectorRepo")
    @Autowired
    IRepository<Connector> connectorRepo;


    @PostMapping(path = "/registry/connector/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Connector> registerConnector(@RequestBody Connector connector){

        final Optional<Connector> result = connectorRepo.create(connector);
        if(result.isPresent()) {
            return ResponseEntity.accepted().body(result.get());
        }else{
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @PostMapping(path = "/registry/connector/unregister", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Connector> unregisterConnector(@RequestBody Connector connector){

        final Optional<Connector> result = connectorRepo.delete(connector);
        if(result.isPresent()) {
            return ResponseEntity.accepted().body(result.get());
        }else{
            return ResponseEntity.unprocessableEntity().build();
        }
    }

}
