package com.example.jcc.quarkus.app.auth;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GoogleAuthResponse {

    private String access_token;
    private Integer expires_in;
    private String scope;
    private String token_type;
    private String id_token;
    private String error;
    private String message;

    public boolean isError() {
        return this.error != null;
    }
}
