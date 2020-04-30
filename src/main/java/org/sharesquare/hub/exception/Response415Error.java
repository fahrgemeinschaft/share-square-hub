package org.sharesquare.hub.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class Response415Error {

	protected static final HttpStatus STATUS = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private final OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);

	private final int status = STATUS.value();

	private final String error = STATUS.getReasonPhrase();

	private String message;

	private String path;

	public Response415Error(String message, WebRequest request) {
		this.message = message;
		this.path = ((ServletWebRequest) request).getRequest().getRequestURI();
	}
}
