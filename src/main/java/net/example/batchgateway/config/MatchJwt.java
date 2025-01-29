package net.example.batchgateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MatchJwt {

    private final String ACCESS_FORBIDDEN = "BearerToken does not allow access to this service endpoint";

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Authentication authentication;

    public MatchJwt(Authentication authentication) {
        this.authentication = authentication;
    }

    public boolean isAuthenticated() {
        if (! (authentication instanceof AnonymousAuthenticationToken) && ! authentication.isAuthenticated()) {
            logger.warn(ACCESS_FORBIDDEN + " - is not an authenticated user");
            return false;
        }
        return true;
    }

    public boolean tokenAuthenticated(List<JwtAuthorization> jwtAuthorizationList) {
        boolean isAcceptNoAuthHeader = isAcceptNoAuthHeader(jwtAuthorizationList);
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (isAcceptNoAuthHeader) {
                return true;
            }
            return false;
        }

        if (! isAcceptNoAuthHeader) {
            return true;
        }

        if (jwtAuthorizationList.isEmpty()) {
            return true;
        }

        return true;
    }

    private static boolean isAcceptNoAuthHeader(List<JwtAuthorization> jwtAuthorizationList) {
        if (! jwtAuthorizationList.isEmpty()) {
            AtomicBoolean authenticated = new AtomicBoolean(false);
            jwtAuthorizationList.forEach(jwtAuthorization -> {
                if (jwtAuthorization.acceptNoAuthorizationHeader()) {
                    authenticated.set(true);
                }
            });
            if (authenticated.get()) {
                return true;
            }
        }
        return false;
    }
}
