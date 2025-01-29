package net.example.batchgateway.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Configuration
public class CreateJwtToken {

    private static final String secret = "841D8A6C80CBA4FDAC44D5367C18C53B";  // not really a secret
    private static final String Issuer = "Cryptoworker";


    private static JwtEncoder jwtEncoder() {
        SecretKey key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<>(key);
        return new NimbusJwtEncoder(immutableSecret);
    }

    public static String oAuthToken(String subject, List<String> roles) {
        return createOAuthToken(subject, roles).getTokenValue();
    }

    private static Jwt createOAuthToken(String subject, List<String> roles) {

        var claimsBuilder = JwtClaimsSet.builder()
                .issuer(Issuer)
                .subject(subject)
                .audience(Arrays.asList("myAudience", "Bob"))
                .claim("typ", "Bearer")
                .claim("azp", "api-services")
                .claim("acr", "1")
                .claim("scope", "");
        if (!roles.isEmpty()) {
            claimsBuilder.claim("roles", roles);
        }

        return createToken(claimsBuilder);
    }

//    https://connect2id.com/products/nimbus-jose-jwt/examples/jwe-with-shared-key
    private static Jwt createToken(JwtClaimsSet.Builder claimsSetBuilder) {
        Instant iat = Instant.now();
        JwtClaimsSet.Builder claimsBuilder = claimsSetBuilder
                .issuedAt(iat)
                .expiresAt(iat.plusSeconds(300))
                .id(UUID.randomUUID().toString());

        JwtClaimsSet claims = claimsBuilder.build();

        var jwsHeader = JwsHeader
                .with(() -> "HS256")
                .type("JWT")
                .header("keyid", UUID.randomUUID().toString())
                .build();

        return jwtEncoder().encode(JwtEncoderParameters.from(jwsHeader, claims));
    }

}
