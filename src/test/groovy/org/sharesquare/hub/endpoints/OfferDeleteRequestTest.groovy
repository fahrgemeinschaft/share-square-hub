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
			final offer = (example == 1) ? defaultOffer() : exampleOffer()
			final id = fromJson(doPost(offersUri, offer).contentAsString).id
			final response = doDelete("$offersUri/$id")

		then:
			response.status == NO_CONTENT.value

		when:
			final getResponse = doGet("$offersUri/$id")

		then:
			getResponse.status == NOT_FOUND.value
			
		where:
			example | _
			1       | _
			2       | _
	}

	def "A delete request with a not existing id should respond with status code 404"() {
		given:
			final id = UUID.randomUUID()

		when:
			final response = doDelete("$offersUri/$id")

		then:
			response.status == NOT_FOUND.value

		when:
			final getResponse = doGet("$offersUri/$id")

		then:
			getResponse.status == NOT_FOUND.value
	}

	def "A delete request with an invalid UUID or an empty id should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doDelete("$offersUri/$id")

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs("$offersUri/$id", responseError, BAD_REQUEST, expectedMessage)

		when:
			final getResponse = doGet("$offersUri/$id")

		then:
			getResponse.status == BAD_REQUEST.value

		where:
			id       | expectedMessage
			'invalid' | "Type mismatch for path variable: Invalid UUID string: $id"
			''        | 'Required path variable id is missing'
	}
}
