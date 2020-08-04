package com.example.database.repositories.implementations;

import com.example.database.entity.Product;
import com.example.database.repositories.interfaces.CardRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.Promise;
import io.vertx.redis.client.RedisAPI;
import lombok.extern.jbosslog.JBossLog;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.vertx.mutiny.core.Promise.promise;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@JBossLog
@ApplicationScoped
public class CardRepositoryImpl implements CardRepository {

    private final RedisAPI redisAPI;
    private final Jsonb jsonb = JsonbBuilder.create();

    public CardRepositoryImpl(RedisAPI redisAPI) {
        this.redisAPI = redisAPI;
    }

    @Override
    public Uni<List<Product>> getCardList(String id) {
        Promise<List<Product>> promise = promise();
        redisAPI.get(id, res -> {
            if (!res.succeeded()) {
                log.warnf("Failed to store in redis, with message: %", res.cause());
            }
            promise.complete(ofNullable(res.result())
                            .map(r -> jsonToPojo(r.toString()))
                            .orElse(emptyList()));
        });
        return promise.future().onComplete();
    }

    @Override
    public void saveCard(String id, List<Product> products) {
        redisAPI.set(List.of(id, Json.encode(products)),res -> {
            if (res.succeeded()) {
                log.info("Card successfully persisted in redis");
            } else {
                log.warn(res.cause());
            }
        });
    }

    private List<Product> jsonToPojo(String json) {
        return jsonb.fromJson(json, new ArrayList<Product>() {}.getClass().getGenericSuperclass());
    }
}
