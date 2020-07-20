package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.sharesquare.hub.endpoints.OfferUtil.targetSystemIds
import static org.sharesquare.hub.endpoints.OfferUtil.userId
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import static org.springframework.http.MediaType.TEXT_XML

import org.sharesquare.model.Offer

import spock.lang.Issue

class OfferPostRequestTest extends RequestSpecification {

	@Issue("#4,#19")
	def "A valid post request should work and return 201"() {
		when:
			def requestOffer = null
			if (example == 1) {
				requestOffer = "{\"$userId\": \"1\", \"$targetSystemIds\": [\"${targetSystemId1()}\"]}"
			} else {
				requestOffer = exampleOffer()
			}
			// StackOverflowError when using groovy.json.JsonOutput.toJson with java.time.ZoneId
			// https://issues.apache.org/jira/browse/GROOVY-7682
			final response = doPost(offersUri, requestOffer)

		then:
			resultIs(response, CREATED)

		when:
			final responseOffer = fromJson(response.contentAsString)
			requestOffer = fromJson(requestOffer)

		then:
			responseOffer.id instanceof UUID
			responseOffer.id != null
			responseOffer == requestOffer

		when:
			final getResponse = doGet("$offersUri/$responseOffer.id")
			def getResponseOffer = fromJson(getResponse.contentAsString)
			getResponseOffer = replaceEmptyListsByNull(getResponseOffer)

		then:
			getResponseOffer.id == responseOffer.id
			getResponseOffer == requestOffer

		where:
			example | _
			1       | _
			2       | _
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
			final expectedMessage = 'Required request body is missing'

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
			final expectedMessage = "Invalid request body. JSON parse error: Unexpected character ('.' (code 46)): was expecting double-quote to start field name"

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
			(responseError.message == "Invalid JSON input for Offer in field '$userId': Cannot deserialize instance of `java.lang.String` out of START_ARRAY token"
				|| responseError.message == "JSON parse error for Offer in field '$userId': Cannot deserialize instance of `java.lang.String` out of START_ARRAY token")
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

	def "A post request with umlaut should work and return 201"() {
		given:
			def offer = new Offer(userId: '\u00fc', // ue
				                  targetSystemIds: [targetSystemId1()])

		when:
			final response = doUTF8Post(offersUri, toJson(offer))

		then:
			resultIs(response, CREATED, APPLICATION_JSON_UTF8_VALUE)

		when:
			final responseOffer = fromJson(response.contentAsString)

		then:
			responseOffer.id instanceof UUID
			responseOffer.id != null
			responseOffer == offer

		when:
			final getResponse = doUTF8Get("$offersUri/$responseOffer.id")
			def getResponseOffer = fromJson(getResponse.contentAsString)
			getResponseOffer = replaceEmptyListsByNull(getResponseOffer)

		then:
			getResponseOffer.id == responseOffer.id
			getResponseOffer == offer
	}

	def "A post request with startTime and startDate should work and have the default startTimezone in the response"() {
		given:
			final offer = [startTime: '08:30',
			               startDate: '2013-12-20',
			               targetSystemIds: [targetSystemId1()]]

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

		when:
			final getResponse = doGet("$offersUri/$responseOffer.id")
			def getResponseOffer = fromJson(getResponse.contentAsString)
			getResponseOffer = replaceEmptyListsByNull(getResponseOffer)

		then:
			getResponseOffer == responseOffer
	}

	def "A post request with startTimezone should work and return 201"() {
		given:
			final offer = [startTimezone: 'Europe/Paris',
			               targetSystemIds: [targetSystemId1()]]

		when:
			final response = doPost(offersUri, toJson(offer))

		then:
			resultIs(response, CREATED)

		when:
			final responseOffer = fromJson(response.contentAsString)

		then:
			responseOffer.startTimezone != null
			responseOffer.startTimezone as String == offer.startTimezone

		when:
			final getResponse = doGet("$offersUri/$responseOffer.id")
			def getResponseOffer = fromJson(getResponse.contentAsString)
			getResponseOffer = replaceEmptyListsByNull(getResponseOffer)

		then:
			getResponseOffer == responseOffer
	}

	def "A post request with an invalid startTime or startDate or startTimezone should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doPost(offersUri, toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST)
			responseError.message.startsWith("JSON parse error for Offer in field '${(invalidOffer as Map).keySet()[0]}'")

		where:
			invalidOffer           | _
			[startTime: 'x']       | _
			[startDate: ',']       | _
			[startTimezone: '1 7'] | _
	}

	def "A post request with a missing target system id should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doPost(offersUri, toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'The list of target system ids must not be empty'

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST, expectedMessage)

		where:
			invalidOffer                                 | _
			[userId: '5']                                | _
			[userId: '6', targetSystemIds: null]         | _
			[userId: '7', targetSystemIds: []]           | _
			[userId: '8', targetSystemIds: [null]]       | _
			[userId: '9', targetSystemIds: [null, null]] | _
	}

	def "A post request with a not existing target system id should respond with status code 400 and a meaningful error message"() {
		given:
			final notExistingId = UUID.randomUUID()
			final invalidOffer = new Offer(userId: '10',
			                               targetSystemIds: [targetSystemId1(), notExistingId])

		when:
			final response = doPost(offersUri, toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Target system for id '$notExistingId' doesn't exist"

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST, expectedMessage)
	}

	def "A post request with an invalid target system id should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidId = 333
			final invalidOffer = [userId: '11',
			                      targetSystemIds: [targetSystemId1(), invalidId]]

		when:
			final response = doPost(offersUri, toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "JSON parse error for Offer in field '$targetSystemIds': Cannot deserialize value of type `java.util.UUID` from String \"$invalidId\": UUID has to be represented by standard 36-char representation"

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST, expectedMessage)
	}

	def "A post request with an exiting target system id should work and return 201"() {
		given:
			final offer = new Offer(userId: '12',
			                        targetSystemIds: [null, targetSystemId1()])

		when:
			final response = doPost(offersUri, toJson(offer))

		then:
			resultIs(response, CREATED)

		when:
			final responseOffer = fromJson(response.contentAsString)
			offer.targetSystemIds.remove(null)

		then:
			responseOffer.id instanceof UUID
			responseOffer.id != null
			responseOffer.targetSystemIds == offer.targetSystemIds
			responseOffer == offer

		when:
			final getResponse = doGet("$offersUri/$responseOffer.id")
			def getResponseOffer = fromJson(getResponse.contentAsString)
			getResponseOffer = replaceEmptyListsByNull(getResponseOffer)

		then:
			getResponseOffer.id == responseOffer.id
			getResponseOffer.targetSystemIds == offer.targetSystemIds
			getResponseOffer == offer
	}
}
