package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.defaultOffer
import static org.springframework.http.HttpHeaders.AUTHORIZATION
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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

	@Value("\${custom.auth.server.token.uri}")
	private String tokenUri;

	@Value("\${custom.auth.server.client.id}")
	private String clientId;

	@Value("\${custom.auth.server.client.secret}")
	private String clientSecret;

	@Value("\${custom.auth.server.client.not.in.scope.id}")
	private String clientNotInScopeId;

	@Value("\${custom.auth.server.client.not.in.scope.secret}")
	private String clientNotInScopeSecret;

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

	def doPost(uri, requestBody, mediaType = APPLICATION_JSON) {
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

	def doUTF8Post(uri, requestBody, mediaType = APPLICATION_JSON) {
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

	def doPostWithAuthHeaderValue(uri, value) {
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

	def doPostWithAuthHeaderKey(uri, key) {
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

	def doPostWithoutAuthHeader(uri) {
		mvc.perform(
				post(uri)
					.contentType(APPLICATION_JSON)
					.content(defaultOffer)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doGet(uri) {
		mvc.perform(
				get(uri)
					.header(AUTHORIZATION, "Bearer ${accessToken()}")
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doGet(uri, name1, value1, name2 = ' ', value2 = '', name3 = ' ', value3 = '') {
		mvc.perform(
				get(uri)
					.header(AUTHORIZATION, "Bearer ${accessToken()}")
					.param(name1, value1)
					.param(name2, value2)
					.param(name3, value3)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doUTF8Get(uri) {
		mvc.perform(
				get(uri)
					.header(AUTHORIZATION, "Bearer ${accessToken()}")
					.accept(APPLICATION_JSON_UTF8)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doPut(uri, requestBody, mediaType = APPLICATION_JSON) {
		mvc.perform(
				put(uri)
					.header(AUTHORIZATION, "Bearer ${accessToken()}")
					.contentType(mediaType)
					.content(requestBody)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doUTF8Put(uri, requestBody, mediaType = APPLICATION_JSON) {
		mvc.perform(
				put(uri)
					.header(AUTHORIZATION, "Bearer ${accessToken()}")
					.contentType(mediaType)
					.content(requestBody)
					.accept(APPLICATION_JSON_UTF8)
			)
			.andDo(print())
			.andReturn()
			.response
	}

	def doDelete(uri) {
		mvc.perform(
				delete(uri)
					.header(AUTHORIZATION, "Bearer ${accessToken()}")
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

	def replaceEmptyListsByNull(offer) {
		if (offer.contactOptions == []) {
			offer.contactOptions = null
		}
		if (offer.targetPlatforms == []) {
			offer.targetPlatforms = null
		}
		if (offer.preferences == []) {
			offer.preferences = null
		}
		return offer
	}

	void resultIs(response, httpStatus, mediaType = APPLICATION_JSON_VALUE) {
		with (response) {
			assert status                      == httpStatus.value
			assert errorMessage                == null
			assert headers[CONTENT_TYPE].value == mediaType
			assert contentType                 == mediaType
		}
	}

	void resultContentIs(uri, responseContent, httpStatus, expectedMessage = null) {
		with (responseContent) {
			assert status == httpStatus.value
			assert error  == httpStatus.reasonPhrase
			if (expectedMessage != null) {
				assert message == expectedMessage
			}
			assert path == uri
		}
	}

	void contentSizeIs(response, size) {
		assert response != null
		assert response.content != null
		assert response.content.size() == size
	}
}
