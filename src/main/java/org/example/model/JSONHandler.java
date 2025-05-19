package org.example.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;

public class JSONHandler {
    private static final Gson gson = new Gson();

    // Kullanıcıya özel veri dosyası yolu
    public static File getUserDataFile() {
        String userHome = System.getProperty("user.home");
        File dir = new File(userHome, "AppData/Local/VideoGameCatalog");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "games.json");
    }

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

    public static List<Game> loadOrInitializeUserData() {
        File userFile = new File(System.getProperty("user.home") + "/AppData/Local/VideoGameCatalog/games.json");
        if (!userFile.exists()) {
            try {
                // Create directory if not exist
                File parent = userFile.getParentFile();
                if (!parent.exists()) parent.mkdirs();

                // Load from resources
                try (InputStream is = JSONHandler.class.getClassLoader().getResourceAsStream("games.json")) {
                    if (is != null) {
                        try (OutputStream os = new FileOutputStream(userFile)) {
                            is.transferTo(os);
                        }
                    } else {
                        // fallback: create empty file if even resources missing
                        saveGamesToFile(List.of(), userFile);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return List.of();
            }
        }

        try {
            return loadGamesFromFile(userFile);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
