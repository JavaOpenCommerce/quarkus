package com.example.jcc.quarkus.app.route;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;

import static javax.ws.rs.core.Response.Status.OK;

@ApplicationScoped
public class GreetController {

    @Route(path="/hello", methods = HttpMethod.GET, produces = MediaType.APPLICATION_JSON)
    void hello(RoutingContext rc) {
        rc.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .end("{ \"response\" : \"Hello World\" }");
    }

    @Route(path = "/greetings", methods = HttpMethod.GET, produces = MediaType.APPLICATION_JSON)
    void greetings(RoutingExchange ex) {
        ex.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setStatusCode(OK.getStatusCode())
                .end("hello " + ex.getParam("name").orElse("world"));
    }
}
