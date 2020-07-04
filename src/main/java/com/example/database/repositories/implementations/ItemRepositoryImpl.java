package com.example.database.repositories.implementations;

import com.example.database.entity.Image;
import com.example.database.entity.Item;
import com.example.database.entity.ItemDetails;
import com.example.database.repositories.interfaces.ItemRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@ApplicationScoped
public class ItemRepositoryImpl implements ItemRepository {

    private final PgPool client;

    public ItemRepositoryImpl(PgPool client) {this.client = client;}

    @Override
    public List<Item> listItemByCategoryId(Long categoryId, int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public List<Item> listItemByProducerId(Long producerId, int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public List<Item> getAll(int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public List<Item> getShippingMethodList() {
        return null;
    }


    @Override
    public Uni<Set<Item>> getAll() {
        return client.preparedQuery("SELECT * FROM Item i " +
                                        "INNER JOIN Image img ON i.image_id = img.id ")
                .onItem().apply(rs -> {
                    Set<Item> items = new HashSet<>();
                    for (Row row : rs) {
                        items.add(rowToItem(row));
                    }
                    return items;
                });
    }

    @Override
    public Uni<Item> getItemById(Long id) {
        return client.preparedQuery("SELECT * FROM Item i " +
                                        "INNER JOIN Image img ON i.image_id = img.id " +
                                        "WHERE i.id = $1", Tuple.of(id))
                .onItem().apply(RowSet::iterator)
                .onItem().apply(it -> it.hasNext() ? rowToItem(it.next()) : null);
    }

    @Override
    public Uni<Set<ItemDetails>> getItemDetailsListByItemId(Long id) {
        return client.preparedQuery("SELECT * FROM ITEMDETAILS WHERE item_id = $1", Tuple.of(id))
                .map(rs -> rs.iterator().hasNext() ? rowToItemDetails(rs) : null);
    }

    private Item rowToItem(Row row) {
        return Item.builder()
                .stock(row.getInteger("stock"))
                .valueGross(BigDecimal.valueOf(row.getDouble("valuegross")))
                .vat(row.getDouble("vat"))
                .id(row.getLong("id"))
                .image(Image.builder()
                        .id(row.getLong("image_id"))
                        .alt(row.getString("alt"))
                        .url(row.getString("url"))
                        .build())
                .build();
    }

    private Set<ItemDetails> rowToItemDetails(RowSet<Row> rs) {
        Set<ItemDetails> result = new HashSet<>(rs.size());
        for (Row row : rs) {
            result.add(ItemDetails.builder()
                    .id(row.getLong("id"))
                    .description(row.getString("description"))
                    .lang(Locale.forLanguageTag(row.getString("lang")))
                    .name(row.getString("name"))
                    .build());
        }
        return result;
    }
}
