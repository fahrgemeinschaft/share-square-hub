# share-square-hub

The hub allows different client systems to provide trip offer data to a selection of registered target systems.

### Deployment

Define your maven repository properties in the **local.properties** file where the library 'share-square-commons' can be found:

* repo.url
* repo.username
* repo.password

or use environment variables:

* REPO_URL
* REPO_USERNAME
* REPO_PASSWORD


Alternatively install the library 'share-square-commons' into your local repository and use `mavenLocal()` in your **build.gradle** instead.

Define authorization properties in the **application.properties** file:

* custom.auth.server.domain
* custom.auth.server.realm
* custom.auth.server.scope

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

### Datasource

The Application runs with a H2 in-memory database. The database web console can be accessed at   [http://localhost:8080/h2-console](http://localhost:8080/h2-console).

Use the following settings:

**Saved Settings:** Generic H2 (Embedded)  
**Setting Name:** Generic H2 (Embedded)  
**Driver Class:** org.h2.Driver  
**JDBC URL:** jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1  
**User Name:** sa  
**Password:**

### Swagger

[http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)  
[http://localhost:8080/api/v3/api-docs](http://localhost:8080/api/v3/api-docs)

### Actuator

Endpoints like:  
[http://localhost:8080/api/actuator/health](http://localhost:8080/api/actuator/health)  
[http://localhost:8080/api/actuator/beans](http://localhost:8080/api/actuator/beans)
