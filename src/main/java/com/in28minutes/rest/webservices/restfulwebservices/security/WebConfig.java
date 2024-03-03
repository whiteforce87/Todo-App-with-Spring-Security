package com.in28minutes.rest.webservices.restfulwebservices.security;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;


@Configuration
public class WebConfig {

        @Bean
        public ServletContextInitializer servletContextInitializer(CsrfTokenRepository csrfTokenRepository) {
            return new ServletContextInitializer() {
                @Override
                public void onStartup(ServletContext servletContext) throws ServletException {
                    // Set CSRF token cookie as HTTP-only
                    servletContext.getSessionCookieConfig().setHttpOnly(true);
                }
            };
        }

        @Bean
        public CsrfTokenRepository csrfTokenRepository() {
            HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
            // Optionally, you can set additional properties of the CSRF token repository here
            return repository;
        }

    }
