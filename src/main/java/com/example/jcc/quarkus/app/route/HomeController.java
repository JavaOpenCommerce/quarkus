package com.example.jcc.quarkus.app.route;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class HomeController {

    @Path("/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello. Use different url, for proper response.";
    }

}
