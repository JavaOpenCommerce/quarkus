package com.example.database.repositories.implementations;

import com.example.database.entity.Image;
import com.example.database.entity.Producer;
import com.example.database.entity.ProducerDetails;
import com.example.database.repositories.interfaces.ProducerRepository;
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
public class ProducerRepositoryImpl implements ProducerRepository {

    private final PgPool client;

    public ProducerRepositoryImpl(PgPool client) {this.client = client;}

    @Override
    public Uni<Producer> getProducerByItemId(Long id) {
        return client.preparedQuery("SELECT * FROM Producer p " +
                                        "INNER JOIN Image img ON p.image_id = img.id " +
                                        "INNER JOIN ProducerDetails pd ON pd.producer_id = p.id " +
                                        "INNER JOIN Item i ON i.producer_id = p.id " +
                                        "WHERE i.id = $1", Tuple.of(id))
                .map(rs -> rowToProducer(rs));
    }

    private Producer rowToProducer(RowSet<Row> rs) {

        Set<ProducerDetails> details = new HashSet<>();

        for (Row row : rs) {
            details.add(ProducerDetails.builder()
                    .id(row.getLong(5)) //because it is present twice in a set
                    .name(row.getString("name"))
                    .description(row.getString("description"))
                    .lang(Locale.forLanguageTag(row.getString("lang")))
                    .build());
        }
        return buildProducer(details, rs.iterator().next());
    }

    private Producer buildProducer(Set<ProducerDetails> details, Row first) {
        return Producer.builder()
                .id(first.getLong("producer_id"))
                .details(details)
                .image(Image.builder()
                        .id(first.getLong("image_id"))
                        .alt(first.getString("alt"))
                        .url(first.getString("url"))
                        .build())
                .build();
    }
}
