package com.example.business.config;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.redis.client.Redis;
import io.vertx.reactivex.redis.client.RedisAPI;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@JBossLog
@ApplicationScoped
public class RedisClientConfig {

    @ConfigProperty(name = "com.example.redis.connection")
    String connectionString;

    @Produces
    public RedisAPI redisAPI() {

        Redis client = Redis
                .createClient(Vertx.vertx(), connectionString)
                .connect(result -> {
                    if (result.succeeded()) {
                        log.info("Successfully connected to Redis");
                    } else {
                        log.warnf("Failed to connect to Redis with message: {}", result.cause());
                    }
                });
        return RedisAPI.api(client);
    }
}
