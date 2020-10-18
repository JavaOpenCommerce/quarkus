package com.example.jcc.quarkus.app;

import com.example.jcc.quarkus.app.exception.WrappedException;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IOUtil {
    private IOUtil() {
        // util class
    }

    public static JsonObject readJson(String path) {
        return new JsonObject(readFile(path));
    }

    public static String readFile(String path) {
        try {
            final URL resource = IOUtil.class.getClassLoader().getResource(path);
            return Files.readString(Paths.get(resource.toURI()));
        } catch (URISyntaxException | IOException e) {
            throw new WrappedException(e);
        }
    }

}
