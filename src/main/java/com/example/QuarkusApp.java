package com.example;

import com.sun.security.auth.module.UnixSystem;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * This file is not necessary. It's here, only to act as a starting point for IDE to run project.
 * If this file does not exists, the same (without log statement) will be generated by Quarkus.
 */
@Slf4j
@QuarkusMain
public class QuarkusApp {

    public static void main(String... args) throws InterruptedException, IllegalAccessException, InstantiationException {
        log.info("Running quarkus application with parameters: {}", Arrays.asList(args));
        if ("Linux".equals(System.getenv("os.name"))) {
            UnixSystem system = new UnixSystem();
            if (0L == system.getUid() && "root".equals(system.getUsername()))
                throw new InstantiationException("This application cannot be run as a superuser (root) for security reasons.");
        }
        Thread.sleep(6000L);
        Quarkus.run(args);
    }

}