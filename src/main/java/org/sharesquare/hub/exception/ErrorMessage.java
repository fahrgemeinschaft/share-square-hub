package org.sharesquare.hub.exception;

public final class ErrorMessage {

	private ErrorMessage() {
	}

	public static final String OFFER_NOT_VALID = "Invalid request body for Offer";

	public static final String REQUEST_BODY_IS_EMPTY = "Required request body is missing";

	public static final String OFFER_IS_EMPTY = "Required request body for Offer is missing";

	public static final String JSON_PARSE_PROBLEM = "Could not parse JSON";
}
