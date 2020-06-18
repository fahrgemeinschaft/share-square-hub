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
import org.sharesquare.sanity.IShareSquareSanitizer;
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
public class Offers {
	
    private static final Logger log = LoggerFactory.getLogger(Offers.class);

    @Autowired
    private IShareSquareSanitizer<Offer> offerSanitizer;

    @Autowired
    OfferService offerService;
    //TODO: use offerservice, remove offerRepository

    @Autowired
    private IRepository<Offer> offerRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Operation(description = "Returns the offer with the given ID")
    @ApiResponse(description = "Successful operation", responseCode = "200")
    @ApiResponse(description = "Malformed ID", responseCode = "400", content = @Content)
    @ApiResponse(description = "Not existing ID", responseCode = "404", content = @Content)
    @GetMapping(path="/offers/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> getById(@PathVariable final String id){
        if(offerSanitizer.isIdValid(id)) {
            final Optional<Offer> offer = offerRepository.findById(id);
            if(offer.isPresent()) {
                return ResponseEntity.ok(offer.get());
            }else{
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @Operation(description = "Add a new Offer")
    @ApiResponse(responseCode = "201", description = "Success")
    @ApiResponse(responseCode = "400", description = "Wrong data input", content = @Content)
    @ApiResponse(responseCode = "415", description = "Wrong format", content = @Content)
    @PostMapping(path = "/offers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @Operation(description = "Update an Offer")
    @ApiResponse(description = "Successful operation", responseCode = "202")
    @ApiResponse(description = "Malformed Data", responseCode = "422", content = @Content)
    @ApiResponse(description = "Entity Not Found", responseCode = "404", content = @Content)
    @PutMapping(path = "/offers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> updateOffer(@RequestBody Offer offer){

        final Optional<Offer> result = offerRepository.update(offer);
        if(result.isPresent()) {
            return ResponseEntity.accepted().body(result.get());
        }else{
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/offers",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
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
    @DeleteMapping(path = "/offers/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable final UUID id) {
    	if (offerService.deleteOffer(id)) {
    		return new ResponseEntity<>(NO_CONTENT);
    	}
    	return new ResponseEntity<>(NOT_FOUND);
    }
}
