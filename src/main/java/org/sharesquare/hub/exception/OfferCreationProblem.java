package org.sharesquare.hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OfferCreationProblem extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public OfferCreationProblem(String message) {
		super(message);
	}
}
