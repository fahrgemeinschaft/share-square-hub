package org.sharesquare.hub.endpoints;


import org.sharesquare.model.Offer;
import org.sharesquare.repository.SimpleInMemoryRepository;
import org.sharesquare.sanity.IShareSquareSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class Offers {

    @Autowired
    private IShareSquareSanitizer<Offer> offerSanitizer;

    @Autowired
    private SimpleInMemoryRepository<Offer> offerRepository;


    @GetMapping(path="/offers/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> getById(@PathVariable final String id){
        if(offerSanitizer.isIdValid(id)) {
            final Optional<Offer> offer = offerRepository.findById(id);
            if(offer.isPresent()) {
                return ResponseEntity.ok().body(offer.get());
            }else{
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PostMapping(path = "/offers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer){

        final Optional<Offer> result = offerRepository.create(offer);
        if(result.isPresent()) {
            return ResponseEntity.accepted().body(result.get());
        }else{
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/offers",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Offer>> findMany(@RequestParam final Offer search, @RequestParam(required = false) final Pageable page){
        return ResponseEntity.ok(offerRepository.findMany(search, page));
    }

}

