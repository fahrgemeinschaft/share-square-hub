package org.sharesquare.hub.endpoints

import static org.springframework.http.HttpStatus.OK

import org.sharesquare.model.TargetSystem

import spock.lang.Issue

class TargetSystemsGetRequestTest extends RequestSpecification {

	static final getUri = '/targetsystems'

	@Issue("#9")
	def "A get request should return 200 and the list of target systems in the response body"() {
		when:
			final response = doGet(getUri)

		then:
			resultIs(response, OK)

		when:
			final responseTargetSystems = fromJson(response.contentAsString, TargetSystem[])
			Set names = []
			responseTargetSystems.each {
				names.add(it.name)
			}

		then:
			responseTargetSystems.size() == 3
			names == ['Fahrgemeinschaft.de', 'ride2Go', 'Mitfahrzentrale MiFaZ'] as Set
			responseTargetSystems.each {
				it.id instanceof UUID
				it.id != null
				it.description instanceof String
				it.description != null
				it.description != ''
				it.vanityUrl instanceof URL
				it.vanityUrl != null
			}
	}
}
