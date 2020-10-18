package com.example.jcc.quarkus.app.auth;

import io.vertx.core.json.JsonObject;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GoogleAuthResponse {

    private String accessToken;
    private Integer expiresIn;
    private String scope;
    private String tokenType;
    private String idToken;
    private String error;
    private String message;

    public static GoogleAuthResponse fromJson(String jsonString) {
        return GoogleAuthResponse.fromJson(new JsonObject(jsonString));
    }

    public static GoogleAuthResponse fromJson(JsonObject json) {
        return GoogleAuthResponse.builder()
                .accessToken(json.getString("access_token"))
                .expiresIn(json.getInteger("expires_in"))
                .scope(json.getString("scope"))
                .tokenType(json.getString("token_type"))
                .idToken(json.getString("id_token"))
                .error(json.getString("error"))
                .message(json.getString("message"))
                .build();
    }

    public boolean isError() {
        return this.error != null;
    }

    public GoogleAuthJWT getToken() {
        return GoogleAuthJWT.from(this.idToken);
    }
}
