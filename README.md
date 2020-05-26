# share-square-hub

### Deployment

Define authorization properties in the **application.properties** file:

* auth.server.domain
* auth.server.realm
* auth.server.scope

or use environment variables:

* SHARE2_AUTH_SERVER_DOMAIN
* SHARE2_AUTH_SERVER_REALM
* SHARE2_AUTH_SERVER_SCOPE

Run **HubMain.java** or

```
$ ./gradlew bootRun
```

### Authorization

Get access token from Keycloak:

```
curl -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=xxx" \
  -d "client_secret=xxx" \
  -d "grant_type=client_credentials" \
  -X POST http://localhost:9080/auth/realms/heroes/protocol/openid-connect/token
```

Authorize request with header `"Authorization: Bearer {access_token}"`
