package org.sharesquare.hub.exception;

public final class ErrorMessage {

	private ErrorMessage() {
	}

	public static final String OFFER_NOT_VALID = "Invalid request body for Offer";

	public static final String REQUEST_BODY_IS_EMPTY = "Required request body is missing";

	public static final String OFFER_IS_EMPTY = "Required request body for Offer is missing";

	public static final String JSON_PARSE_ERROR = "JSON parse error";

	public static final String JSON_INVALID_PROBLEM = "Invalid JSON input";

	public static final String OFFER_ID_IS_EMPTY = "Required path variable Offer id is missing";

	public static final String USER_ID_IS_EMPTY = "Request parameter userId must not be empty";

	public static final String ID_OR_USER_ID_IS_EMPTY = "Required path variable id or parameter userId is missing";
}
