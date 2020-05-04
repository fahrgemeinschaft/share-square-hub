package org.sharesquare.hub.exception;

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

@ControllerAdvice
public class OfferResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(OfferResponseEntityExceptionHandler.class);

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
		String message = ex.getMessage();
		if (ex.getCause() instanceof InvalidFormatException) {
			InvalidFormatException cause = (InvalidFormatException) ex.getCause();
			message = ErrorMessage.OFFER_NOT_VALID;
			if (!CollectionUtils.isEmpty(cause.getPath())) {
				String fieldName = cause.getPath().get(0).getFieldName();
				message += String.format(" in field '%s': %s", fieldName, cause.getOriginalMessage());
			} else {
				message += String.format(": %s", cause.getOriginalMessage());
			}
		} else if (ex.getCause() instanceof JsonParseException) {
			String originalMessage = ((JsonParseException) ex.getCause()).getOriginalMessage();
			message = String.format("%s. %s: %s", ErrorMessage.OFFER_NOT_VALID, ErrorMessage.JSON_PARSE_PROBLEM,
					originalMessage);
		} else if (message.startsWith(ErrorMessage.REQUEST_BODY_IS_EMPTY)) {
			message = ErrorMessage.OFFER_IS_EMPTY;
		}
		log.info("Wrong user input: " + message);
		return new ResponseEntity<>(new Response415Error(message, request), Response415Error.STATUS);
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
		return new ResponseEntity<>(new Response415Error(message, request), Response415Error.STATUS);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, WebRequest request) {
		return new ResponseEntity<>(new Response415Error(ex.getMessage(), request), Response415Error.STATUS);
	}
}
