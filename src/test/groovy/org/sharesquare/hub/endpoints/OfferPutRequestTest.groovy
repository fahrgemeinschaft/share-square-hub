package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.sharesquare.hub.endpoints.OfferUtil.userId
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import static org.springframework.http.MediaType.TEXT_XML

import org.sharesquare.model.Offer

import spock.lang.Issue

@Issue("#16")
class OfferPutRequestTest extends RequestSpecification {
	
	def existingId
	
	def existingId() {
		existingId != null ? existingId : fromJson(doPost(offersUri, defaultOffer).contentAsString).id
	}

	def "A valid put request should work and return 200"() {
		given:
			def updateOffer = new Offer(id: existingId(),
				                        userId: '3')

		when:
			final response = doPut("$offersUri/$updateOffer.id", toJson(updateOffer))

		then:
			response.status == OK.value

		when:
			final getResponse = doGet("$offersUri/$updateOffer.id")

		then:
			fromJson(getResponse.contentAsString) == updateOffer
	}

	def "A put request with a not existing id should respond with status code 404"() {
		given:
			final id = 'f52c1ce2-70d9-4917-a0dc-a0a12a58396e'

		when:
			final response = doPut("$offersUri/$id", defaultOffer)

		then:
			response.status == NOT_FOUND.value


		when:
			final getResponse = doGet("$offersUri/$id")

		then:
			getResponse.status == NOT_FOUND.value
	}

	def "A put request with an invalid UUID or an empty id should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doPut("$offersUri/$id", defaultOffer)

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs("$offersUri/$id", responseError, BAD_REQUEST, expectedMessage)

		where:
			id        | expectedMessage
			'corrupt' | "Type mismatch for path variable: Invalid UUID string: $id"
			''        | 'Required path variable Offer id is missing'
	}

	def "A put request with an empty body should respond with status code 400 and a meaningful error message"() {
		given:
			final emptyRequestBody = ''

		when:
			final id = existingId()
			final response = doPut("$offersUri/$id", emptyRequestBody)

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'Required request body for Offer is missing'

		then:
			resultContentIs("$offersUri/$id", responseError, BAD_REQUEST, expectedMessage)
	}

	def "A put request with invalid JSON should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidJson = '{]}'

		when:
			final id = existingId()
			final response = doPut("$offersUri/$id", invalidJson)

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Invalid request body for Offer. JSON parse error: Unexpected close marker ']': expected '}' (for Object starting at [Source: (PushbackInputStream); line: 1, column: 1])"

		then:
			resultContentIs("$offersUri/$id", responseError, BAD_REQUEST, expectedMessage)
	}

	def "A put request with a wrong field type in the body should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidOffer = [origin: 'string']

		when:
			final id = existingId()
			final response = doPut("$offersUri/$id", toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs("$offersUri/$id", responseError, BAD_REQUEST)
			responseError.message == "JSON parse error for Offer in field 'origin': Cannot construct instance of `org.sharesquare.model.Location` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('string')"
	}

	def "A put request with a not excepted content type should respond with status code 415 and a meaningful error message"() {
		given:
			final offerAsXml = "<offer><$userId>4</$userId></offer>"

		when:
			final id = existingId()
			final response = doPut("$offersUri/$id", offerAsXml, TEXT_XML)

		then:
			resultIs(response, UNSUPPORTED_MEDIA_TYPE)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Content type 'text/xml' not supported"

		then:
			resultContentIs("$offersUri/$id", responseError, UNSUPPORTED_MEDIA_TYPE, expectedMessage)
	}

	def "A put request with umlaut should work"() {
		given:
			def updateOffer = new Offer(id: existingId(),
				                        userId: '\u00c4') // Ae

		when:
			final response = doUTF8Put("$offersUri/$updateOffer.id", toJson(updateOffer))

		then:
			response.status == OK.value

		when:
			final getResponse = doUTF8Get("$offersUri/$updateOffer.id")

		then:
			fromJson(getResponse.contentAsString) == updateOffer
	}

	def "A put request with startTime and startDate should work and have the default startTimezone in the response"() {
		given:
			final addOffer = [startTimezone: 'Pacific/Auckland']
			final updateOffer = [startTime: '10:40',
				                 startDate: '2016-07-01']

		when:
			final id = fromJson(doPost(offersUri, toJson(addOffer)).contentAsString).id
			updateOffer.id = id
			final response = doPut("$offersUri/$updateOffer.id", toJson(updateOffer))

		then:
			response.status == OK.value

		when:
			final getResponse = doGet("$offersUri/$updateOffer.id")
			final responseOffer = fromJson(getResponse.contentAsString)

		then:
			with (responseOffer) {
				id                      != null
				id                      == updateOffer.id
				startTime               != null
				startTime as String     == updateOffer.startTime
				startDate               != null
				startDate as String     == updateOffer.startDate
				startTimezone           != null
				startTimezone as String == 'Europe/Berlin'
			}
	}

	def "A put request with startTimezone should work"() {
		given:
			final offer = [startTimezone: 'America/Argentina/Buenos_Aires']

		when:
			final id = existingId()
			final response = doPut("$offersUri/$id", toJson(offer))

		then:
			response.status == OK.value
			
		when:
			final getResponse = doGet("$offersUri/$id")
			final responseOffer = fromJson(getResponse.contentAsString)

		then:
			responseOffer.startTimezone != null
			responseOffer.startTimezone as String == offer.startTimezone
	}

	def "A put request with an invalid startTime or startDate or startTimezone should respond with status code 400 and a meaningful error message"() {
		when:
			final id = existingId()
			final response = doPut("$offersUri/$id", toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs("$offersUri/$id", responseError, BAD_REQUEST)
			responseError.message.startsWith("JSON parse error for Offer in field '${(invalidOffer as Map).keySet()[0]}")

		where:
			invalidOffer                      | _
			[startTime: '1:1']                | _
			[startDate: '2017-5-6']           | _
			[startTimezone: 'Europe/Rostock'] | _
	}
}
