package net.example.batchgateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CreateJwtTokenTest {


    @Test
    @DisplayName("create OAuth2.0 token returning JWT string")
    void createOAuthTokenReturningJwtString() {
        // Given
        String subject = "mySubject";
        ArrayList<String> roles = new ArrayList<>();

        // When
        var res = CreateJwtToken.oAuthToken(subject, roles);

        // Then
        assertNotNull(res);
    }

}
