package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT

import spock.lang.Issue

@Issue("#11")
class OfferDeleteRequestTest extends RequestSpecification {

	def "A delete request with an existing id should work and respond with status code 204"() {
		when:
			final uuid = fromJson(doPost(offersUri, defaultOffer).contentAsString).id
			final response = doDelete("$offersUri/$uuid")

		then:
			response.status == NO_CONTENT.value

		when:
			final getResponse = doGet("$offersUri/$uuid")

		then:
			getResponse.status == NOT_FOUND.value
	}

	def "A delete request with a not existing id should respond with status code 404"() {
		given:
			final uuid = UUID.randomUUID()

		when:
			final response = doDelete("$offersUri/$uuid")

		then:
			response.status == NOT_FOUND.value

		when:
			final getResponse = doGet("$offersUri/$uuid")

		then:
			getResponse.status == NOT_FOUND.value
	}

	def "A delete request with an invalid UUID or an empty id should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doDelete("$offersUri/$uuid")

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs("$offersUri/$uuid", responseError, BAD_REQUEST, expectedMessage)

		when:
			final getResponse = doGet("$offersUri/$uuid")

		then:
			getResponse.status == BAD_REQUEST.value

		where:
			uuid      | expectedMessage
			'invalid' | "Type mismatch for path variable: Invalid UUID string: $uuid"
			''        | 'Required path variable Offer id is missing'
	}
}
