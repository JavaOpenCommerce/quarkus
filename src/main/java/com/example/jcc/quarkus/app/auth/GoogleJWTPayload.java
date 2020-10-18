package com.example.jcc.quarkus.app.auth;

import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

class GoogleJWTPayload {
    private final Map<Fields, String> payload;

    private GoogleJWTPayload(Map<Fields, String> map) {
        this.payload = map;
    }

    public static GoogleJWTPayload from(JsonObject obj) {
        final Spliterator<Map.Entry<String, Object>> spliterator = Spliterators.spliteratorUnknownSize(requireNonNull(obj).stream().iterator(), Spliterator.CONCURRENT);
        final Map<Fields, String> map = StreamSupport.stream(spliterator, false)
                .filter(e -> !Objects.isNull(e.getKey()))
                .map(e -> Map.entry(Fields.valueOf(e.getKey().toUpperCase()), String.valueOf(Objects.requireNonNullElse(e.getValue(), "null"))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new GoogleJWTPayload(map);
    }

    public Optional<String> get(Fields field) {
        return Optional.ofNullable(this.payload.get(field))
                .filter(v -> !v.equals("null"));
    }

    /** Fields that are valid for JWT, as in https://tools.ietf.org/html/rfc7517 **/
    enum Fields {
        KTY("Key Type"),
        KID("Key ID"),
        USE("Public Key Use"),
        ALG("Algorithm"),
        ISS("Issuer"),
        SUB("Subject"),
        AUD("Audience"),
        EXP("Expiration"),
        NBF("Not Before"),
        IAT("Issued At"),
        JTI("JWT ID"),
        //Google fields:
        AZP(""),
        EMAIL("Email"),
        EMAIL_VERIFIED("Email verified"),
        AT_HASH("Hash"),
        NONCE("Non repeatable id");

        private final String description;

        Fields(String name) {
            this.description = name;
        }
    }
}
