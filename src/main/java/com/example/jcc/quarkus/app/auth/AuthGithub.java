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

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Optional;

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
        String sessionId = ofNullable(rc.session()).map(Session::id).orElse("null");
        final Optional<Session> session = ofNullable(rc.session());
        if (session.isEmpty()) {
            sendUnauthorized(rc, "You are not authorized to perform such requests.");
        }
        ofNullable(rc.queryParam(STATE)).orElse(emptyList()).stream().findFirst()
                .ifPresentOrElse(state -> {
                    var sessionState = rc.session().data().get(STATE);
                    rc.session().data().remove(STATE);
                    if (!state.equals(sessionState)) {
                        sendUnauthorized(rc, "Invalid state token");
                        return;
                    }
                }, () -> {
                    sendUnauthorized(rc,"Parameter 'state' is mandatory.");
                    return;
                }
        );
        log.infof("Got GET request with: %s, incoming session: %s", rc.user().principal(), sessionId);
        final String code = ofNullable(rc.queryParam("code")).orElse(emptyList()).stream().findFirst().orElse("");

        String sb = "code=" + URLEncoder.encode(code, UTF_8) +
                "&client_id=" + URLEncoder.encode(cfg.clientId(), UTF_8) +
                "&client_secret=" + URLEncoder.encode(cfg.clientSecret(), UTF_8) +
                "&redirect_uri=" + "http://localhost:8080/oauth/authorize/google" +
                "&grant_type=authorization_code";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
                .POST(HttpRequest.BodyPublishers.ofString(sb))
                .build();

        final String jsonResponse = HttpClient.newHttpClient()
                .sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenApply(java.net.http.HttpResponse::body)
                .join();
        final JsonObject resp = new JsonObject(jsonResponse);

        final GoogleAuthResponse authResponse = GoogleAuthResponse
                .builder()
                .access_token(resp.getString("access_token"))
                .expires_in(resp.getInteger("expires_in"))
                .scope(resp.getString("scope"))
                .token_type(resp.getString("token_type"))
                .id_token(resp.getString("id_token"))
                .error(resp.getString("error"))
                .message(resp.getString("message"))
                .build();
        if (authResponse.isError()) {
            log.errorf("Authentication failed with message: %s : %s", authResponse.getError(), authResponse.getMessage());
            return;
        }
        log.infof("Logged in user %s", authResponse.toString());

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
