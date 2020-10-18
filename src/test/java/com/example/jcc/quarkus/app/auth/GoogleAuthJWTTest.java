package com.example.jcc.quarkus.app.auth;

import com.example.jcc.quarkus.app.IOUtil;
import io.vertx.core.json.JsonObject;
import lombok.Value;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.example.jcc.quarkus.app.auth.GoogleAuthJWT.TOKEN;
import static org.junit.jupiter.api.Assertions.*;
@Value
class GoogleAuthJWTTest {

    @Test
    void readJsonTest() {
        // given
        String jwt = IOUtil.readFile("googleResponse.json");
        final GoogleAuthJWT googleJWT = GoogleAuthJWT.from(new JsonObject(jwt).getString(TOKEN));
        assertNotNull(googleJWT);
        final Optional<String> email = googleJWT.getPayload().get(GoogleJWTPayload.Fields.EMAIL);
        assertTrue(email.isPresent());
        assertEquals("exampleemail@example.com", email.get());
    }

}