package com.in28minutes.rest.webservices.restfulwebservices.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

//@Configuration
public class BasicAuthenticationSecurityConfiguration {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//		1) All requests should be authenticated
		http.authorizeHttpRequests(
				auth -> auth
						//.requestMatchers("/").permitAll() //#CHANGE
						.requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
						.anyRequest().authenticated()
		);
//		2) If a request is not authenticated, a web page is shown
		http.httpBasic(withDefaults());
		;
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//		3) CSRF -> POST, PUT
		http.csrf(csrf -> csrf.disable());


		return http.build();
	}

}
