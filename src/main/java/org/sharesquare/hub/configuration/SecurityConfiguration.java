package org.sharesquare.hub.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sharesquare.commons.web.security.oauth.resourceserver.KeycloakRealmRoleConverter;
import org.sharesquare.hub.exception.RestAccessDeniedHandler;
import org.sharesquare.hub.exception.RestAuthenticationEntryPoint;
import org.sharesquare.hub.exception.RestAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

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

	@Value("${custom.auth.server.scope}")
	private String scope;

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;

	private static final String SCOPE_PREFIX = "SCOPE_";

	@Bean
	public JwtDecoder JwtDecoder() {
		return JwtDecoders.fromIssuerLocation(issuerUri);
	}

	@Bean
	RestAccessDeniedHandler accessDeniedHandler() {
		return new RestAccessDeniedHandler();
	}

	@Bean
	RestAuthenticationEntryPoint authenticationEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}

	@Bean
	RestAuthenticationFailureHandler authenticationFailureHandler() {
		return new RestAuthenticationFailureHandler();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// do NOT call super.configure()
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().mvcMatchers(
				// Swagger UI
				"/v3/api-docs/**", 
				"/swagger-ui.html", 
				"/swagger-ui/**",
				// H2 Web Console
				"/h2-console/**",
				"/error/**",
				"/favicon.ico");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeRequests(authorize -> authorize
				.mvcMatchers("/offers/**").hasAuthority(SCOPE_PREFIX + scope)
				.anyRequest().authenticated()
			)
			.exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler())
				.authenticationEntryPoint(authenticationEntryPoint()).and()
			.sessionManagement(cust -> cust.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

		BearerTokenAuthenticationFilter filter = new BearerTokenAuthenticationFilter(authenticationManagerBean());
		filter.setAuthenticationFailureHandler(authenticationFailureHandler());
		http.addFilterBefore(filter, BearerTokenAuthenticationFilter.class);
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
