package com.example.business.config;


import io.vertx.core.Vertx;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SessionProducer {

    private final SessionHandler sessionHandler;
    private final LocalSessionStore sessionStore;

    public SessionProducer(Vertx vertx) {
        sessionStore = LocalSessionStore.create(vertx);
        this.sessionHandler = SessionHandler.create(sessionStore)
                .setMinLength(30)
                .setSessionCookieName("jsessionid")
                .setSessionTimeout(60L * 60L * 1000L);
    }

    public LocalSessionStore getSessionStore() {
        return sessionStore;
    }

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }
}