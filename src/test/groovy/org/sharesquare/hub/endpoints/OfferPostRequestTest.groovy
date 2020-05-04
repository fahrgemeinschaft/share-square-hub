package org.sharesquare.hub.endpoints

import static groovy.json.JsonOutput.toJson
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.http.MediaType.TEXT_XML
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

import org.sharesquare.model.Offer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc

import com.fasterxml.jackson.databind.ObjectMapper

import spock.lang.Issue
import spock.lang.Specification

@AutoConfigureMockMvc
@WebMvcTest
class OfferPostRequestTest extends Specification {

	@Autowired
	private MockMvc mvc

	@Autowired
	ObjectMapper objectMapper

	static final uri = '/offers'
	
	static final userId = 'userId'

	@Issue("#4")
	def "A valid post request should work and return 201"() {
		given:
			def offer = new Offer(userId: '1')

		when:
			final response = doPost(toJson(offer))

		then:
			with (response) {
				status                      == CREATED.value
				errorMessage                == null
				headers[CONTENT_TYPE].value == APPLICATION_JSON_VALUE
				contentType                 == APPLICATION_JSON_VALUE
		}

		when:
			final responseOffer = fromJson(response.contentAsString)

		then:
			responseOffer.id instanceof UUID
			responseOffer.id != null

		when:
			offer.id = responseOffer.id

		then:
			responseOffer == offer
	}

	def "A post request with an empty body should respond with status code 415 and a meaningful error message"() {
		given:
			final emptyRequestBody = ''

		when:
			final response = doPost(emptyRequestBody)

		then:
			with (response) {
				status                      == UNSUPPORTED_MEDIA_TYPE.value
				errorMessage                == null
				headers[CONTENT_TYPE].value == APPLICATION_JSON_VALUE
				contentType                 == APPLICATION_JSON_VALUE
		}

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			with (responseError) {
				status  == UNSUPPORTED_MEDIA_TYPE.value
				error   == UNSUPPORTED_MEDIA_TYPE.reasonPhrase
				message == 'Required request body for Offer is missing'
				path    == uri
			}
	}

	def "A post request with invalid JSON should respond with status code 415 and a meaningful error message"() {
		given:
			final invalidJson = '{.'

		when:
			final response = doPost(invalidJson)

		then:
			with (response) {
				status                      == UNSUPPORTED_MEDIA_TYPE.value
				errorMessage                == null
				headers[CONTENT_TYPE].value == APPLICATION_JSON_VALUE
				contentType                 == APPLICATION_JSON_VALUE
		}

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			with (responseError) {
				status  == UNSUPPORTED_MEDIA_TYPE.value
				error   == UNSUPPORTED_MEDIA_TYPE.reasonPhrase
				message.startsWith('Invalid request body for Offer. Could not parse JSON: Unexpected character')
				path    == uri
			}
	}

	def "A post request with a wrong field type in the body should respond with status code 415 and a meaningful error message"() {
		given:
			final invalidOffer = [userId: []]

		when:
			final response = doPost(toJson(invalidOffer))

		then:
			with (response) {
				status                      == UNSUPPORTED_MEDIA_TYPE.value
				errorMessage                == null
				headers[CONTENT_TYPE].value == APPLICATION_JSON_VALUE
				contentType                 == APPLICATION_JSON_VALUE
		}

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			with (responseError) {
				status  == UNSUPPORTED_MEDIA_TYPE.value
				error   == UNSUPPORTED_MEDIA_TYPE.reasonPhrase
				// depending on spring boot version
				(message.startsWith('JSON parse error: Cannot deserialize instance of')
					|| message.startsWith('Invalid JSON input: Cannot deserialize instance of'))
				path    == uri
			}
	}

	def "A post request with a not excepted content type should respond with status code 415 and a meaningful error message"() {
		given:
			final offerAsXml = "<offer><$userId>2</$userId></offer>"

		when:
			final response = doPost(offerAsXml, TEXT_XML)

		then:
			with (response) {
				status                      == UNSUPPORTED_MEDIA_TYPE.value
				errorMessage                == null
				headers[CONTENT_TYPE].value == APPLICATION_JSON_VALUE
				contentType                 == APPLICATION_JSON_VALUE
		}

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			with (responseError) {
				status  == UNSUPPORTED_MEDIA_TYPE.value
				error   == UNSUPPORTED_MEDIA_TYPE.reasonPhrase
				message == "Content type 'text/xml' not supported"
				path    == uri
			}
	}

	def "A post request with umlaut should work"() {
		given:
			def offer = new Offer(userId: '\u00fc') // ue

		when:
			final response = mvc.perform(
					post(uri)
						.contentType(APPLICATION_JSON)
						.content(toJson(offer))
						.accept(APPLICATION_JSON_UTF8)
				).andDo(print())
				.andReturn()
				.response

		then:
			with (response) {
				status                      == CREATED.value
				errorMessage                == null
				headers[CONTENT_TYPE].value == APPLICATION_JSON_UTF8_VALUE
				contentType                 == APPLICATION_JSON_UTF8_VALUE
		}

		when:
			final responseOffer = fromJson(response.contentAsString)

		then:
			responseOffer.id instanceof UUID
			responseOffer.id != null

		when:
			offer.id = responseOffer.id

		then:
			responseOffer == offer
	}

	private def doPost(requestBody, mediaType = APPLICATION_JSON) {
		mvc.perform(
			post(uri)
				.contentType(mediaType)
				.content(requestBody)
		).andDo(print())
		.andReturn()
		.response
	}

	private def fromJson(content, valueType = Offer) {
		objectMapper.readValue(content, valueType)
	}
}
