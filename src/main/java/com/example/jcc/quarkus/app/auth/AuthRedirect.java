package com.example.jcc.quarkus.app.auth;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class AuthRedirect {

    @Route(path="/login/github", methods = HttpMethod.POST)
    public void redirect(RoutingContext context) {
        context.response()
                .setStatusCode(HttpResponseStatus.FOUND.code())
                .putHeader(HttpHeaders.LOCATION, "https://github.com/login/oauth/access_token")
                .end();
    }

    @Route(path="/login/github", methods = {HttpMethod.GET, HttpMethod.CONNECT, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.PATCH}, produces = MediaType.APPLICATION_JSON)
    public void getRedirectInfo(RoutingContext context) {
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setStatusCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code())
                .end("{ \"error\" : \"please use POST for this endpoint\"}");
    }
}
