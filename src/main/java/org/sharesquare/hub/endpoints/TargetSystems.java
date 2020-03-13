package org.sharesquare.hub.endpoints;


import org.sharesquare.model.TargetSystem;
import org.sharesquare.repository.SimpleInMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Collection;

@RestController
public class TargetSystems {

    @Autowired
    private SimpleInMemoryRepository<TargetSystem> targetSystemsRepository;


    @GetMapping(path="/targetsystems", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<TargetSystem>> getTargetSystems() {
        return ResponseEntity.ok(targetSystemsRepository.getAll());
    }

}
