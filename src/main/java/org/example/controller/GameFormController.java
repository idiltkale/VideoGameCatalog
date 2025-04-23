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

public class GameFormController {

    @FXML private TextField titleField;
    @FXML private TextField developerField;
    @FXML private TextField publisherField;
    @FXML private TextField genreField;
    @FXML private TextField steamIdField;
    @FXML private TextField yearField;
    @FXML private TextField tagsField;
    @FXML private VBox tagBox;

    private Game game;

    public void setGame(Game game) {
        this.game = game;
        if (game != null) {
            titleField.setText(game.getTitle());
            developerField.setText(game.getDeveloper());
            publisherField.setText(game.getPublisher());
            genreField.setText(String.join(",", game.getGenre()));
            steamIdField.setText(game.getSteamId());
            yearField.setText(String.valueOf(game.getReleaseYear()));
            tagsField.setText(String.join(", ", game.getTags()));
            for (Node node : tagBox.getChildren()) {
                if (node instanceof CheckBox cb && game.getTags() != null) {
                    cb.setSelected(game.getTags().contains(cb.getText()));
                }
            }
        }
    }

    public Game getGameFromForm() {
        String title = titleField.getText();
        String developer = developerField.getText();
        String publisher = publisherField.getText();
        String genreInput = genreField.getText();
        String steamId = steamIdField.getText();
        int year = 0;
        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            year = 0;
        }

        List<String> genres = Arrays.asList(genreInput.split(","));

        List<String> selectedTags = tagBox.getChildren().stream()
                .filter(n -> n instanceof CheckBox && ((CheckBox) n).isSelected())
                .map(n -> ((CheckBox) n).getText())
                .toList();

        if (game == null) {
            game = new Game();
        }

        game.setTags(selectedTags);
        game.setTitle(title);
        game.setDeveloper(developer);
        game.setPublisher(publisher);
        game.setGenre(genres);
        game.setSteamId(steamId);
        game.setReleaseYear(year);

        return game;
    }

    @FXML
    private void onSaveClicked() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
