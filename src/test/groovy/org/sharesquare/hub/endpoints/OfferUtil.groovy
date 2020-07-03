package org.sharesquare.hub.endpoints

class OfferUtil {

	protected static final offersUri = '/offers'

	protected static final userId = 'userId'

	protected static final defaultOffer = '{}'

	protected static final exampleOffer = '''
		{
		  "userId": "string",
		  "startDate": "2020-06-26",
		  "startTime": "23:57",
		  "startTimezone": "Europe/Paris",
		  "origin": {
		    "latitude": 0,
		    "longitude": 0,
		    "name": "string",
		    "type": "Address"
		  },
		  "destination": {
		    "latitude": 0,
		    "longitude": 0,
		    "name": "string",
		    "type": "Address"
		  },
		  "contactOptions": [
		    {
		      "contactType": "EMAIL",
		      "contactIdentifier": "string"
		    }
		  ],
		  "targetPlatforms": [
		    "3fa85f64-5717-4562-b3fc-2c963f66afa6",
		    "61b7317c-dfcd-473b-9159-acb820609087"
		  ],
		  "preferences": [
 		   {
		      "key": "string",
		      "value": true,
		      "type": "BooleanPreference"
		    },
 		   {
 		     "key": "string",
		      "value": 0,
		      "type": "DoublePreference"
		    },
		    {
		      "key": "string",
		      "value": 0,
		      "type": "IntegerPreference"
		    },
		    {
		      "key": "string",
		      "value": "MALE",
		      "type": "PaxGenderPreference"
		    },
		    {
		      "key": "string",
		      "value": "PETS_OK",
		      "type": "PaxPetsPreference"
		    },
		    {
		      "key": "string",
		      "value": "SMOKER",
		      "type": "PaxSmokerPreference"
		    },
		    {
		      "key": "string",
		      "value": "string",
		      "type": "StringPreference"
		    }
		  ],
		  "additionalInfo": "string"
		}
		'''
}
