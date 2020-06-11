package com.example.jcc.quarkus.app.route;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Version 1 of the TokenSecuredResource
 */
@Path("/secured")
@ApplicationScoped
public class TokenSecuredResource {

    @GET()
    @Path("/permit-all")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@Context SecurityContext ctx) {
        Principal caller =  ctx.getUserPrincipal();
        String name = caller == null ? "anonymous" : caller.getName();
        return String.format("hello + %s, isSecure: %s, authScheme: %s", name, ctx.isSecure(), ctx.getAuthenticationScheme());
    }

    @GET()
    @Path("/roles-allowed")
    @RolesAllowed({"user", "Subscriber"})
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRolesAllowed(@Context SecurityContext ctx) {
        Principal caller =  ctx.getUserPrincipal();
        return String.format("hello + %s, isSecure: %s, authScheme: %s", caller.getName(), ctx.isSecure(), ctx.getAuthenticationScheme());
    }

}