package org.sharesquare.hub.endpoints;


import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.sharesquare.hub.exception.OfferCreationProblem;
import org.sharesquare.hub.service.OfferService;
import org.sharesquare.model.Offer;
import org.sharesquare.repository.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

@ApiResponse(responseCode = "401", description = "Wrong client authorization", content = @Content)
@ApiResponse(responseCode = "403", description = "Client not allowed", content = @Content)
@RestController
@RequestMapping("/offers")
public class Offers {
	
    private static final Logger log = LoggerFactory.getLogger(Offers.class);

    @Autowired
    OfferService offerService;
    //TODO: use offerservice, remove offerRepository

    @Autowired
    private IRepository<Offer> offerRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Operation(description = "Get Offer by id")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Offer doesn't exist", content = @Content)
    @ApiResponse(responseCode = "400", description = "Path variable Offer id is invalid or missing", content = @Content)
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> getOffer(@PathVariable final UUID id) {
    	final Offer offer = offerService.getOffer(id);
    	if (offer != null) {
    		return ResponseEntity.ok(offer);
    	}
    	return new ResponseEntity<>(NOT_FOUND);
    }

    @Operation(description = "Add a new Offer using a generated id")
    @ApiResponse(responseCode = "201", description = "Success")
    @ApiResponse(responseCode = "400", description = "Wrong data input", content = @Content)
    @ApiResponse(responseCode = "415", description = "Wrong format", content = @Content)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> createOffer(@Valid @RequestBody Offer offer) {

        final Optional<Offer> result = offerRepository.create(offer);
        if(result.isPresent()) {
        	return new ResponseEntity<>(result.get(), HttpStatus.CREATED);
        }else{
        	// should not be reached
        	String message = "There was an unexpected problem while creating the Offer: ";
			try {
				message += objectMapper.writeValueAsString(offer);
			} catch (JsonProcessingException e) {
				log.warn("JSON processing problem: " + e.getMessage());
			}
        	log.error(message);
        	throw new OfferCreationProblem(message);
        }
    }

    @Operation(description = "Update an existing Offer")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Offer doesn't exist")
    @ApiResponse(responseCode = "400", description = "Wrong data input")
    @ApiResponse(responseCode = "415", description = "Wrong format")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateOffer(@PathVariable final UUID id,
    		                                @Valid @RequestBody Offer offer) {
    	if (offerService.updateOffer(id, offer)) {
    		return ResponseEntity.ok(null);
    	}
    	return new ResponseEntity<>(NOT_FOUND);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Offer>> findMany(@RequestParam final String search,
    		@PageableDefault(page = 0, size = 50) final Pageable pageable) {
		try {
			Offer searchOffer = objectMapper.readValue(search, Offer.class);
			return ResponseEntity.ok(offerRepository.findMany(searchOffer, pageable));
		} catch (JsonProcessingException e) {
			log.info("Cannot convert user search to offer object: ", e);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @Operation(description = "Delete an Offer")
    @ApiResponse(responseCode = "204", description = "No content success")
    @ApiResponse(responseCode = "404", description = "Offer doesn't exist")
    @ApiResponse(responseCode = "400", description = "Path variable Offer id is invalid or missing")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteOffer(@PathVariable final UUID id) {
    	if (offerService.deleteOffer(id)) {
    		return new ResponseEntity<>(NO_CONTENT);
    	}
    	return new ResponseEntity<>(NOT_FOUND);
    }
}
