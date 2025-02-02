package net.example.batchgateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String contentSecurityPolicyHeader = "frame-ancestors 'none'";

    private final boolean disabledSecurity = false;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        if (disabledSecurity) {
            logger.warn("############# Security disabled! ##############");
            http
                    .csrf(csrf -> csrf
                            .disable()
                    );
            return http.build();
        }

        http
                .csrf(csrf -> csrf
                        .disable()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(header -> header
                        .contentSecurityPolicy(content -> content
                                .policyDirectives(contentSecurityPolicyHeader)
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/readyz", "/livez", "/info/version", "/actuator/**").permitAll()
                        .requestMatchers("/batch/**").permitAll() // TODO: make this more strict...
                        .anyRequest().authenticated()
                )

        ;
        return http.build();
    }

}
