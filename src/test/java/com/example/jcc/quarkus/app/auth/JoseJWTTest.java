package com.example.jcc.quarkus.app.auth;

import com.example.jcc.quarkus.app.IOUtil;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

class JoseJWTTest {

    @Test
    void deserializeJWTTest() {

        final JsonObject string = IOUtil.readJson("googleResponse.json");
        final String jwtValue = string.getString("id_token").split("\\.")[1];
        final JsonObject jwt = new JsonObject(new String(Base64.getDecoder().decode(jwtValue)));
        Assertions.assertNotNull(jwt.getString("email"));
        Assertions.assertEquals("exampleemail@example.com",jwt.getString("email"));
        Assertions.assertNotNull(jwt.getBoolean("email_verified"));
        Assertions.assertTrue(jwt.getBoolean("email_verified"));
        Assertions.assertNotNull(jwt.getString("aud"));
        Assertions.assertEquals("722771259999-examplegoogleaccountidentification.apps.googleusercontent.com", jwt.getString("aud"));
        Assertions.assertNotNull(jwt.getString("azp"));
        Assertions.assertEquals("722771259999-examplegoogleaccountidentification.apps.googleusercontent.com", jwt.getString("azp"));
    }

}
