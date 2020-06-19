package org.sharesquare.hub.exception;

import static org.sharesquare.hub.exception.ErrorMessage.JSON_INVALID_PROBLEM;
import static org.sharesquare.hub.exception.ErrorMessage.JSON_PARSE_ERROR;
import static org.sharesquare.hub.exception.ErrorMessage.OFFER_ID_IS_EMPTY;
import static org.sharesquare.hub.exception.ErrorMessage.OFFER_IS_EMPTY;
import static org.sharesquare.hub.exception.ErrorMessage.OFFER_NOT_VALID;
import static org.sharesquare.hub.exception.ErrorMessage.REQUEST_BODY_IS_EMPTY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

@ControllerAdvice
public class OfferResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(OfferResponseEntityExceptionHandler.class);

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
		String message = ex.getMessage();
		if (ex.getCause() instanceof MismatchedInputException) {
			if (message.startsWith(JSON_INVALID_PROBLEM)
					|| message.startsWith(JSON_PARSE_ERROR)) {
				MismatchedInputException cause = (MismatchedInputException) ex.getCause();
				message = message.startsWith(JSON_INVALID_PROBLEM) ? JSON_INVALID_PROBLEM : JSON_PARSE_ERROR;
				message += " for Offer";
				if (!CollectionUtils.isEmpty(cause.getPath())) {
					message += String.format(" in field '%s'", cause.getPath().get(0).getFieldName());
				}
				message += String.format(": %s", cause.getOriginalMessage());
			}
		} else if (ex.getCause() instanceof InvalidFormatException) {
			InvalidFormatException cause = (InvalidFormatException) ex.getCause();
			message = OFFER_NOT_VALID;
			if (!CollectionUtils.isEmpty(cause.getPath())) {
				message += String.format(" in field '%s'", cause.getPath().get(0).getFieldName());
			}
			message += String.format(": %s", cause.getOriginalMessage());
		} else if (ex.getCause() instanceof JsonParseException) {
			String originalMessage = ((JsonParseException) ex.getCause()).getOriginalMessage();
			message = String.format("%s. %s: %s", OFFER_NOT_VALID, JSON_PARSE_ERROR,
					originalMessage);
		} else if (message.startsWith(REQUEST_BODY_IS_EMPTY)) {
			message = OFFER_IS_EMPTY;
		}
		log.info("Wrong client request (http message not readable): " + message);
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, message, request), BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
		String message = ex.getMessage();
		BindingResult result = ex.getBindingResult();
		if (result != null) {
			List<FieldError> errors = result.getFieldErrors();
			if (!CollectionUtils.isEmpty(errors)) {
				FieldError e = errors.get(0);
				message = String.format("%s Value '%s' not excepted.", e.getDefaultMessage(), e.getRejectedValue());
			}
		}
		log.info("Wrong client request (method argument not valid): " + message);
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, message, request), BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
		String message = ex.getMessage();
		if (ex.getCause() instanceof IllegalArgumentException) {
			message = String.format("Type mismatch for path variable: %s", ((IllegalArgumentException) ex.getCause()).getMessage());
		}
		log.info("Wrong client request (method argument type mismatch): " + message);
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, message, request), BAD_REQUEST);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, WebRequest request) {
		String message = ex.getMessage();
		HttpStatus status = UNSUPPORTED_MEDIA_TYPE;
		if (requestHasEmptyPathVariable(request)) {
			message = OFFER_ID_IS_EMPTY;
			status = BAD_REQUEST;
		}
		log.info("Wrong client request (http media type not supported): " + message);
		return new ResponseEntity<>(new ResponseError(status, message, request), status);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
		String message = ex.getMessage();
		HttpStatus status = METHOD_NOT_ALLOWED;
		if (requestHasEmptyPathVariable(request)) {
			message = OFFER_ID_IS_EMPTY;
			status = BAD_REQUEST;
		}
		log.info("Wrong client request (http request method not supported): " + message);
		return new ResponseEntity<>(new ResponseError(status, message, request), status);
	}

	private boolean requestHasEmptyPathVariable(WebRequest request) {
		HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();
		if ((httpServletRequest.getMethod().equals("GET")
				|| httpServletRequest.getMethod().equals("DELETE"))
				&& httpServletRequest.getRequestURI().equals("/offers/")) {
			return true;
		}
		return false;
	}
}
