package org.example.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class JSONHandler {
    private static final Gson gson = new Gson();

    public static void saveGamesToFile(List<Game> games, File file) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(games, writer);
        }
    }

    public static List<Game> loadGamesFromFile(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Game>>() {}.getType();
            return gson.fromJson(reader, listType);
        }
    }
}
