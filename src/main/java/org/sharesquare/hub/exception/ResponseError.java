package org.sharesquare.hub.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class ResponseError {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private final OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);

	private int status;

	private String error;

	private String message;

	private String path;

	public ResponseError(HttpStatus status, String message, WebRequest request) {
		this.status = status.value();
		this.error = status.getReasonPhrase();
		this.message = message;
		this.path = ((ServletWebRequest) request).getRequest().getRequestURI();
	}
}
