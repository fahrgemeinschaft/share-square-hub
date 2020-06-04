package org.sharesquare.hub.endpoints

import static org.springframework.http.HttpHeaders.AUTHORIZATION
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

import org.sharesquare.model.Offer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc

import com.fasterxml.jackson.databind.ObjectMapper

import groovyx.net.http.RESTClient
import spock.lang.Specification

@AutoConfigureMockMvc
@WebMvcTest
class RequestSpecification extends Specification {

	@Autowired
	private MockMvc mvc

	@Autowired
	ObjectMapper objectMapper

	@Value("\${auth.server.token.uri}")
	private String tokenUri;

	@Value("\${auth.server.client.id}")
	private String clientId;

	@Value("\${auth.server.client.secret}")
	private String clientSecret;

	@Value("\${auth.server.client.not.in.scope.id}")
	private String clientNotInScopeId;

	@Value("\${auth.server.client.not.in.scope.secret}")
	private String clientNotInScopeSecret;

	protected static final uri = '/offers'

	private static final defaultOffer = '{}'

	def authServerResponse(id = clientId, secret = clientSecret) {
		(new RESTClient(tokenUri))
				.post(body: [client_id:     id,
					         client_secret: secret,
					         grant_type :   'client_credentials'],
					  requestContentType: APPLICATION_FORM_URLENCODED)
	}

	def accessToken() {
		authServerResponse().data.access_token
	}

	def accessTokenNotInScope() {
		authServerResponse(clientNotInScopeId, clientNotInScopeSecret).data.access_token
	}

	def doPost(requestBody, mediaType = APPLICATION_JSON) {
		mvc.perform(
				post(uri)
					.header(AUTHORIZATION, "Bearer ${accessToken()}")
					.contentType(mediaType)
					.content(requestBody)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doUTF8Post(requestBody, mediaType = APPLICATION_JSON) {
		mvc.perform(
				post(uri)
					.header(AUTHORIZATION, "Bearer ${accessToken()}")
					.contentType(mediaType)
					.content(requestBody)
					.accept(APPLICATION_JSON_UTF8)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doPostWithAuthHeaderValue(value) {
		mvc.perform(
				post(uri)
					.header(AUTHORIZATION, value)
					.contentType(APPLICATION_JSON)
					.content(defaultOffer)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doPostWithAuthHeaderKey(key) {
		mvc.perform(
				post(uri)
					.header(key, "Bearer ${accessToken()}")
					.contentType(APPLICATION_JSON)
					.content(defaultOffer)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doPostWithoutAuthHeader() {
		mvc.perform(
				post(uri)
					.contentType(APPLICATION_JSON)
					.content(defaultOffer)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def fromJson(content, valueType = Offer) {
		objectMapper.readValue(content, valueType)
	}

	def toJson(object) {
		objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(object);
	}

	void resultIs(response, httpStatus, mediaType = APPLICATION_JSON_VALUE) {
		with (response) {
			assert status                      == httpStatus.value
			assert errorMessage                == null
			assert headers[CONTENT_TYPE].value == mediaType
			assert contentType                 == mediaType
		}
	}

	void resultContentIs(responseContent, httpStatus, expectedMessage = null) {
		with (responseContent) {
			assert status == httpStatus.value
			assert error  == httpStatus.reasonPhrase
			if (expectedMessage != null) {
				assert message == expectedMessage
			}
			assert path == uri
		}
	}
}
