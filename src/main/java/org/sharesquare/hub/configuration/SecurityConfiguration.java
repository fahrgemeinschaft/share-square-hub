package org.sharesquare.hub.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sharesquare.commons.web.security.oauth.resourceserver.KeycloakRealmRoleConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@EnableWebSecurity
@Configuration
@Data
@EqualsAndHashCode(callSuper = false)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    JwtAuthenticationConverter createJwtAuthenticationConverter() {
        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return jwtAuthenticationConverter;
    }

    @Value("${SHARE2_HUB_ROLE_NAME:share2_hub}")
    private String share2HubRole;

    @Value("${SHARE2_CONNECTOR_ROLE_NAME:share2_connector}")
    private String share2ConnectorRole;

    @Value("${SHARE2_CLIENT_ROLE_NAME:share2_client}")
    private String share2ClientRole;

    @Value("${SHARE2_USER_ROLES_CLAIM:share2_roles}")
    private String userRolesClaim;

    @Value("${SHARE2_USER_ID_CLAIM:user_id}")
    private String userIdClaim;

    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .anyRequest().permitAll();

    }
        /*
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                //endpoints with explicit auth  config
                //user
                .antMatchers("/user/**")
                .hasAnyRole(share2HubRole, share2ConnectorRole)
                //endpoints/contact
                .antMatchers("/endpoint/**")
                .hasAnyRole(share2HubRole, share2ConnectorRole)
                //trips
                .antMatchers(HttpMethod.POST, "/trip/{id:[\\\\w+]}").hasAnyRole(share2HubRole, share2ConnectorRole)
                .antMatchers(HttpMethod.PUT, "/trip/id/{id:[\\\\w+]}").hasAnyRole(share2HubRole, share2ConnectorRole)
                .antMatchers(HttpMethod.GET, "/trip/**").anonymous()
                //anything else
                .anyRequest().permitAll().and()
                //oauth config
                .oauth2ResourceServer()
                .jwt().jwtAuthenticationConverter(createJwtAuthenticationConverter());


    }
 */

}
