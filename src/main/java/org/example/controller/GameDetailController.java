package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.model.Game;

import java.io.File;

public class GameDetailController {

    @FXML private ImageView coverImage;
    @FXML private Label titleLabel;
    @FXML private Label developerLabel;
    @FXML private Label publisherLabel;
    @FXML private Label genreLabel;
    @FXML private Label platformLabel;
    @FXML private Label translatorLabel;
    @FXML private Label steamIdLabel;
    @FXML private Label releaseYearLabel;
    @FXML private Label playtimeLabel;
    @FXML private Label formatLabel;
    @FXML private Label languageLabel;
    @FXML private Label ratingLabel;
    @FXML private Label tagsLabel;

    public void setGame(Game game) {
        titleLabel.setText("Title: " + value(game.getTitle()));
        developerLabel.setText("Developer: " + value(game.getDeveloper()));
        publisherLabel.setText("Publisher: " + value(game.getPublisher()));
        genreLabel.setText("Genre: " + listToString(game.getGenre()));
        platformLabel.setText("Platforms: " + listToString(game.getPlatforms()));
        translatorLabel.setText("Translators: " + listToString(game.getTranslators()));
        steamIdLabel.setText("Steam ID: " + value(game.getSteamId()));
        releaseYearLabel.setText("Release Year: " + game.getReleaseYear());
        playtimeLabel.setText("Playtime: " + game.getPlaytime() + " hrs");
        formatLabel.setText("Format: " + value(game.getFormat()));
        languageLabel.setText("Language: " + value(game.getLanguage()));
        ratingLabel.setText("Rating: " + game.getRating());
        tagsLabel.setText("Tags: " + listToString(game.getTags()));

        if (game.getImagePath() != null && !game.getImagePath().isBlank()) {
            File file = new File(game.getImagePath());
            if (file.exists()) {
                coverImage.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    private String listToString(java.util.List<String> list) {
        return list == null ? "-" : String.join(", ", list);
    }

    private String value(String val) {
        return val == null ? "-" : val;
    }
}
