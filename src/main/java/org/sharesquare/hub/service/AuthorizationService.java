package org.sharesquare.hub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

	private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

	public String getClientId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String clientId = ((Jwt) auth.getCredentials()).getClaimAsString("clientId");
		log.info("for client id " + clientId);
		return clientId;
	}
}
