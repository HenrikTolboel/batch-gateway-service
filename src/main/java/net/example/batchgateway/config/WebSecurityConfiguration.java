package net.example.batchgateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String issuerUri = "Cryptoworker";

    private final String contentSecurityPolicyHeader = "frame-ancestors 'none'";

    @Value("${org.springframework.boot.test.context.SpringBootTestContextBootstrapper:false}")
    private boolean isSpringBootTest;


    private final boolean disabledSecurity = false;

    private final String secret = "841D8A6C80CBA4FDAC44D5367C18C53B";

    private final SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");


//    https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
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
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(withDefaults())
                )
        ;
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        final OAuth2TokenValidator<Jwt> withClockSkew = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(60)),
                new JwtIssuerValidator(issuerUri),
                new JwtClaimValidator<String>("azp", azp -> azp.equals("api-services"))
        );

        final NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).build();

        jwtDecoder.setJwtValidator(withClockSkew);

        return jwtDecoder;
    }
}
