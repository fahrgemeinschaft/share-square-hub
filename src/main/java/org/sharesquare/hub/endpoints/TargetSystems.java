package org.sharesquare.hub.endpoints;


import org.sharesquare.model.Connector;
import org.sharesquare.model.TargetSystem;
import org.sharesquare.repository.IRepository;
import org.sharesquare.repository.SimpleInMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TargetSystems {

    @Autowired
    IRepository<Connector> connectorRepo;


    @GetMapping(path="/targetsystems", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<TargetSystem>> getTargetSystems() {
        List<TargetSystem> targetSystems =
                null;//connectorRepo.getAll().stream().map(c->c.getTargetSystem()).collect(Collectors.toList());
        return ResponseEntity.ok(targetSystems);
    }

}
