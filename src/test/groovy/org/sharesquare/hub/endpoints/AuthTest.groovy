package org.sharesquare.hub.endpoints

import static org.sharesquare.hub.endpoints.OfferUtil.offersUri
import static org.sharesquare.hub.endpoints.OfferUtil.userId
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE
import static org.springframework.http.HttpStatus.FORBIDDEN
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNAUTHORIZED
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

import org.springframework.beans.factory.annotation.Value

import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Stepwise

@Issue("#6")
@Stepwise
class AuthTest extends RequestSpecification {

	@Shared token

	@Value("\${custom.token.expired}")
	private String tokenExpired;

	static final tokenPattern = /(?i)([a-z0-9]+\.){2}[a-z0-9\_\-]+/

	def "The authorization server should return a access token"() {
		when:
			final response = authServerResponse()
			token = response.data.access_token

		then:
			with (response) {
				status == OK.value
				contentType == APPLICATION_JSON_VALUE
				data.access_token != null
				data.access_token.matches(tokenPattern)
			}
	}

	def "A request without authorization header should respond with status code 401 and a meaningful error message"() {
		when:
			final response = doPostWithoutAuthHeader(offersUri)

		then:
			resultIs(response, UNAUTHORIZED)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'Insufficient authentication: Full authentication is required to access this resource'

		then:
			resultContentIs(offersUri, responseError, UNAUTHORIZED, expectedMessage)
	}

	static final jwtError = 'An error occurred while attempting to decode the Jwt: '

	def "A request with an invalid access token should respond with status code 401 and a meaningful error message"() {
		when:
			final response = doPost(offersUri, defaultOffer(), APPLICATION_JSON, invalid)

		then:
			resultIs(response, UNAUTHORIZED)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			responseError.message = responseError.message.replaceFirst(/ token [^ ]* at /, ' token  at ')

		then:
			resultContentIs(offersUri, responseError, UNAUTHORIZED, jwtError + errorDetail)

		where:
			invalid                                 | errorDetail
			'a.b.c'                                 | 'Invalid unsecured/JWS/JWE header: Invalid JSON: Unexpected token  at position 0.'
			'.'                                     | 'Invalid unsecured/JWS/JWE header: Invalid JSON: Unexpected token  at position 0.'
			'a1.b.c.d'                              | 'Invalid unsecured/JWS/JWE header: Invalid JSON: Unexpected token  at position 1.'
			'a2a.b'                                 | 'Invalid unsecured/JWS/JWE header: Invalid JSON: Unexpected token  at position 2.'
			'eyJ.eyJ.123'                           | 'Invalid unsecured/JWS/JWE header: Invalid JSON: Unexpected End Of File position 2: null'
			'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.' | 'Invalid serialized unsecured/JWS/JWE object: Missing second delimiter'
			'0'                                     | 'Invalid JWT serialization: Missing dot delimiter(s)'

			"X$token"                               | 'Invalid unsecured/JWS/JWE header: Invalid JSON: Unexpected character (]) at position 0.'
			"$token.$token"                         | 'Invalid serialized unsecured/JWS/JWE object: Too many part delimiters'
			"e$token"                               | 'Invalid unsecured/JWS/JWE header: Invalid JSON: Unexpected token  at position 72.'
			"$token."                               | 'Invalid serialized JWE object: Missing fourth delimiter'
			"${token}3"                             | 'Signed JWT rejected: Invalid signature'
			"$token$token"                          | 'Unexpected number of Base64URL parts, must be three'

			"${token.replaceFirst(/\./, '')}"       | 'Invalid unsecured/JWS/JWE header: Invalid JSON: Unexpected token  at position 83.'
			"${token.replaceFirst(/\./, '..')}"     | 'Invalid serialized JWE object: Missing fourth delimiter'
			"${token.replaceFirst(/\./, '.X')}"     | 'Malformed payload'
	}

	static final malformedError = 'Bearer error="invalid_token", error_description="Bearer token is malformed", error_uri="https://tools.ietf.org/html/rfc6750#section-3.1"'

	def "A request with a malformed or empty access token should respond with status code 401 and a meaningful error message in the response header"() {
		when:
			final response = doPost(offersUri, defaultOffer(), APPLICATION_JSON, malformed)

		then:
			with (response) {
				status                          == UNAUTHORIZED.value
				errorMessage                    == null
				headers[CONTENT_TYPE]           == null
				contentType                     == null
				headers[WWW_AUTHENTICATE].value == malformedError
				contentAsString                 == ''
		}

		where:
			malformed       | _
			'a.b.c*'        | _
			'a.b.c!'        | _
			'?.'            | _
			'&'             | _
			"$token "       | _
			"$token $token" | _
			''              | _
	}

	def "A request with a malformed authorization header value should respond with status code 401 and a meaningful error message in the response header"() {
		when:
			final response = doPostWithAuthHeaderValue(offersUri, value)

		then:
			with (response) {
				status                          == UNAUTHORIZED.value
				errorMessage                    == null
				headers[CONTENT_TYPE]           == null
				contentType                     == null
				headers[WWW_AUTHENTICATE].value == malformedError
				contentAsString                 == ''
		}

		where:
			value                  | _
			"Bearer  $token"       | _
			"Bearer$token"         | _
			"Bearerr $token"       | _
			"Bearer Bearer $token" | _
	}

