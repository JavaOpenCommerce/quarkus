package com.example.business.config;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;


@ApplicationScoped
public class RedisClientConfig {

    @Produces
    public RedisAPI redisAPI() {
        Redis client = Redis
                .createClient(Vertx.vertx(), SocketAddress.inetSocketAddress(6380, "localhost"))
                .connect(AsyncResult::succeeded);
        return RedisAPI.api(client);
    }
}
