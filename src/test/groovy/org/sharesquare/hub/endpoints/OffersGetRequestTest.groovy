package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.sharesquare.hub.endpoints.OfferUtil.page
import static org.sharesquare.hub.endpoints.OfferUtil.size
import static org.sharesquare.hub.endpoints.OfferUtil.userId
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

import spock.lang.Issue

@Issue("#21")
class OffersGetRequestTest extends RequestSpecification {

	def "A get request with an userId search parameter should return the matching Offers along with status code 200"() {
		given:
			String userA = 'a'
			String userB = 'b'
			String userC = 'c'

		when:
			String id1 = fromJson(doPost(offersUri, toJson([userId: userA, startTime: '21:22', startDate: '2013-02-25', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]])).contentAsString).id
			String id2 = fromJson(doPost(offersUri, toJson([userId: userA, startTime: '21:22', startDate: '2013-02-25', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]])).contentAsString).id
			String id3 = fromJson(doPost(offersUri, toJson([userId: userB, startTime: '21:22', startDate: '2013-02-25', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]])).contentAsString).id

		and:
			final responseA = doGet(offersUri, userId, userA)
			final responseB = doGet(offersUri, userId, userB)
			final responseC = doGet(offersUri, userId, userC)

		then:
			resultIs(responseA, OK)
			resultIs(responseB, OK)
			resultIs(responseC, OK)

		when:
			final responseOffersA = fromJson(responseA.contentAsString, Map)
			final responseOffersB = fromJson(responseB.contentAsString, Map)
			final responseOffersC = fromJson(responseC.contentAsString, Map)

		and:
			Set idsA = []
			responseOffersA.content.each {
				idsA.add(it.id)
			}

		then:
			contentSizeIs(responseOffersA, 2)
			responseOffersA.content.each {
				assert it.userId == userA
			}
			idsA == [id1, id2] as Set

		and:
			contentSizeIs(responseOffersB, 1)
			with (responseOffersB) {
				content[0].userId == userB
				content[0].id == id3
			}

		and:
			contentSizeIs(responseOffersC, 0)
	}

	def "A get request with an empty userId search parameter value should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doGet(offersUri, userId, '')

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Request parameter $userId must not be empty"

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST, expectedMessage)
	}

	def "A get request with a missing userId search parameter should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doGet(offersUri, ' ', '')

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'Required path variable id or query parameter is missing'

		then:
			resultContentIs(offersUri, responseError, BAD_REQUEST, expectedMessage)
	}

	def "A get request with a size parameter should return the right amount of Offers along with status code 200"() {
		when:
			for (int i = 0; i < 2; i++) {
				doPost(offersUri, toJson([userId: userD, startTime: '11:10', startDate: '2009-04-16', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]]))
			}

		and:
			final response = doGet(offersUri, userId, userD, size, requestSize)

		then:
			resultIs(response, OK)

		when:
			final responseOffers = fromJson(response.contentAsString, Map)

		then:
			contentSizeIs(responseOffers, responseSize)

		where:
			userD | requestSize | responseSize
			'd1'  | '1'         | 1
			'd2'  | '2'         | 2
			'd3'  | '3'         | 2
	}

	def "A get request with a page parameter should return the right amount of Offers along with status code 200"() {
		when:
			for (int i = 0; i < 3; i++) {
				doPost(offersUri, toJson([userId: userE, startTime: '13:10', startDate: '2019-08-10', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]]))
			}

		and:
			final response = doGet(offersUri, userId, userE, size, '2', page, requestPage)

		then:
			resultIs(response, OK)

		when:
			final responseOffers = fromJson(response.contentAsString, Map)

		then:
			contentSizeIs(responseOffers, responseSize)

		where:
			userE | requestPage | responseSize
			'e1'  | '0'         | 2
			'e2'  | '1'         | 1
			'e3'  | '2'         | 0
	}

	def "A get request with an userId search parameter should return the matching Offers in the right default order along with status code 200"() {
		given:
			String userF = 'f'

		when:
			doPost(offersUri, toJson([userId: userF, startTime: '11:10', startDate: '2019-08-15', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]]))
			doPost(offersUri, toJson([userId: userF, startTime: '11:00', startDate: '2019-08-15', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]]))
			doPost(offersUri, toJson([userId: userF, startTime: '11:20', startDate: '2019-08-10', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]]))

		and:
			final response = doGet(offersUri, userId, userF)

		then:
			resultIs(response, OK)

		when:
			final responseOffers = fromJson(response.contentAsString, Map)

		then:
			contentSizeIs(responseOffers, 3)
			with (responseOffers) {
				content[0].startTime == '11:20'
				content[1].startTime == '11:00'
				content[2].startTime == '11:10'
			}
	}

	def "A get request with an invalid or too high size parameter should use the default size 10 or max size 12 and respond with status code 200"() {
		given:
			int defaultSize = 10

		when:
			for (int i = 0; i < defaultSize + 3; i++) {
				doPost(offersUri, toJson([userId: userG, startTime: '11:11', startDate: '2011-06-04', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]]))
			}

		and:
			final response = doGet(offersUri, userId, userG, size, requestSize)

		then:
			resultIs(response, OK)

		when:
			final responseOffers = fromJson(response.contentAsString, Map)

		then:
			contentSizeIs(responseOffers, responseSize)

		where:
			userG | requestSize | responseSize
			'g1'  | 'twelve'    | 10
			'g2'  | '20'        | 12
	}

	def "A get request with an invalid page parameter should use the default page 0 and respond with status code 200"() {
		given:
			String userH = 'h'

		when:
			doPost(offersUri, toJson([userId: userH, startTime: '17:01', startDate: '2012-11-24', origin: [latitude: 0, longitude: 0], destination: [latitude: 0, longitude: 0], targetSystemIds: [targetSystemId1()]]))

		and:
			final response = doGet(offersUri, userId, userH, page, 'two')

		then:
			resultIs(response, OK)

		when:
			final responseOffers = fromJson(response.contentAsString, Map)

		then:
			contentSizeIs(responseOffers, 1)
	}

	def "A get request with an userId search parameter should work for different offers"() {
		when:
			def offer = (example == 1) ? defaultOffer() : exampleOffer()
			offer = fromJson(offer)
			offer.userId = userI

			doPost(offersUri, toJson(offer))

			final response = doGet(offersUri, userId, userI)

		then:
			resultIs(response, OK)

		when:
			final responseOffers = fromJson(response.contentAsString, Map)

		then:
			contentSizeIs(responseOffers, 1)
			replaceEmptyListsByNull(
				fromJson(
					toJson(responseOffers.content[0]))) == offer

		where:
			example | userI
			1       | 'i1'
			2       | 'i2'
	}
}
