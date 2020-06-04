package org.sharesquare.hub.configuration;


import java.util.Collections;

import org.apache.tomcat.websocket.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfiguration {

	private static final String AUTH = "OAuth 2.0";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(AUTH, authorizationSecuritySchema()))
                .info(new Info().title("ShareSquare HUB API").version("1").description(
                        "ShareSquare HUB API.  You can find out more about     Swagger at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).      For this sample, you can use the api key `special-key` to test the authorization     filters.")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .security(Collections.singletonList(new SecurityRequirement().addList(AUTH)));
    }

	public SecurityScheme authorizationSecuritySchema() {
		return new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.name(Constants.AUTHORIZATION_HEADER_NAME)
				.description(
						"Authorization header using the Bearer scheme <b>\"Authorization: Bearer &lt;Value&gt;\"</b> where Value is the access token.")
				.in(SecurityScheme.In.HEADER);
	}
}
