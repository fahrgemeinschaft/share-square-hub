package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

import spock.lang.Issue

@Issue("#14")
class OfferGetRequestTest extends RequestSpecification {

	def "A get request with an existing id should return the corresponding Offer along with status code 200"() {
		when:
			final uuid = fromJson(doPost(offersUri, defaultOffer).contentAsString).id
			final response = doGet("$offersUri/$uuid")

		then:
			resultIs(response, OK)

		when:
			final responseOffer = fromJson(response.contentAsString)

		then:
			responseOffer.id instanceof UUID
			responseOffer.id != null
	}

	def "A get request with a not existing id should respond with status code 404"() {
		given:
			final uuid = UUID.randomUUID()

		when:
			final response = doGet("$offersUri/$uuid")

		then:
			response.status == NOT_FOUND.value
	}

	def "A get request with an invalid UUID or an empty id should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doGet("$offersUri/$uuid")

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs("$offersUri/$uuid", responseError, BAD_REQUEST, expectedMessage)

		where:
			uuid      | expectedMessage
			'invalid' | "Type mismatch for path variable: Invalid UUID string: $uuid"
			''        | 'Required path variable Offer id is missing'
	}
}
