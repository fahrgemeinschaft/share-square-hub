package org.sharesquare.hub.exception;

import static org.sharesquare.hub.exception.ErrorMessage.ID_IS_EMPTY;
import static org.sharesquare.hub.exception.ErrorMessage.ID_OR_PARAM_IS_EMPTY;
import static org.sharesquare.hub.exception.ErrorMessage.JSON_INVALID_PROBLEM;
import static org.sharesquare.hub.exception.ErrorMessage.JSON_PARSE_ERROR;
import static org.sharesquare.hub.exception.ErrorMessage.REQUEST_BODY_INVALID;
import static org.sharesquare.hub.exception.ErrorMessage.REQUEST_BODY_IS_EMPTY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.sharesquare.hub.model.data.EntityTargetSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
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
				if (!CollectionUtils.isEmpty(cause.getPath())) {
					Reference r = cause.getPath().get(0);
					message += String.format(" for %s in field '%s'", r.getFrom().getClass().getSimpleName(), r.getFieldName());
				}
				message += String.format(": %s", cause.getOriginalMessage());
			}
		} else if (ex.getCause() instanceof InvalidFormatException) {
			InvalidFormatException cause = (InvalidFormatException) ex.getCause();
			message = REQUEST_BODY_INVALID;
			if (!CollectionUtils.isEmpty(cause.getPath())) {
				message += String.format(" in field '%s'", cause.getPath().get(0).getFieldName());
			}
			message += String.format(": %s", cause.getOriginalMessage());
		} else if (ex.getCause() instanceof JsonParseException) {
			String originalMessage = ((JsonParseException) ex.getCause()).getOriginalMessage();
			message = String.format("%s. %s: %s", REQUEST_BODY_INVALID, JSON_PARSE_ERROR,
					originalMessage);
		} else if (message.startsWith(REQUEST_BODY_IS_EMPTY)) {
			message = REQUEST_BODY_IS_EMPTY;
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
				message = String.format("%s. Value '%s' for %s not excepted.", e.getDefaultMessage(), e.getRejectedValue(), e.getField());
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
		if (requestHasEmptyPathVariable(request) != null) {
			message = ID_IS_EMPTY;
			status = BAD_REQUEST;
		}
		log.info("Wrong client request (http media type not supported): " + message);
		return new ResponseEntity<>(new ResponseError(status, message, request), status);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
		String message = ex.getMessage();
		HttpStatus status = METHOD_NOT_ALLOWED;
		if (requestHasEmptyPathVariable(request) != null) {
			message = ID_IS_EMPTY;
			status = BAD_REQUEST;
		}
		log.info("Wrong client request (http request method not supported): " + message);
		return new ResponseEntity<>(new ResponseError(status, message, request), status);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			WebRequest request) {
		String message = ex.getMessage();
		String method = requestHasEmptyPathVariable(request);
		if (method != null && method.equals("GET")) {
			message = ID_OR_PARAM_IS_EMPTY;
		}
		log.info("Wrong client request (missing servlet request parameter): " + message);
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, message, request), BAD_REQUEST);
	}

	@ExceptionHandler(OfferValidationProblem.class)
	public ResponseEntity<Object> handleOfferValidationProblem(OfferValidationProblem ex, WebRequest request) {
		log.info("Wrong client request (Offer validation problem): " + ex.getMessage());
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, ex.getMessage(), request), BAD_REQUEST);
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<Object> handleTransactionSystemProblem(TransactionSystemException ex, WebRequest request) {
		String message = ex.getMessage();
		Throwable e = ex.getMostSpecificCause();
		if (e instanceof ConstraintViolationException) {
			Set<ConstraintViolation<?>> set = ((ConstraintViolationException) e).getConstraintViolations();
			if (set != null) {
				List<ConstraintViolation<?>> violations = new ArrayList<>();
				violations.addAll(set);
				if (violations.size() > 0) {
					message = violations.get(0).getMessage();
				}
			}
		}
		log.info("Wrong client request (transaction system problem): " + message);
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, message, request), BAD_REQUEST);
	}

	private static final Pattern TARGET_SYSTEM_ID_PATTERN = Pattern.compile("\\(X\\'[a-z0-9]{32}\\'\\)");

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
		String message = ex.getMessage();
		if (message.contains("insert into offer_target_system_membership")) {
			Matcher m = TARGET_SYSTEM_ID_PATTERN.matcher(message);
			if (m.find()) {
				String id = message.substring(m.start() + 3, m.end() - 2);
				id = id.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
				message = "Target system for id '" + id + "' doesn't exist";
			}
		}
		log.info("Wrong client request (data integrity violation): " + message);
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, message, request), BAD_REQUEST);
	}

	@ExceptionHandler(JpaObjectRetrievalFailureException.class)
	public ResponseEntity<Object> handleJpaObjectRetrievalFailure(JpaObjectRetrievalFailureException ex,
			WebRequest request) {
		String message = ex.getMessage();
		Throwable e = ex.getMostSpecificCause();
		if (e instanceof EntityNotFoundException) {
			String moreSpecificMessage = e.getMessage();
			if (moreSpecificMessage.contains(" " + EntityTargetSystem.class.getName() + " ")) {
				message = moreSpecificMessage.replaceFirst(
						" " + EntityTargetSystem.class.getName().replaceAll("\\.", "\\\\.") + " ", " target system ");
			}
		}
		log.info("Wrong client request (jpa object retrieval failure): " + message);
		return new ResponseEntity<>(new ResponseError(BAD_REQUEST, message, request), BAD_REQUEST);
	}

	private String requestHasEmptyPathVariable(WebRequest request) {
		HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();
		String method = httpServletRequest.getMethod();
		if ((method.equals("GET")
				|| method.equals("PUT")
				|| method.equals("DELETE"))
				&& httpServletRequest.getRequestURI().trim().matches("\\/(offer|targetsystem)s\\/?")) {
			return method;
		}
		return null;
	}
}
