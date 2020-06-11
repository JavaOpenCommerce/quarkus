package com.example.jcc.quarkus.app.auth;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "com.example.oauth2")
public interface AuthConfig {

    @ConfigProperty(name = "client-id")
    String clientId();
    @ConfigProperty(name = "client-secret")
    String clientSecret();


}
