package org.sharesquare.hub.exception;

import static org.sharesquare.hub.exception.ErrorMessage.JSON_INVALID_PROBLEM;
import static org.sharesquare.hub.exception.ErrorMessage.JSON_PARSE_ERROR;
import static org.sharesquare.hub.exception.ErrorMessage.OFFER_IS_EMPTY;
import static org.sharesquare.hub.exception.ErrorMessage.OFFER_NOT_VALID;
import static org.sharesquare.hub.exception.ErrorMessage.REQUEST_BODY_IS_EMPTY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

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
			if (message.startsWith(JSON_INVALID_PROBLEM)) {
				MismatchedInputException cause = (MismatchedInputException) ex.getCause();
				message = String.format("%s for Offer", JSON_INVALID_PROBLEM);
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
		log.info("Wrong user input: " + message);
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
		log.info("Wrong user input: " + message);
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, message, request), BAD_REQUEST);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, WebRequest request) {
		log.info("Wrong user input: " + ex.getMessage());
		return new ResponseEntity<>(new ResponseError(UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), request), UNSUPPORTED_MEDIA_TYPE);
	}
}
