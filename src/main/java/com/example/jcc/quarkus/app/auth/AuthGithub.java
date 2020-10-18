package com.example.jcc.quarkus.app.auth;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.codec.BodyCodec;
import lombok.extern.jbosslog.JBossLog;
import org.apache.http.entity.ContentType;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@JBossLog
@ApplicationScoped
public class AuthGithub {

    public static final String STATE = "state";
    private final AuthConfig cfg;
    private final SessionProducer sessionStore;
    private final WebClient client;
    private final String googleAuthUrl = "https://oauth2.googleapis.com/token";
    private final String googleRedirectUrl = "http://localhost:8080/oauth/authorize/google";

    public AuthGithub(SessionProducer sess, AuthConfig config, Vertx mVertx) {
        cfg = config;
        sessionStore = sess;
        client = WebClient.create(mVertx, new WebClientOptions()
                .setDefaultHost("oauth2.googleapis.com")
                .setSsl(true).setDefaultPort(443)
                .setConnectTimeout(3)
                .setKeepAlive(false)
        );
    }

    @Route(path = "/oauth/authorized/github", methods = {HttpMethod.GET,HttpMethod.POST})
    public void githubGet(RoutingContext rc) {
        log.infof("Got GET request with: %s", rc.user().principal());
        final String code = ofNullable(rc.queryParam("code")).orElse(emptyList()).stream().findFirst().orElse("null");
        final JsonObject reqParams = new JsonObject()
                .put("code", code)
                .put("client_id", cfg.clientId())
                .put("client_secret", cfg.clientSecret())
                .put("redirect_uri", "http://localhost:8080/oauth/authorize/google")
                .put("grant_type", "authorization_code");

        client.post(443, "github.com", "/login/auth/access_token")
                .putHeader("Accept", "application/json")
                .putHeader("Content-Type", "application/json")
                .as(BodyCodec.jsonObject())
                .sendJsonObject(reqParams)
                .onItem().apply(HttpResponse::body)
                .subscribe().with(
                this::extractResponse,
                this::handleError
        );
    }

    @Route(path = "/redirect/github", methods = HttpMethod.GET)
    public void redirectGH(RoutingContext rc) {
        rc.response()
                .setStatusCode(HttpResponseStatus.FOUND.code())
                .putHeader(HttpHeaders.LOCATION, "https://github.com/login/oauth/authorize?client_id=d024f3d8f1c3eaa9f993&redirect_uri=http://localhost:8080/oauth/authorized/github")
                .end();
    }

    @Route(path = "/oauth/authorize/google", methods = {HttpMethod.GET})
    public void authorizeGoogle(RoutingContext rc) throws IOException, InterruptedException {
        sessionStore.getSessionHandler().handle(rc);
        if (!validateAuthorizedUser(rc)) {
            log.debug("Authentication failed, user state token is invalid or missing session");
            return;
        }
        String sessionId = ofNullable(rc.session()).map(Session::id).orElse("null");
        log.infof("Got GET request with: %s, incoming session: %s", rc.user().principal(), sessionId);
        final String code = ofNullable(rc.queryParam("code")).orElse(emptyList()).stream().findFirst().orElse("");

        String params = "code=" + code +
                "&grant_type=authorization_code" +
                "&client_id=" + cfg.clientId() +
                "&client_secret=" + cfg.clientSecret() +
                "&redirect_uri=" + googleRedirectUrl;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(googleAuthUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .build();

        final String jsonResponse = HttpClient.newHttpClient()
                .sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenApply(java.net.http.HttpResponse::body)
                .join();

        final GoogleAuthResponse authResponse = GoogleAuthResponse.fromJson(jsonResponse);
        if (authResponse.isError()) {
            log.errorf("Authentication failed with message: %s : %s", authResponse.getError(), authResponse.getMessage());
            return;
        }
        log.infof("Logged in user %s", authResponse.toString());
//eyJhbGciOiJSUzI1NiIsImtpZCI6IjZiYzYzZTlmMThkNTYxYjM0ZjU2NjhmODhhZTI3ZDQ4ODc2ZDgwNzMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI3MjI2NzEyNTg5NDctZGtwOGhhOWplcWszN2I1ZmhzZHFsNGp0MW51bm5tbWguYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI3MjI2NzEyNTg5NDctZGtwOGhhOWplcWszN2I1ZmhzZHFsNGp0MW51bm5tbWguYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDE3Njk3NzE2MDg3MzMwNzY1MDIiLCJlbWFpbCI6ImF1Z3VzdHlud2lsa0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6IjAxNG9LanVaQmktX3pvTXFiS3BiSXciLCJub25jZSI6IjAzOTQ4NTIzMTkwNDg1MjQ5MDM1OCIsImlhdCI6MTU5NzY4MTE2NSwiZXhwIjoxNTk3Njg0NzY1fQ.Wz0nmGzXv5Va_KfJ_xc0vYyRYjHvl_ZWzl3k9rFh1QmYz1PSJtRrCPy5M303pQr2dUGR_HUz01xwbbkG3gow6V1kRKM546W9oT_B4py44HjP5btpSs9MuS3OFLh-hCbXB-wg9eplm66ujdqmz_uX-xDMGrwtFbNYJpe7Mwgaj0U7DzQtMkuyIa0ABPsQBrX_Pq2pb49mGPJw-4y4OEvoKAwm83V74vYf4p044RN002GXz6coLgiD6VC03SBzkvLGHDY2cOxzFvDRl_rck2HU79RZyV23NLT_JvJcLCDqP7j_efwXxELueeNunZetBTzJs5lx5dLppkl_7PtL7-fjGQ
    }

    private boolean validateAuthorizedUser(RoutingContext rc) {
        final Optional<Session> session = ofNullable(rc.session());
        if (session.isEmpty()) {
            sendUnauthorized(rc, "You are not authorized to perform such requests.");
            return false;
        }
        AtomicBoolean isValid = new AtomicBoolean(true);
        ofNullable(rc.queryParam(STATE)).orElse(emptyList()).stream().findFirst()
                .ifPresentOrElse(state -> {
                    var sessionState = rc.session().data().get(STATE);
                    rc.session().data().remove(STATE);
                    if (!state.equals(sessionState)) {
                        sendUnauthorized(rc, "Invalid state token");
                        isValid.set(false);
                    }
                }, () -> {
                    sendUnauthorized(rc,"Parameter 'state' is mandatory.");
                    isValid.set(false);
                }
        );
        return isValid.get();
    }

    private Throwable handleError(Throwable err) {
        log.error("Failed with error: ", err);
        return err;
    }
    private void extractResponse(JsonObject resp) {
        log.infof("Received response: %s", resp);
//        return resp.bodyAsJson(GoogleAuthResponse.class);
    }
    private String extractResponse(HttpResponse<String> resp) {
        log.infof("Extracting received response: %s", resp.body());
        return resp.bodyAsJson(GoogleAuthResponse.class).toString();
    }

    @Route(path = "/oauth/authorize/google", methods = {HttpMethod.POST})
    public void authenticateViaGoogle(RoutingContext rc) {
        log.infof("Received response from google...%s", rc.getBodyAsJson());
    }

    private void sendUnauthorized(RoutingContext rc, final String message) {
        rc.response()
                .setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
                .end(message);
    }

}
