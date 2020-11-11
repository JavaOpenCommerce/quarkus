package com.example.database.repositories.implementations;

import com.example.database.entity.ItemQuantity;
import com.example.database.repositories.interfaces.ItemQuantityRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@ApplicationScoped
public class ItemQuantityRepositoryImpl implements ItemQuantityRepository {

    private final PgPool client;

    public ItemQuantityRepositoryImpl(PgPool client) {
        this.client = client;
    }

    @Override
    public Uni<List<ItemQuantity>> getItemQuantitiesByOrderId(Long id) {
        return client.preparedQuery("SELECT * FROM ITEMQUANTITY WHERE order_id = $1", Tuple.of(id))
                .onItem().apply(this::rowSetToItemQuantityList);
    }

    @Override
    public Uni<ItemQuantity> saveItemQuantity(ItemQuantity itemQuantity) {
        return client.preparedQuery("INSERT INTO ITEMQUANTITY (amount, item_id, order_id) " +
                                        "VALUES($1, $2, $3)", Tuple.of(
                                                itemQuantity.getAmount(),
                                                itemQuantity.getItemId(),
                                                itemQuantity.getOrderId()))
                .onItem().apply(rs -> {
            if (rs == null || !rs.iterator().hasNext()) {
                return ItemQuantity.builder().build();
            }
            return rowToItemQuantity(rs.iterator().next());
        });
    }

    //--Helpers-----------------------------------------------------------------------------------------------------

    private ItemQuantity rowToItemQuantity(Row row) {
        if (row == null) {
            return ItemQuantity.builder().build();
        }

        return ItemQuantity.builder()
                .id(row.getLong("id"))
                .amount(row.getInteger("amount"))
                .itemId(row.getLong("item_id"))
                .orderId(row.getLong("order_id"))
                .build();
    }

    private List<ItemQuantity> rowSetToItemQuantityList(RowSet<Row> rs) {
        if (isRowSetEmpty(rs)) {
            return emptyList();
        }

        return stream(rs.spliterator(), false)
                .map(this::rowToItemQuantity)
                .collect(toList());
    }

    private boolean isRowSetEmpty(RowSet<Row> rs) {
        return rs == null || !rs.iterator().hasNext();
    }
}
