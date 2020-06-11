/*
package com.example.jcc.quarkus.app.auth.response;

import io.quarkus.oidc.IdToken;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

*/
/**
 * Depends on Tenant Resolver. Should not be application scope.
 *//*

@RequestScoped
@Path("/auth/{tenant}")
public class AuthResponse {

    */
/**
     * Injection point for the ID Token issued by the OpenID Connect Provider
     *//*

    private final JsonWebToken idToken;

    @Inject
    @IdToken
    public AuthResponse(final JsonWebToken idToken) {
        this.idToken = idToken;
    }

    */
/**
     * Returns the tokens available to the application. This endpoint exists only for demonstration purposes, you should not
     * expose these tokens in a real application.
     *
     * @return the landing page HTML*//*

    @GET
    public String getHome() {
        return "<html><body>" +
                "<h2>Welcome, " + this.idToken.getClaim("email").toString() + "</h2>\n" +
                "<h3>You are accessing the application within tenant <b>" + idToken.getIssuer() + " boundaries</b></h3>" +
                "</body></html>";
    }
}
*/
