package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Game;

import java.util.Arrays;
import java.util.List;

public class GameFormController {

    @FXML
    private TextField titleField, developerField, publisherField,
            genreField, steamIdField, yearField;

    private Game game;

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

        if (game == null) {
            game = new Game(); // yeni oyun eklerken boşsa oluştur
        }

        // Mevcut game nesnesini güncelle ✅
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
