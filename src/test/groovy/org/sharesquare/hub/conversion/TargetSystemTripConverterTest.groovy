package org.sharesquare.hub.conversion

import static org.sharesquare.model.preferences.PaxGenderValues.FEMALE

import org.sharesquare.hub.endpoints.RequestSpecification
import org.sharesquare.hub.model.data.EntityOffer
import org.sharesquare.hub.model.data.preferences.EntityPaxGenderPreference
import org.sharesquare.hub.model.item.targetsystem.request.TripRequest
import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Issue

class TargetSystemTripConverterTest extends RequestSpecification {

	@Autowired
	private TargetSystemTripConverter converter

	private entityOffer = '''
		{
		   "startDate": "2019-05-05",
		   "startTime": "20:41:00",
		   "startTimezone": "Europe/Berlin",
		   "origin": {
		      "latitude": 52.531677,
		      "longitude": 13.381777,
		      "name": "Berlin"
		   },
		   "destination": {
		      "latitude": 53.551086,
		      "longitude": 9.993682,
		      "name": "Hamburg"
		   },
		   "preferences": {},
		   "additionalInfo": "Hallo, ich werde mit einem Mietauto unterwegs sein"
		}
		'''

	private apiTrip = '''
		{
		  "car" : null,
		  "baggage" : null,
		  "animals" : null,
		  "TripID" : null,
		  "Triptype" : "offer",
		  "Enterdate" : "1603113267",
		  "Startdate" : "20190505",
		  "Starttime" : "2041",
		  "Currency" : "EUR",
		  "IDuser" : " ",
		  "Places" : 3,
		  "Privacy" : {
		    "Name" : null,
		    "Mobile" : null,
		    "Email" : null,
		    "Landline" : null,
		    "Licenseplate" : null,
		    "Car" : 0
		  },
		  "Relevance" : 10,
		  "Reoccur" : {
		    "Monday" : null,
		    "Tuesday" : null,
		    "Wednesday" : null,
		    "Thursday" : null,
		    "Friday" : null,
		    "Saturday" : null,
		    "Sunday" : null
		  },
		  "Routings" : [ {
		    "RoutingID" : null,
		    "Origin" : {
		      "placeID" : null,
		      "Address" : "Berlin",
		      "CountryName" : "Deutschland",
		      "CountryCode" : "DE",
		      "Latitude" : 52.531677,
		      "Longitude" : 13.381777,
		      "StopPrice" : null
		    },
		    "Destination" : {
		      "placeID" : null,
		      "Address" : "Hamburg",
		      "CountryName" : "Deutschland",
		      "CountryCode" : "DE",
		      "Latitude" : 53.551086,
		      "Longitude" : 9.993682,
		      "StopPrice" : null
		    },
		    "RoutingIndex" : 0
		  } ],
		  "Smoker" : null,
		  "Description" : "Hallo, ich werde mit einem Mietauto unterwegs sein",
		  "NumberPlate" : null,
		  "Contactlandline" : null,
		  "Contactmail" : null,
		  "Contactmobile" : null,
		  "Price" : null,
		  "from_seotitle" : null,
		  "from_title" : null,
		  "to_seotitle" : null,
		  "to_title" : null,
		  "Prefgender" : "woman",
		  "ExclusiveAssociations" : null,
		  "Associations" : null
		}
		'''

	@Issue("#44")
	def "The mapping from a share-square-hub Offer to a target system Trip should set all the values"() {
		when:
			final femalePreference = new EntityPaxGenderPreference(value: FEMALE)
			def offer = fromJson(entityOffer, EntityOffer)
			offer.preferences.paxGenderPreferences = [femalePreference]
			TripRequest resultTrip = converter.entityToApi(offer)

		then:
			resultTrip != null

		when:
			def trip = fromJson(apiTrip, TripRequest)
			trip.enterDateEpoch = resultTrip.getEnterDateEpoch()

		then:
			resultTrip == trip
	}
}
