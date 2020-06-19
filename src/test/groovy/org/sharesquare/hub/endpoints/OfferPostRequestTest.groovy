package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.sharesquare.hub.endpoints.OfferUtil.userId
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import static org.springframework.http.MediaType.TEXT_XML

import org.sharesquare.model.Offer

import spock.lang.Issue

class OfferPostRequestTest extends RequestSpecification {

	@Issue("#4")
	def "A valid post request should work and return 201"() {
		given:
			def offer = new Offer(userId: '1')

		when:
			// StackOverflowError when using groovy.json.JsonOutput.toJson with java.time.ZoneId
			// https://issues.apache.org/jira/browse/GROOVY-7682
			final response = doPost(offersUri, toJson(offer))

		then:
			resultIs(response, CREATED)

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

	def "A post request with an empty body should respond with status code 400 and a meaningful error message"() {
		given:
			final emptyRequestBody = ''

		when:
			final response = doPost(offersUri, emptyRequestBody)

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'Required request body for Offer is missing'

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST, expectedMessage)
	}

	def "A post request with invalid JSON should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidJson = '{.'

		when:
			final response = doPost(offersUri, invalidJson)

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Invalid request body for Offer. JSON parse error: Unexpected character ('.' (code 46)): was expecting double-quote to start field name"

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST, expectedMessage)
	}

	def "A post request with a wrong field type in the body should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidOffer = [userId: []]

		when:
			final response = doPost(offersUri, toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST)
			// depending on spring boot version
			(responseError.message == "Invalid JSON input for Offer in field 'userId': Cannot deserialize instance of `java.lang.String` out of START_ARRAY token"
				|| responseError.message == "JSON parse error for Offer in field 'userId': Cannot deserialize instance of `java.lang.String` out of START_ARRAY token")
	}

	def "A post request with a not excepted content type should respond with status code 415 and a meaningful error message"() {
		given:
			final offerAsXml = "<offer><$userId>2</$userId></offer>"

		when:
			final response = doPost(offersUri, offerAsXml, TEXT_XML)

		then:
			resultIs(response, UNSUPPORTED_MEDIA_TYPE)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Content type 'text/xml' not supported"

		then:
			resultContentIs(offersUri, responseError, UNSUPPORTED_MEDIA_TYPE, expectedMessage)
	}

	def "A post request with umlaut should work"() {
		given:
			def offer = new Offer(userId: '\u00fc') // ue

		when:
			final response = doUTF8Post(offersUri, toJson(offer))

		then:
			resultIs(response, CREATED, APPLICATION_JSON_UTF8_VALUE)

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

	def "A post request with startTime and startDate should work and have the default startTimezone in the response"() {
		given:
			final offer = [startTime: '08:30',
				           startDate: '2013-12-20']

		when:
			final response = doPost(offersUri, toJson(offer))

		then:
			resultIs(response, CREATED)

		when:
			final responseOffer = fromJson(response.contentAsString)

		then:
			with (responseOffer) {
				startTime               != null
				startTime as String     == offer.startTime
				startDate               != null
				startDate as String     == offer.startDate
				startTimezone           != null
				startTimezone as String == 'Europe/Berlin'
			}
	}

	def "A post request with startTimezone should work"() {
		given:
			final offer = [startTimezone: 'Europe/Paris']

		when:
			final response = doPost(offersUri, toJson(offer))

		then:
			resultIs(response, CREATED)

		when:
			final responseOffer = fromJson(response.contentAsString)

		then:
			responseOffer.startTimezone != null
			responseOffer.startTimezone as String == offer.startTimezone
	}

	def "A post request with an invalid startTime should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidOffer = [startTime: 'x']

		when:
			final response = doPost(offersUri, toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST)
			responseError.message.startsWith("JSON parse error for Offer in field 'startTime'")
	}

	def "A post request with an invalid startDate should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidOffer = [startDate: ',']

		when:
			final response = doPost(offersUri, toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST)
			responseError.message.startsWith("JSON parse error for Offer in field 'startDate'")
	}

	def "A post request with an invalid startTimezone should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidOffer = [startTimezone: '1 7']

		when:
			final response = doPost(offersUri, toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST)
			responseError.message.startsWith("JSON parse error for Offer in field 'startTimezone'")
	}
}
