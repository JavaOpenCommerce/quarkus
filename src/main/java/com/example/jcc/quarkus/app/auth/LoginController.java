package com.example.jcc.quarkus.app.auth;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.vertx.web.Route;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class LoginController {
    private final Random random;
    private final SessionProducer session;

    public LoginController(SessionProducer sesProd) throws NoSuchAlgorithmException {
        random = SecureRandom.getInstanceStrong();
        session = sesProd;
    }

    @Route(path="/login/github", methods = HttpMethod.GET)
    public void redirect(RoutingContext context) {
        context.response()
                .setStatusCode(HttpResponseStatus.FOUND.code())
                .putHeader(HttpHeaders.LOCATION, "https://github.com/login/oauth/access_token")
                .end();
    }

    @Route(path="/login/github", methods = {HttpMethod.POST, HttpMethod.CONNECT, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.PATCH}, produces = MediaType.APPLICATION_JSON)
    public void getRedirectInfo(RoutingContext context) {
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setStatusCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code())
                .end("{ \"error\" : \"please use GET for this endpoint\"}");
    }

    //TODO: this logic (except getting strong state, and nonce) should be in front-end project.
    @Route(path="/login/google", methods = HttpMethod.GET)
    public void redirectGoogle(RoutingContext context) {
        session.getSessionHandler().handle(context);
        final String clientId = ConfigProvider.getConfig().getOptionalValue("com.example.oauth2.client-id", String.class)
                .orElseThrow(() -> new BadRequestException("No client Id given"));
        final Session ctxSess = Optional.ofNullable(context.session())
                .orElseThrow(() -> new IllegalStateException("Session not found. This request cannot be processed"));
        final String state = new BigInteger(130, random).toString(32);
        ctxSess.put("state", state);
        this.session.getSessionStore().put(ctxSess, AsyncResult::succeeded);

        context.response()
                .setStatusCode(HttpResponseStatus.FOUND.code())
                .putHeader(HttpHeaders.LOCATION, "https://accounts.google.com/o/oauth2/v2/auth?" +
                        "response_type=code&" +
                        "client_id=" + clientId + "&" +
                        "scope=openid profile email&" +
                        "redirect_uri=http://localhost:8080/oauth/authorize/google&" +
                        "state=" + state + "&" +
                        "nonce=039485231904852490358")
                .end();
    }
}
