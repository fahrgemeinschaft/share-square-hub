package org.sharesquare.hub.endpoints

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.FORBIDDEN
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.TEXT_XML

import org.sharesquare.model.Connector
import org.sharesquare.model.TargetSystem
import org.springframework.beans.factory.annotation.Value

import spock.lang.Issue
import spock.lang.Stepwise

@Issue("#25,#19,#20,#29")
@Stepwise
class TargetSystemsGetRequestTest extends RequestSpecification {

	@Value("\${custom.data.example.target.name}")
	private exampleTargetName;

	@Issue("#9")
	def "A get request should return 200 and the list of target systems in the response body"() {
		when:
			final response = doGet(targetSystemsUri)

		then:
			resultIs(response, OK)

		when:
			final responseTargetSystems = fromJson(response.contentAsString, TargetSystem[])
			Set names = []
			responseTargetSystems.each {
				names.add(it.name)
			}

		then:
			responseTargetSystems.size() == 4
			names == ['Fahrgemeinschaft.de', 'ride2Go', 'Mitfahrzentrale MiFaZ', exampleTargetName] as Set
			responseTargetSystems.each {
				assert it.id instanceof UUID
				assert it.id != null
				//assert it.description instanceof String
				//assert it.description != null
				//assert it.description != ''
				//assert it.vanityUrl instanceof URL
				//assert it.vanityUrl != null
				//assert it.contentLanguage instanceof String
				//assert it.contentLanguage != null
				//assert it.contentLanguage != ''
				//assert it.dataProtectionRegulations instanceof String
				//assert it.dataProtectionRegulations != null
				//assert it.dataProtectionRegulations != ''
			}
	}

	@Issue("#11")
	def "A valid post request should work and return 201"() {
		when:
			final response = doPost(targetSystemsUri, targetSystem, APPLICATION_JSON, accessTokenInTargetScope())

		then:
			resultIs(response, CREATED)

		when:
			final responseTargetSystem = fromJson(response.contentAsString, TargetSystem)
			targetSystem = fromJson(targetSystem, TargetSystem)
			targetSystem.connector = null

		then:
			responseTargetSystem.id instanceof UUID
			responseTargetSystem.id != null
			responseTargetSystem == targetSystem

		when:
			final getResponse = doGet(targetSystemsUri)
			def getResponseTargetSystems = fromJson(getResponse.contentAsString, TargetSystem[])
			def getResponseTargetSystem = null
			getResponseTargetSystems.each {
				if (it.name == targetSystem.name) {
					getResponseTargetSystem = it
				}
			}

		then:
			getResponseTargetSystems.size() == expectedSize
			getResponseTargetSystem != null
			getResponseTargetSystem.id == responseTargetSystem.id
			getResponseTargetSystem == targetSystem

		where:
			targetSystem        | expectedSize
			defaultTargetSystem | 5
			exampleTargetSystem | 6
	}

	@Issue("#27")
	def "Target system post and delete requests should only manage target systems from this client"() {
		when:
			final id1 = fromJson(doPost(targetSystemsUri, defaultTargetSystem, APPLICATION_JSON, accessTokenInTargetScope()).contentAsString, TargetSystem).id
			final id2 = fromJson(doPost(targetSystemsUri, exampleTargetSystem, APPLICATION_JSON, accessTokenInTargetScope2()).contentAsString, TargetSystem).id

		and:
			final response = doGet(targetSystemsUri)
			final responseTargetSystems = fromJson(response.contentAsString, TargetSystem[])
			Set ids = []
			responseTargetSystems.each {
				if (it.id == id1 || it.id == id2) {
					ids.add(it.id)
				}
			}

		then:
			ids == [id1, id2] as Set

		when:
			final response1 = doDelete("$targetSystemsUri/$id1", accessTokenInTargetScope2())
			final response2 = doDelete("$targetSystemsUri/$id2", accessTokenInTargetScope())
			final response3 = doDelete("$targetSystemsUri/$id1", accessTokenInTargetScope())
			final response4 = doDelete("$targetSystemsUri/$id2", accessTokenInTargetScope2())

		then:
			response1.status == NOT_FOUND.value
			response2.status == NOT_FOUND.value
			response3.status == NO_CONTENT.value
			response4.status == NO_CONTENT.value
	}

