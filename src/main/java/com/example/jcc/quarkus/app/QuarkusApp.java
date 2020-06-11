package com.example.jcc.quarkus.app;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.jbosslog.JBossLog;

import javax.enterprise.event.Observes;

@JBossLog
@QuarkusMain
public class QuarkusApp {
    // tag::main[]
    public static void main(String... args) {
        log.info("Starting quarkus application...");
        // Do not do business logic in #main method.
        Quarkus.run(args);
    }
    // end::main[]
    void onStart(@Observes StartupEvent event) {
        log.info("Application started...");
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
    }

}
