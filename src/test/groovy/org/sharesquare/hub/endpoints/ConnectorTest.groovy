package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.sharesquare.hub.model.data.EntityOfferTargetStatus.Status.SUCCESS

import org.sharesquare.hub.model.data.EntityOffer
import org.sharesquare.hub.model.data.EntityTargetSystem
import org.sharesquare.hub.service.OfferTargetStatusService
import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Issue

class ConnectorTest extends RequestSpecification {

	@Autowired
	private OfferTargetStatusService offerTargetStatusService;

	@Issue("#44")
	def "The target system post request should work and write SUCCESS in the database"() {
		when:
			def offer = fromJson(exampleOffer())
			offer.origin.name = 'Berlin'
			offer.destination.name = 'Hamburg'
			offer.additionalInfo = 'Liebe Mitfahrer, drei meiner Kinder fahren auch mit'
			offer.targetSystemIds = [targetSystemId3()]

			final response = doPost(offersUri, toJson(offer))
			final offerId = fromJson(response.contentAsString).id
			final status = offerTargetStatusService.getStatus(
				new EntityOffer(id: offerId), new EntityTargetSystem(id: targetSystemId3()))

		then:
			status == SUCCESS
	}
}
