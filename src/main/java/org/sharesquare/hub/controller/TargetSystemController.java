package org.sharesquare.hub.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.sharesquare.hub.conversion.TargetSystemConverter;
import org.sharesquare.hub.service.TargetSystemService;
import org.sharesquare.model.TargetSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ApiResponse(responseCode = "401", description = "Wrong client authorization", content = @Content)
@ApiResponse(responseCode = "403", description = "Client not allowed", content = @Content)
@RestController
@RequestMapping("/targetsystems")
public class TargetSystemController {

	private static final Logger log = LoggerFactory.getLogger(TargetSystemController.class);

	@Autowired
	TargetSystemService targetSystemService;

	@Autowired
	private TargetSystemConverter targetSystemConverter;

	@Operation(description = "Add a new TargetSystem using a generated id")
	@ApiResponse(responseCode = "201", description = "Success")
	@ApiResponse(responseCode = "400", description = "Wrong data input", content = @Content)
	@ApiResponse(responseCode = "415", description = "Wrong format", content = @Content)
	@PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<TargetSystem> addTargetSystem(@Valid @RequestBody final TargetSystem targetSystem) {
		log.info("TargetSystem POST request for TargetSystem instance '{}'",
				targetSystemConverter.apiToJSONString(targetSystem));
		final TargetSystem responseTargetSystem = targetSystemService.addTargetSystem(targetSystem);
		return new ResponseEntity<>(responseTargetSystem, CREATED);
	}

	@Operation(description = "Delete a TargetSystem")
	@ApiResponse(responseCode = "204", description = "No content success")
	@ApiResponse(responseCode = "404", description = "TargetSystem doesn't exist")
	@ApiResponse(responseCode = "400", description = "Path variable TargetSystem id is invalid or missing")
	@DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteTargetSystem(@PathVariable final UUID id) {
		log.info("TargetSystem DELETE request for id value '{}'", id);
		if (targetSystemService.deleteTargetSystem(id)) {
			return new ResponseEntity<>(NO_CONTENT);
		}
		return new ResponseEntity<>(NOT_FOUND);
	}

	@Operation(description = "Find all TargetSystems")
	@ApiResponse(responseCode = "200", description = "Success")
	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TargetSystem>> getTargetSystems() {
		log.info("TargetSystem GET all request");
		final List<TargetSystem> targetSystems = targetSystemService.getTargetSystems();
		return ResponseEntity.ok(targetSystems);
	}
}
