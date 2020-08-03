package com.example.quarkus.app;

import com.example.business.config.SessionProducer;
import com.example.database.entity.Product;
import com.example.rest.services.CardDtoService;
import io.quarkus.vertx.web.Route;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@ApplicationScoped
public class CardController {

    private final SessionProducer sessionProducer;
    private final CardDtoService cardDtoService;

    public CardController(SessionProducer sessionProducer, CardDtoService cardDtoService) {
        this.sessionProducer = sessionProducer;
        this.cardDtoService = cardDtoService;
    }

    @Route(path = "/card/persist", methods = {HttpMethod.GET})
    public void redisAddTest(RoutingContext rc) {
        sessionProducer.getSessionHandler().handle(rc);

        Session session = ofNullable(rc.session())
                .orElseThrow(() -> new IllegalStateException("Session not found. This request cannot be processed"));

        //bootstrap for tests
        Product product1 = Product.builder().amount(5).itemId(1L).build();
        Product product2 = Product.builder().amount(3).itemId(2L).build();

        session.put("card", List.of(product1, product2));

        sessionProducer.getSessionStore().put(session, AsyncResult::succeeded);

        rc.response().end("OK");
    }

    @Route(path = "/card/get", methods = {HttpMethod.GET})
    public void redisGetTest(RoutingContext rc) {
        sessionProducer.getSessionHandler().handle(rc);

        List<Product> productList;
        if (nonNull(rc.session()) && nonNull(rc.session().get("card"))) {
            productList = rc.session().get("card");
        } else {
            productList = emptyList();
        }
        cardDtoService.getCard(productList, rc).subscribe().with(card -> {

            rc.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .end(Json.encodePrettily(card));
        }, fail -> new IllegalStateException(fail.getMessage()));
    }
}
