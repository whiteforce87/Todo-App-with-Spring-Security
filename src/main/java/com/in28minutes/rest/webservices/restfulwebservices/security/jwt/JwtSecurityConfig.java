package com.in28minutes.rest.webservices.restfulwebservices.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JwtSecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.security.user.query}")
    String usersQuery;

    @Value("${spring.security.authority.query}")
    String authorityQuery;

    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity

                .cors() // Enable CORS
                .and()
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .csrf(AbstractHttpConfigurer::disable) // (1)
                .sessionManagement(
                        session -> 
                            session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS)
                //.maximumSessions(1).maxSessionsPreventsLogin(true)
                )
                .cors().configurationSource(request -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Allow requests from any origin (you can restrict this if needed)
                    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Allow only specific HTTP methods
                    corsConfig.setAllowedHeaders(Collections.singletonList("*")); // Allow any headers
                    corsConfig.setAllowCredentials(true); // Allow credentials (cookies)
                    return corsConfig;
                }).and()

                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout")
                .deleteCookies(cookieName) // Specify the name of your authentication cookie
                .invalidateHttpSession(true).and()

                .authorizeHttpRequests(
                        auth -> 
                            auth.requestMatchers("/", //#CHANGE
                            		"/authenticate","/registerUser","/actuator/**","/swagger-ui.html","/swagger-ui/**"
                                            ,"/v3/**","/explorer/**","/refreshToken","/checkAuth")
                                .permitAll()
                                    .requestMatchers(PathRequest.toH2Console())
                                    .permitAll()
                                    .requestMatchers(HttpMethod.OPTIONS,"/**")
                                .permitAll()
                                 //  .requestMatchers(HttpMethod.POST,"/updateUser").hasAnyAuthority("ROLE_USER","ROLE_ADMIN")

                                    .anyRequest()
                                .authenticated()) // (3)
                 //.addFilterAfter(new JwtRequestFilter(), UsernamePasswordAuthenticationFilter.class) // Add JWT token filter

                .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults())) // (4)
                .exceptionHandling(
                        (ex) -> 
                            ex.authenticationEntryPoint(
                                new BearerTokenAuthenticationEntryPoint())
                              .accessDeniedHandler(
                                new BearerTokenAccessDeniedHandler()))
                //.httpBasic(
                //        withDefaults()) // (5)
                .headers()
                                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER).and()
                                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN).and()
                                .xssProtection()
                                .and()
                                .contentSecurityPolicy("form-action 'self'").and()
                                .addHeaderWriter(new StaticHeadersWriter("Feature-Policy", "accelerometer 'none';" +
                                        " ambient-light-sensor 'none';" +
                                        " animations 'self';" +
                                        " autoplay 'none';" +
                                        " camera 'none';" +
                                        " cookie 'self';" +
                                        " encrypted-media 'none';" +
                                        " fullscreen 'self';" +
                                        " geolocation 'none';" +
                                        " gyroscope 'none';" +
                                        " magnetometer 'none';" +
                                        " max-downscaling-image 'self';" +
                                        " microphone 'none';" +
                                        " midi 'none';" +
                                        " payment 'none';" +
                                        " picture-in-picture 'self';" +
                                        " speaker 'none';" +
                                        " sync-script 'self';" +
                                        " sync-xhr 'self';" +
                                        " unsized-media 'self';" +
                                        " usb 'none';" +
                                        " vertical-scroll 'self';" +
                                        " vr 'none'"))
                .addHeaderWriter(new StaticHeadersWriter("X-XSS-Protection", "1; mode=block"))
                .referrerPolicy()
                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN).and()
                .frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()).and()


                .build();



    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        return  new SessionRegistryImpl();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService) {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
/*This is for inMemoryAuthentication
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("fatih")
                                .password("{noop}dummy2")
                                .authorities("read")
                                .roles("USER")
                                .build();

        return new InMemoryUserDetailsManager(user);
    }

 */
/*This is for embeddedH2 jdbc authentication
    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }
*/
    @Bean
    public UserDetailsService userDetailService(DataSource dataSource){
      /* This are for create user when application start
      var admin = User.withUsername("fatih")
                //.password("{noop}Akgüç")
                .password("Akgüç")
                .passwordEncoder(str-> passwordEncoder().encode(str))
                .roles("ADMIN","USER")
                .build();

        var user = User.withUsername("tugce")
                //.password("{noop}Akgüç")
                .password("Akgüç")
                .passwordEncoder(str-> passwordEncoder().encode(str))
                .roles("USER")
                .build();
*/
        var jdbsUserDetailManager = new JdbcUserDetailsManager(dataSource);
       // jdbsUserDetailManager.createUser(user);
       // jdbsUserDetailManager.createUser(admin);
        jdbsUserDetailManager.setUsersByUsernameQuery(usersQuery);
        jdbsUserDetailManager.setAuthoritiesByUsernameQuery(authorityQuery);

        return jdbsUserDetailManager;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        JWKSet jwkSet = new JWKSet(rsaKey());
        return (((jwkSelector, securityContext) 
                        -> jwkSelector.select(jwkSet)));
    }

    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder
                .withPublicKey(rsaKey().toRSAPublicKey())
                .build();
    }
    
    @Bean
    public RSAKey rsaKey() {
        
        KeyPair keyPair = keyPair();
        
        return new RSAKey
                .Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public KeyPair keyPair() {
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to generate an RSA Key Pair", e);
        }
    }

    //----------------For swagger ui ------------------
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("My REST API")
                        .description("Some custom description of API.")
                        .version("1.0").contact(new Contact().name("Fatih Akguc")
                                .email( "www.baeldung.com").url("fatih_akguc@hotmail.com"))
                        .license(new License().name("License of API")
                                .url("API license URL")));
    }

}


