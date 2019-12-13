package org.sharesquare.hub.endpoints;


import org.sharesquare.model.Offer;
import org.sharesquare.hub.sanity.OfferSanitizer;

import org.sharesquare.repository.SimpleInMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class Offers {

    @Autowired
    private OfferSanitizer offerSanitizer;

    @Autowired
    private SimpleInMemoryRepository<Offer> offerRepository;


    @GetMapping(path="/offers/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> getById(@PathVariable final String id){
        if(offerSanitizer.sanitizeId(id)) {
            final Offer offer = offerRepository.findById(id);

            return ResponseEntity.ok().body(offer);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PostMapping(path = "/offers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer){

        final Offer result = offerRepository.create(offer);
        return ResponseEntity.ok().body(result);
    }
}
