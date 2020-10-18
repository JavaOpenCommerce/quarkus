package com.example.jcc.quarkus.app.auth;

import io.vertx.core.json.JsonObject;
import lombok.Getter;

import java.util.*;

@Getter
public class GoogleAuthJWT {
    public static final String TOKEN = "id_token";
    private final JsonObject header;
    private final GoogleJWTPayload payload;
    private final String signature;

    GoogleAuthJWT(String header, String body, String tail) {
        this.header = new JsonObject(decodeBase64(header));
        this.payload = GoogleJWTPayload.from(new JsonObject(decodeBase64(body)));
        this.signature = tail;
    }

    public static GoogleAuthJWT from(String[] strArr) {
        final String[] jsonArr = Optional.ofNullable(strArr).
                filter(arr -> arr.length == 3)
                .orElseThrow(() -> new IllegalArgumentException("JWT array must be exactly 3 elements according to OpenId spec."));
        return new GoogleAuthJWT(jsonArr[0], jsonArr[1], jsonArr[2]);
    }

    public static GoogleAuthJWT from(String jsonJwt) {
        return from(Optional.ofNullable(jsonJwt).orElse("..").split("\\."));
    }

    public GoogleJWTPayload getPayload() {
        return this.payload;
    }

    private String decodeBase64(String body) {
        return new String(Base64.getDecoder().decode(body));
    }

}
