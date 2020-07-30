package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.sharesquare.hub.endpoints.OfferUtil.targetSystemIds
import static org.sharesquare.hub.endpoints.OfferUtil.userId
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import static org.springframework.http.MediaType.TEXT_XML

import java.time.LocalDate
import java.time.LocalTime

import org.sharesquare.model.Offer

import spock.lang.Issue
import spock.lang.Stepwise

@Issue("#16,19")
@Stepwise
class OfferPutRequestTest extends RequestSpecification {

	private existingId

	private existingId() {
		existingId = (existingId != null) ? existingId : fromJson(doPost(offersUri, defaultOffer()).contentAsString).id
	}

	private uri

	private uri() {
		uri = (uri != null) ? uri : "$offersUri/${existingId()}"
	}

	def "A valid put request should work and return 200"() {
		when:
			def requestOffer = (example == 1) ? defaultOffer() : exampleOffer()
			final response = doPut(uri(), requestOffer)

		then:
			response.status == OK.value

		when:
			final getResponse = doGet(uri())
			def getResponseOffer = fromJson(getResponse.contentAsString)
			getResponseOffer = replaceEmptyListsByNull(getResponseOffer)
			requestOffer = fromJson(requestOffer)

		then:
			getResponseOffer.id == existingId()
			getResponseOffer == requestOffer

		where:
			example | _
			1       | _
			2       | _
	}

	def "A put request with a not existing id should respond with status code 404"() {
		given:
			final id = 'f52c1ce2-70d9-4917-a0dc-a0a12a58396e'

		when:
			final response = doPut("$offersUri/$id", defaultOffer())

		then:
			response.status == NOT_FOUND.value

		when:
			final getResponse = doGet("$offersUri/$id")

		then:
			getResponse.status == NOT_FOUND.value
	}

	def "A put request with an invalid UUID or an empty id should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doPut("$offersUri/$id", defaultOffer())

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs("$offersUri/$id", responseError, BAD_REQUEST, expectedMessage)

