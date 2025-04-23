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

    @FXML
    private TextField titleField, developerField, publisherField,
            genreField, steamIdField, yearField, tagsField;

    private Game game;
    @FXML
    private VBox tagBox;


    // Formu düzenleme için açarsak kullanılacak
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

    // Formdan veri alınıp Game nesnesi oluşturulur
    public Game getGameFromForm() {
        String title = titleField.getText();
        String developer = developerField.getText();
        String publisher = publisherField.getText();
        String genreInput = genreField.getText();
        String steamId = steamIdField.getText();
        int year = Integer.parseInt(yearField.getText());
        List<String> genres = Arrays.asList(genreInput.split(","));

        String tagInput = tagsField.getText();
        List<String> selectedTags = tagBox.getChildren().stream()
                .filter(n -> n instanceof CheckBox && ((CheckBox) n).isSelected())
                .map(n -> ((CheckBox) n).getText())
                .toList();




        if (game == null) {
            game = new Game(); // yeni oyun eklerken boşsa oluştur
        }

        // Mevcut game nesnesini güncelle ✅
        game.setTags(selectedTags);
        game.setTitle(title);
        game.setDeveloper(developer);
        game.setPublisher(publisher);
        game.setGenre(genres);
        game.setSteamId(steamId);
        game.setReleaseYear(year);

        return game;
    }


    // Kaydet butonuna tıklanınca pencereyi kapat
    @FXML
    private void onSaveClicked() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
