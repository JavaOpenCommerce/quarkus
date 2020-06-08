package com.example.jcc.quarkus.app.auth;

import io.quarkus.oidc.TenantResolver;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomTenantResolver implements TenantResolver {

    @Override
    public String resolve(RoutingContext context) {
        String[] parts = context.request().path().split("/profile/");

        if (parts.length == 0) {
            // resolve to default tenant configuration
            return null;
        }

        return parts[1];
    }
}
