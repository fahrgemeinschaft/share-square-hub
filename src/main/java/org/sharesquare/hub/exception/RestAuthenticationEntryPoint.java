package org.sharesquare.hub.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger log = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

	@Autowired
	private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
    	response.setContentType(APPLICATION_JSON_VALUE);
    	response.setStatus(UNAUTHORIZED.value());
    	String message = e.getMessage();
    	if (e instanceof InsufficientAuthenticationException) {
    		message = String.format("Insufficient authentication: %s", message);
    	}
    	ResponseError error = new ResponseError(UNAUTHORIZED, message, request);
    	objectMapper.writeValue(response.getOutputStream(), error);
        log.info("Authentication problem: {}: {}", e.getClass().getName(), e.getMessage());
    }
}
