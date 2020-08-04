package com.example.quarkus.app;

import com.example.database.entity.Product;
import com.example.rest.dtos.CardDto;
import com.example.rest.services.CardDtoService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.Promise;
import io.vertx.redis.client.RedisAPI;
import lombok.extern.jbosslog.JBossLog;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static io.vertx.mutiny.core.Promise.promise;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@JBossLog
@Path("/card")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CardController {

    @Context
    private HttpServerRequest request;

    private final CardDtoService cardDtoService;
    private final RedisAPI redisAPI;
    private final Jsonb jsonb = JsonbBuilder.create();

    private final String COOKIE_NAME = "CardCookie";

    public CardController(CardDtoService cardDtoService, RedisAPI redisAPI) {
        this.cardDtoService = cardDtoService;
        this.redisAPI = redisAPI;
    }

    @GET
    @Path("/save")
    public String persistCard() {

        addCookieIfNotPresent();

        //temporary bootstrap
        Product product1 = Product.builder().amount(5).itemId(1L).build();
        Product product2 = Product.builder().amount(3).itemId(2L).build();

        redisAPI.set(List.of("lol", Json.encode(List.of(product1, product2))), res -> {
            if (res.succeeded()) {
                log.info("Card successfully persisted in redis");
            } else {
                log.warn(res.cause());
            }
        });

        return HttpResponseStatus.CREATED.toString();
    }

    @GET
    @Path("/get")
    public Uni<CardDto> getCard() {

        addCookieIfNotPresent();

        String cardCookie = request.getCookie(COOKIE_NAME).getValue();

        Promise<List<Product>> promise = promise();
        redisAPI.get(cardCookie, res -> {
            List<Product> products;
            if (res.succeeded() && nonNull(res.result())) {
                products = jsonToPojo(res.result().toString());
            } else {
                log.warn(res.cause());
                products = emptyList();
            }
            promise.complete(products);
        });

        return promise
                .future()
                .onComplete()
                .onItem()
                .apply(products -> cardDtoService.getCard(products))
                .await().indefinitely();
    }

    //helper methods
    private List<Product> jsonToPojo(String json) {
        return jsonb.fromJson(json, new ArrayList<Product>() {}.getClass().getGenericSuperclass());
    }

    private boolean cookieCheck() {
        return isNull(request.getCookie(COOKIE_NAME)) || isNull(request.getCookie(COOKIE_NAME).getValue());
    }

    private void addCookieIfNotPresent() {
        if (cookieCheck()) {
            request.cookieMap().put(COOKIE_NAME, Cookie.cookie(COOKIE_NAME, generateValue()));
        }
    }

    private String generateValue() {
        //todo
        return "lol";
    }
}