	def "A post request with an empty body should respond with status code 400 and a meaningful error message"() {
		given:
			final emptyRequestBody = ''

		when:
			final response = doPost(targetSystemsUri, emptyRequestBody, APPLICATION_JSON, accessTokenInTargetScope())

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'Required request body is missing'

		then:
			resultContentIs(targetSystemsUri, responseError, BAD_REQUEST, expectedMessage)
	}

	def "A post request with invalid JSON should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidJson = '{{}{!'

		when:
			final response = doPost(targetSystemsUri, invalidJson, APPLICATION_JSON, accessTokenInTargetScope())

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Invalid request body. JSON parse error: Unexpected character ('{' (code 123)): was expecting double-quote to start field name"

		then:
			resultContentIs(targetSystemsUri, responseError, BAD_REQUEST, expectedMessage)
	}

	def "A post request with a wrong field type in the body should respond with status code 400 and a meaningful error message"() {
		given:
			final invalidTargetSystem = [vanityUrl: []]

		when:
			final response = doPost(targetSystemsUri, toJson(invalidTargetSystem), APPLICATION_JSON, accessTokenInTargetScope())

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(targetSystemsUri, responseError, BAD_REQUEST)
			responseError.message == "JSON parse error for TargetSystem in field 'vanityUrl': Cannot deserialize instance of `java.net.URL` out of START_ARRAY token"
	}

	def "A post request with a not excepted content type should respond with status code 415 and a meaningful error message"() {
		given:
			final targetSystemAsXml = "<targetSystem><name>nobody</name></targetSystemr>"

		when:
			final response = doPost(targetSystemsUri, targetSystemAsXml, TEXT_XML, accessTokenInTargetScope())

		then:
			resultIs(response, UNSUPPORTED_MEDIA_TYPE)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = "Content type 'text/xml' not supported"

		then:
			resultContentIs(targetSystemsUri, responseError, UNSUPPORTED_MEDIA_TYPE, expectedMessage)
	}

	def "A delete request with an existing id should work and respond with status code 204"() {
		when:
			final id = fromJson(doPost(targetSystemsUri, targetSystem, APPLICATION_JSON, accessTokenInTargetScope()).contentAsString, TargetSystem).id
			final response = doDelete("$targetSystemsUri/$id", accessTokenInTargetScope())

		then:
			response.status == NO_CONTENT.value

		when:
			final getResponse = doGet(targetSystemsUri)
			def getResponseTargetSystems = fromJson(getResponse.contentAsString, TargetSystem[])

		then:
			getResponseTargetSystems.each {
				assert it.id != id
			}
			
		where:
			targetSystem        | _
			defaultTargetSystem | _
			exampleTargetSystem | _
	}

	def "A delete request with a not existing id should respond with status code 404"() {
		given:
			final id = UUID.randomUUID()

		when:
			final response = doDelete("$targetSystemsUri/$id", accessTokenInTargetScope())

		then:
			response.status == NOT_FOUND.value

		when:
			final getResponse = doGet(targetSystemsUri)
			def getResponseTargetSystems = fromJson(getResponse.contentAsString, TargetSystem[])

		then:
			getResponseTargetSystems.each {
				assert it.id != id
			}
	}

	def "A delete request with an invalid UUID or an empty id should respond with status code 400 and a meaningful error message"() {
		when:
			final response = doDelete("$targetSystemsUri/$id", accessTokenInTargetScope())

		then:
			resultIs(response, BAD_REQUEST)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs("$targetSystemsUri/$id", responseError, BAD_REQUEST, expectedMessage)

		when:
			final getResponse = doGet(targetSystemsUri)
			def getResponseTargetSystems = fromJson(getResponse.contentAsString, TargetSystem[])

		then:
			getResponseTargetSystems.each {
				assert it.id != id
			}

		where:
			id         | expectedMessage
			'wrong_id' | "Type mismatch for path variable: Invalid UUID string: $id"
			''         | 'Required path variable id is missing'
	}
}