	def "A request with an invalid or empty authorization header value should respond with status code 401 and a meaningful error message"() {
		when:
			final response = doPostWithAuthHeaderValue(offersUri, value)

		then:
			resultIs(response, UNAUTHORIZED)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'Insufficient authentication: Full authentication is required to access this resource'

		then:
			resultContentIs(offersUri, responseError, UNAUTHORIZED, expectedMessage)

		where:
			value            | _
			token            | _
			" Bearer $token" | _
			" $token"        | _
			"$token "        | _
			''               | _
			' '              | _
	}

	def "A request with an invalid or empty authorization header key should respond with status code 401 and a meaningful error message"() {
		when:
			final response = doPostWithAuthHeaderKey(offersUri, key)

		then:
			resultIs(response, UNAUTHORIZED)

		when:
			final responseError = fromJson(response.contentAsString, Map)
			final expectedMessage = 'Insufficient authentication: Full authentication is required to access this resource'

		then:
			resultContentIs(offersUri, responseError, UNAUTHORIZED, expectedMessage)

		where:
			key              | _
			'Authorization ' | _
			' Authorization' | _
			'Authorizatio'   | _
			'&=;'            | _
			' '              | _
	}

	def "A request with an expired access token should respond with status code 401 and a meaningful error message"() {
		when:
			def response = doPost(offersUri, defaultOffer(), APPLICATION_JSON, tokenExpired)

		then:
			resultIs(response, UNAUTHORIZED)

		when:
			def responseError = fromJson(response.contentAsString, Map)
			responseError.message = responseError.message.replaceFirst(/[^ ]+$/, '')
			final expectedMessage = 'An error occurred while attempting to decode the Jwt: Jwt expired at '

		then:
			resultContentIs(offersUri, responseError, UNAUTHORIZED, expectedMessage)
	}

	@Issue("#29")
	def "A request with an access token not in the scope should respond with status code 403 and a meaningful error message"() {
		when:
			def response = null
			switch(method) {
				case 'post':
					response = doPost(path, '{}', APPLICATION_JSON, accessTokenNotInScope())
					break
				case 'put':
					response = doPut(path, '{}', APPLICATION_JSON, accessTokenNotInScope())
					break
				case 'delete':
					response = doDelete(path, accessTokenNotInScope())
					break
				case 'getById':
					response = doGet(path, accessTokenNotInScope())
					break
				case 'get':
					response = doGet(path, userId, 'testuser', ' ', '', ' ', '', accessTokenNotInScope())
					break
				default:
					response = 'test case missing'
			}

		then:
			resultIs(response, FORBIDDEN)

		when:	
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(path, responseError, FORBIDDEN, 'Access is denied')

		where:
			method    | path
			'post'    | offersUri
			'put'     | "$offersUri/${UUID.randomUUID()}"
			'delete'  | "$offersUri/${UUID.randomUUID()}"
			'getById' | "$offersUri/${UUID.randomUUID()}"
			'get'     | offersUri

			'post'    | targetSystemsUri
			'delete'  | "$targetSystemsUri/${UUID.randomUUID()}"
			'get'     | targetSystemsUri
	}

	@Issue("#29")
	def "A request with an access token in the wrong scope should respond with status code 403 and a meaningful error message"() {
		when:
			def response = null
			switch(method) {
				case 'post':
					if (path.startsWith(offersUri)) {
						response = doPost(path, '{}', APPLICATION_JSON, accessTokenInTargetScope())
					} else {
						response = doPost(path, '{}', APPLICATION_JSON, accessToken())
					}
					break
				case 'put':
					response = doPut(path, '{}', APPLICATION_JSON, accessTokenInTargetScope())
					break
				case 'delete':
					if (path.startsWith(offersUri)) {
						response = doDelete(path, accessTokenInTargetScope())
					} else {
						response = doDelete(path, accessToken())
					}
					break
				case 'getById':
					response = doGet(path, accessTokenInTargetScope())
					break
				case 'get':
					response = doGet(path, userId, 'testuser', ' ', '', ' ', '', accessTokenInTargetScope())
					break
				default:
					response = 'test case missing'
			}

		then:
			resultIs(response, FORBIDDEN)

		when:
			final responseError = fromJson(response.contentAsString, Map)

		then:
			resultContentIs(path, responseError, FORBIDDEN, 'Access is denied')

		where:
			method    | path
			'post'    | offersUri
			'put'     | "$offersUri/${UUID.randomUUID()}"
			'delete'  | "$offersUri/${UUID.randomUUID()}"
			'getById' | "$offersUri/${UUID.randomUUID()}"
			'get'     | offersUri

			'post'    | targetSystemsUri
			'delete'  | "$targetSystemsUri/${UUID.randomUUID()}"
			'get'     | targetSystemsUri
	}
}