		where:
			id        | expectedMessage
			'corrupt' | "Type mismatch for path variable: Invalid UUID string: $id"
			''        | 'Required path variable id is missing'
	}

	def "A put request with an empty body should respond with status code 400 and a meaningful error message"() {
		given:
			final emptyRequestBody = ''

		when:
			final response = doPut(uri(), emptyRequestBody)

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'Required request body is missing'

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST, expectedMessage)
	}

	def "A put request with invalid JSON should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidJson = '{]}'

		when:
			final response = doPut(uri(), invalidJson)

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Invalid request body. JSON parse error: Unexpected close marker ']': expected '}' (for Object starting at [Source: (PushbackInputStream); line: 1, column: 1])"

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST, expectedMessage)
	}

	def "A put request with a wrong field type in the body should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidOffer = [origin: 'string']

		when:
			final response = doPut(uri(), toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST)
			responseError.message == "JSON parse error for Offer in field 'origin': Cannot construct instance of `org.sharesquare.model.Location` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('string')"
	}

	def "A put request with a not excepted content type should respond with status code 415 and a meaningful error message"() {
		given:
			final offerAsXml = "<offer><$userId>4</$userId><$targetSystemIds>[${targetSystemId1()}]</$targetSystemIds></offer>"

		when:
			final response = doPut(uri(), offerAsXml, TEXT_XML)

		then:
			resultIs(response, UNSUPPORTED_MEDIA_TYPE)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Content type 'text/xml' not supported"

		then:
			resultContentIs(uri(), responseError, UNSUPPORTED_MEDIA_TYPE, expectedMessage)
	}

	def "A put request with umlaut should work and return 200"() {
		given:
			def updateOffer = new Offer(id: existingId(),
			                            userId: '\u00c4', // Ae
			                            startDate: LocalDate.now(),
			                            startTime: LocalTime.of(9, 41, 0, 0),
			                            origin: [latitude: 0, longitude: 0],
			                            destination: [latitude: 0, longitude: 0],
			                            targetSystemIds: [targetSystemId1()])

		when:
			final response = doUTF8Put(uri(), toJson(updateOffer))

		then:
			response.status == OK.value

		when:
			final getResponse = doUTF8Get(uri())
			def getResponseOffer = fromJson(getResponse.contentAsString)
			getResponseOffer = replaceEmptyListsByNull(getResponseOffer)

		then:
			getResponseOffer.id == updateOffer.id
			getResponseOffer == updateOffer
	}

	def "A put request with startTime and startDate should work and have the default startTimezone in the response"() {
		given:
			final addOffer = [startTimezone: 'Pacific/Auckland',
			                  startTime: '11:43',
			                  startDate: '2016-03-21',
			                  origin: [latitude: 0, longitude: 0],
			                  destination: [latitude: 0, longitude: 0],
			                  targetSystemIds: [targetSystemId1()]]
			final updateOffer = [startTime: '10:40',
			                     startDate: '2016-07-01',
			                     origin: [latitude: 0, longitude: 0],
			                     destination: [latitude: 0, longitude: 0],
			                     targetSystemIds: [targetSystemId1()]]

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
			final offer = [startTimezone: 'America/Argentina/Buenos_Aires',
			               startTime: '11:23',
			               startDate: '2014-03-26',
			               origin: [latitude: 0, longitude: 0],
			               destination: [latitude: 0, longitude: 0],
			               targetSystemIds: [targetSystemId1()]]

		when:
			final response = doPut(uri(), toJson(offer))

		then:
			response.status == OK.value

		when:
			final getResponse = doGet(uri())
			final responseOffer = fromJson(getResponse.contentAsString)

		then:
			responseOffer.startTimezone != null
			responseOffer.startTimezone as String == offer.startTimezone
	}

	def "A put request with an invalid startTime or startDate or startTimezone should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doPut(uri(), toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST)
			responseError.message.startsWith("JSON parse error for Offer in field '${(invalidOffer as Map).keySet()[0]}")

		where:
			invalidOffer                      | _
			[startTime: '1:1']                | _
			[startDate: '2017-5-6']           | _
			[startTimezone: 'Europe/Rostock'] | _
	}

	@Issue("#30")
	def "A put request with a missing startTime or startDate should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doPut(uri(), toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST, expectedMessage)

		where:
			invalidOffer                                                                                             | expectedMessage
			[startTime: '16:04', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0]]      | "The startDate must not be empty. Value 'null' for startDate not excepted."
			[startDate: '2002-04-18', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0]] | "The startTime must not be empty. Value 'null' for startTime not excepted."
	}

	def "A put request with a missing target system id should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doPut(uri(), toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'The list of targetSystem ids must not be empty'

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST, expectedMessage)

		where:
			invalidOffer                                                                                                                                                              | _
			[userId: '13', startDate: '2008-10-03', startTime: '07:06', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0]]                                | _
			[userId: '14', startDate: '2008-10-03', startTime: '07:06', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: null]         | _
			[userId: '15', startDate: '2008-10-03', startTime: '07:06', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: []]           | _
			[userId: '16', startDate: '2008-10-03', startTime: '07:06', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [null]]       | _
			[userId: '17', startDate: '2008-10-03', startTime: '07:06', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [null, null]] | _
	}

	def "A put request with a not existing target system id should respond with status code 400 and a meaningful error message"() {
		given:
			final notExistingId = UUID.randomUUID()
			final invalidOffer = new Offer(userId: '18',
			                               startDate: LocalDate.now(),
			                               startTime: LocalTime.now(),
			                               origin: [latitude: 0, longitude: 0],
			                               destination: [latitude: 0, longitude: 0],
			                               targetSystemIds: [targetSystemId1(), notExistingId])

		when:
			final response = doPut(uri(), toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Unable to find target system with id $notExistingId"

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST, expectedMessage)
	}

	def "A put request with an invalid target system id should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidId = 333
			final invalidOffer = [userId: '19',
			                      targetSystemIds: [targetSystemId1(), invalidId]]

		when:
			final response = doPut(uri(), toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "JSON parse error for Offer in field '$targetSystemIds': Cannot deserialize value of type `java.util.UUID` from String \"$invalidId\": UUID has to be represented by standard 36-char representation"

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST, expectedMessage)
	}

	def "A put request with an exiting target system id should work and return 200"() {
		given: 
			final updateOffer = new Offer(id: existingId(),
			                              userId: '20',
			                              startDate: LocalDate.now(),
			                              startTime: LocalTime.of(5, 25, 0, 0),
			                              origin: [latitude: 0, longitude: 0],
			                              destination: [latitude: 0, longitude: 0],
			                              targetSystemIds: [null, targetSystemId1()])

		when:
			final response = doPut(uri(), toJson(updateOffer))

		then:
			response.status == OK.value
			
		when:
			final getResponse = doGet(uri())
			def getResponseOffer = fromJson(getResponse.contentAsString)
			getResponseOffer = replaceEmptyListsByNull(getResponseOffer)
			updateOffer.targetSystemIds.remove(null)
	
		then:
			getResponseOffer.id == updateOffer.id
			getResponseOffer.targetSystemIds == updateOffer.targetSystemIds
			getResponseOffer == updateOffer
	}

	@Issue("#30")
	def "A put request with a missing origin, destination, latitude or longitude should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doPut(uri(), toJson(invalidOffer))

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(uri(), responseError, BAD_REQUEST, expectedMessage)

		where:
			invalidOffer                                                                                                                    | expectedMessage
			[startDate: '1989-02-14', startTime: '06:14', destination: [latitude: 0, longitude: 0]]                                         | "The origin must not be empty. Value 'null' for origin not excepted."
			[startDate: '1989-02-14', startTime: '06:14', origin: [latitude: 0, longitude: 0]]                                              | "The destination must not be empty. Value 'null' for destination not excepted."
			[startDate: '1989-02-14', startTime: '06:14', origin: [latitude: 0], destination: [latitude: 0, longitude: 0]]                  | "The longitude must not be empty. Value 'null' for origin.longitude not excepted."
			[startDate: '1989-02-14', startTime: '06:14', origin: [longitude: 0], destination: [latitude: 0, longitude: 0]]                 | "The latitude must not be empty. Value 'null' for origin.latitude not excepted."
			[startDate: '1989-02-14', startTime: '06:14', origin: [latitude: 0, longitude: 0], destination: [latitude: 0]]                  | "The longitude must not be empty. Value 'null' for destination.longitude not excepted."
			[startDate: '1989-02-14', startTime: '06:14', origin: [latitude: 0, longitude: 0], destination: [longitude: 0]]                 | "The latitude must not be empty. Value 'null' for destination.latitude not excepted."
			[startDate: '1989-02-14', startTime: '06:14', origin: [latitude: 100, longitude: 0], destination: [latitude: 0, longitude: 0]]  | "must be less than or equal to 90. Value '100.0' for origin.latitude not excepted."
			[startDate: '1989-02-14', startTime: '06:14', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: -250]] | "must be greater than or equal to -180. Value '-250.0' for destination.longitude not excepted."
	}
}
