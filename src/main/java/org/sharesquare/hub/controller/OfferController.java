package org.sharesquare.hub.controller;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;
import static org.sharesquare.hub.exception.ErrorMessage.USER_ID_IS_EMPTY;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.UUID;

import javax.validation.Valid;

import org.sharesquare.hub.conversion.OfferConverter;
import org.sharesquare.hub.exception.OfferValidationProblem;
import org.sharesquare.hub.service.OfferService;
import org.sharesquare.model.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sun.xml.bind.v2.TODO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ApiResponse(responseCode = "401", description = "Wrong client authorization", content = @Content)
@ApiResponse(responseCode = "403", description = "Client not allowed", content = @Content)
@RestController
@RequestMapping("/offers")
public class OfferController {

    private static final Logger log = LoggerFactory.getLogger(OfferController.class);

    @Autowired
    OfferService offerService;

    @Autowired
    private OfferConverter offerConverter;

    @Operation(description = "Get Offer by id")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Offer doesn't exist", content = @Content)
    @ApiResponse(responseCode = "400", description = "Path variable Offer id is invalid or missing", content = @Content)
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> getOffer(@PathVariable final UUID id) {
    	log.info("Offer GET request for id value '{}'", id);
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
    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Offer> addOffer(@Valid @RequestBody final Offer offer) {
    	log.info("Offer POST request for Offer instance '{}'", offerConverter.apiToJSONString(offer));
    	final Offer responseOffer = offerService.addOffer(offer);
    	return new ResponseEntity<>(responseOffer, CREATED);
    }

    @Operation(description = "Update an existing Offer")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Offer doesn't exist")
    @ApiResponse(responseCode = "400", description = "Wrong data input")
    @ApiResponse(responseCode = "415", description = "Wrong format")
    @PutMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateOffer(@PathVariable final UUID id,
    		                                @Valid @RequestBody final Offer offer) {
    	log.info("Offer PUT request for id value '{}' and Offer instance '{}'",
    			id, offerConverter.apiToJSONString(offer));
    	if (offerService.updateOffer(id, offer)) {
    		return ResponseEntity.ok(null);
    	}
    	return new ResponseEntity<>(NOT_FOUND);
    }

    @Operation(description = "Find all Offers for a given userId")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "400", description = "Request parameter userId is missing or empty", content = @Content)
    @Parameters({
    	@Parameter(in = QUERY, name = "page", schema = @Schema(type = "string")),
    	@Parameter(in = QUERY, name = "size", schema = @Schema(type = "string")),
    	@Parameter(in = QUERY, name = "sort", content = @Content(array = @ArraySchema(schema = @Schema(type = "string")))),
    	@Parameter(in = QUERY, name = "pageable", hidden = true)
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Offer>> getOffers(
			@RequestParam final String userId,
			@PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
					@SortDefault(sort = "startDate", direction = Sort.Direction.ASC),
					@SortDefault(sort = "startTime", direction = Sort.Direction.ASC)}) final Pageable pageable) {
    	TODO.checkSpec("https://github.com/fahrgemeinschaft/share-square-hub/issues/24");
    	log.info("Offer GET request for userId value '{}' and Pageable instance: {}", userId, pageable);
    	if (userId.trim().length() > 0) {
    		final Page<Offer> offers = offerService.getOffers(userId, pageable);
    		return ResponseEntity.ok(offers);
    	}
    	throw new OfferValidationProblem(USER_ID_IS_EMPTY);
    }

    @Operation(description = "Delete an Offer")
    @ApiResponse(responseCode = "204", description = "No content success")
    @ApiResponse(responseCode = "404", description = "Offer doesn't exist")
    @ApiResponse(responseCode = "400", description = "Path variable Offer id is invalid or missing")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteOffer(@PathVariable final UUID id) {
    	log.info("Offer DELETE request for id value '{}'", id);
    	if (offerService.deleteOffer(id)) {
    		return new ResponseEntity<>(NO_CONTENT);
    	}
    	return new ResponseEntity<>(NOT_FOUND);
    }
}
