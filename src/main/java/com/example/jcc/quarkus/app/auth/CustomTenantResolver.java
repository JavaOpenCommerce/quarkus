//package com.example.jcc.quarkus.app.auth;
//
//import io.quarkus.oidc.TenantResolver;
//import io.vertx.ext.web.RoutingContext;
//
//import javax.enterprise.context.ApplicationScoped;
//import java.util.Arrays;
//import java.util.regex.Pattern;
//
//import static java.util.Optional.ofNullable;
//
//@ApplicationScoped
//public class CustomTenantResolver implements TenantResolver {
//    public static final String PROFILE = "auth";
//    public static final String SLASH = "/";
//    private static final Pattern regex = Pattern.compile(SLASH);
//
//    @Override
//    public String resolve(RoutingContext context) {
//        return ofNullable(context.request().path())
//                .filter(p -> p.startsWith(SLASH + PROFILE))
//                .flatMap(p -> Arrays.stream(regex.split(p))
//                        .filter(path -> !PROFILE.equalsIgnoreCase(path))
//                        .findFirst())
//                // resolve to default tenant configuration
//                .orElse(null);
//    }
//}
