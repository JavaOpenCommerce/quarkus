package com.example.jcc.quarkus.app.route;

import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HomeController {

    @Route(path="/", methods = HttpMethod.GET)
    public void hello(RoutingContext rc) {
        rc.response().end("Hello. Use different url, for proper response.");
    }

}
