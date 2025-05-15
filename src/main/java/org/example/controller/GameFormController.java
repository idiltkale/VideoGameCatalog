// === GameFormController.java ===
package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Game;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameFormController {

    @FXML private TextField titleField;
    @FXML private TextField developerField;
    @FXML private TextField publisherField;
    @FXML private TextField genreField;
    @FXML private TextField platformsField;
    @FXML private TextField translatorsField;
    @FXML private TextField steamIdField;
    @FXML private TextField yearField;
    @FXML private TextField playtimeField;
    @FXML private TextField formatField;
    @FXML private TextField languageField;
    @FXML private TextField ratingField;
    @FXML private VBox tagBox;

    private Game game;

    public void setGame(Game game) {
        this.game = game;
        if (game != null) {
            titleField.setText(game.getTitle());
            developerField.setText(game.getDeveloper());
            publisherField.setText(game.getPublisher());
            genreField.setText(String.join(",", safeList(game.getGenre())));
            platformsField.setText(String.join(",", safeList(game.getPlatforms())));
            translatorsField.setText(String.join(",", safeList(game.getTranslators())));
            steamIdField.setText(game.getSteamId());
            yearField.setText(String.valueOf(game.getReleaseYear()));
            playtimeField.setText(String.valueOf(game.getPlaytime()));
            formatField.setText(game.getFormat());
            languageField.setText(game.getLanguage());
            ratingField.setText(String.valueOf(game.getRating()));

            for (Node node : tagBox.getChildren()) {
                if (node instanceof CheckBox cb && game.getTags() != null) {
                    cb.setSelected(game.getTags().contains(cb.getText()));
                }
            }
        }
    }

    @FXML
    private void onSave() {
        game = new Game();

        game.setTitle(titleField.getText());
        game.setDeveloper(developerField.getText());
        game.setPublisher(publisherField.getText());

        game.setGenre(splitByComma(genreField.getText()));
        game.setPlatforms(splitByComma(platformsField.getText()));
        game.setTranslators(splitByComma(translatorsField.getText()));

        game.setSteamId(steamIdField.getText());

        try {
            game.setReleaseYear(Integer.parseInt(yearField.getText()));
        } catch (NumberFormatException e) {
            game.setReleaseYear(0);
        }

        try {
            game.setPlaytime(Double.parseDouble(playtimeField.getText()));
        } catch (NumberFormatException e) {
            game.setPlaytime(0.0);
        }

        game.setFormat(formatField.getText());
        game.setLanguage(languageField.getText());

        try {
            game.setRating(Double.parseDouble(ratingField.getText()));
        } catch (NumberFormatException e) {
            game.setRating(0.0);
        }

        List<String> selectedTags = tagBox.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());
        game.setTags(selectedTags);

        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    public Game getGameFromForm() {
        return game;
    }

    private List<String> splitByComma(String input) {
        return input == null || input.isBlank()
                ? List.of()
                : Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private List<String> safeList(List<String> list) {
        return list == null ? List.of() : list;
    }
}
