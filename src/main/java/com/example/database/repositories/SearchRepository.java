package com.example.database.repositories;

import com.example.database.entity.Item;
import io.quarkus.runtime.StartupEvent;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.mapper.orm.Search;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class SearchRepository {

    private final EntityManager em;

    public SearchRepository(EntityManager em) {this.em = em;}

    @Transactional
    void onStart(@Observes StartupEvent ev) throws InterruptedException {
        Search.session(em)
                .massIndexer()
                .startAndWait();
    }

    public SearchQuery<Item> searchForAProduct(String pattern) {
        return Search.session(em)
                .search(Item.class)
                .where(f -> pattern == null || pattern.trim().isEmpty() ? f.matchAll() : f.bool()
                        .mustNot(
                                f.match()
                                        .field("category.categoryName")
                                        .matching("Shipping"))
                        .should(
                                f.wildcard()
                                        .fields("details.name", "details.description")
                                        .matching("*" + pattern + "*"))).toQuery();

    }
}
