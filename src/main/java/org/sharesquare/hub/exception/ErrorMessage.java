package org.sharesquare.hub.exception;

public final class ErrorMessage {

	private ErrorMessage() {
	}

	public static final String REQUEST_BODY_INVALID = "Invalid request body";

	public static final String REQUEST_BODY_IS_EMPTY = "Required request body is missing";

	public static final String JSON_PARSE_ERROR = "JSON parse error";

	public static final String JSON_INVALID_PROBLEM = "Invalid JSON input";

	public static final String ID_IS_EMPTY = "Required path variable id is missing";

	public static final String USER_ID_IS_EMPTY = "Request parameter userId must not be empty";

	public static final String ID_OR_PARAM_IS_EMPTY = "Required path variable id or query parameter is missing";
}
