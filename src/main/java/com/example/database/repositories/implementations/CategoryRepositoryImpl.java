package com.example.database.repositories.implementations;

import com.example.database.entity.Category;
import com.example.database.entity.CategoryDetails;
import com.example.database.repositories.interfaces.CategoryRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@ApplicationScoped
public class CategoryRepositoryImpl implements CategoryRepository {

    private final PgPool client;

    public CategoryRepositoryImpl(PgPool client) {this.client = client;}


    @Override
    public Uni<Set<Category>> getAll() {
        return client.preparedQuery("SELECT * FROM Category c " +
                                        "INNER JOIN item_category ic ON ic.category_id = c.id " +
                                        "INNER JOIN CategoryDetails cd ON cd.category_id = c.id ")
                .onItem().apply(rs -> rs.iterator().hasNext() ? rowToCategory(rs) : null);
    }

    @Override
    public Uni<Set<Category>> getCategoriesByItemId(Long id) {
        return client.preparedQuery("SELECT * FROM Category c " +
                                        "INNER JOIN item_category ic ON ic.category_id = c.id " +
                                        "INNER JOIN CategoryDetails cd ON cd.category_id = c.id " +
                                        "WHERE ic.item_id = $1", Tuple.of(id))
                .onItem().apply(rs -> rs.iterator().hasNext() ? rowToCategory(rs) : null);
    }

    private Set<Category> rowToCategory(RowSet<Row> rows) {
        Set<Category> result = new HashSet<>(rows.size());

        for (Row row : rows) {
            if (isCategoryAlreadyMapped(result, row)) {
                Category category = result.stream()
                        .filter(i -> i.getId() == row.getLong("item_id"))
                        .findFirst()
                        .get();

                category.getDetails().add(rowToCategoryDetail(row));
            } else {
                Set<CategoryDetails> details = new HashSet<>();
                details.add(rowToCategoryDetail(row));
                result.add(Category.builder()
                        .id(row.getLong("category_id"))
                        .details(details)
                        .build());
            }
        }
        return result;
    }

    private CategoryDetails rowToCategoryDetail(Row row) {
        return CategoryDetails.builder()
                .id(row.getLong(3)) //because it is present twice in a set
                .name(row.getString("name"))
                .description(row.getString("description"))
                .lang(Locale.forLanguageTag(row.getString("lang")))
                .build();
    }

    private boolean isCategoryAlreadyMapped(Set<Category> result, Row row) {
        return result.stream().anyMatch(i -> i.getId() == row.getLong("item_id"));
    }
}
