package com.example.jcc.quarkus.app.auth;

import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.jbosslog.JBossLog;
import lombok.extern.log4j.Log4j2;

import javax.enterprise.context.ApplicationScoped;

@JBossLog
@ApplicationScoped
public class AuthGithub {

    @Route(path = "/oauth/authorize2/github", methods = {HttpMethod.GET,HttpMethod.POST})
    public void githubGet(RoutingContext rc) {
        log.infof("Got GET request with: %s", rc.user().principal());
    }

}
