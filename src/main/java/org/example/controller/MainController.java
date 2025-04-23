package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Game;
import org.example.model.GameCatalog;
import org.example.model.JSONHandler;

import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    private TableView<Game> gameTable;

    @FXML
    private TextField searchBar;

    private final GameCatalog catalog = new GameCatalog();

    @FXML
    private void onHelpClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/help_view.fxml"));
            Parent root = loader.load();

            Stage helpStage = new Stage();
            helpStage.setTitle("Help");
            helpStage.setScene(new Scene(root));
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void initialize() {
        TableColumn<Game, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Game, String> developerCol = new TableColumn<>("Developer");
        developerCol.setCellValueFactory(new PropertyValueFactory<>("developer"));

        gameTable.getColumns().addAll(titleCol, developerCol);

        // Arama
        searchBar.textProperty().addListener((obs, oldText, newText) -> {
            gameTable.getItems().setAll(catalog.search(newText));
        });

    }


    @FXML
    private void onAddGameClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/game_form.fxml"));
            Parent root = loader.load();

            // Yeni pencere
            Stage formStage = new Stage();
            formStage.setTitle("Add Game");
            formStage.setScene(new Scene(root));
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.showAndWait();

            // Formdan yeni oyun al
            GameFormController formController = loader.getController();
            Game newGame = formController.getGameFromForm();

            if (newGame != null && newGame.getTitle() != null && !newGame.getTitle().isBlank()) {
                catalog.addGame(newGame);                        // listeye ekle
                gameTable.getItems().add(newGame);               // GUI'ye ekle
                JSONHandler.saveGamesToFile(catalog.getGames(), new File("games.json")); // dosyaya yaz
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onEditGameClicked() {
        Game selectedGame = gameTable.getSelectionModel().getSelectedItem();
        if (selectedGame == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/game_form.fxml"));
            Parent root = loader.load();

            GameFormController controller = loader.getController();
            controller.setGame(selectedGame); // Formu doldur

            Stage formStage = new Stage();
            formStage.setTitle("Edit Game");
            formStage.setScene(new Scene(root));
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.showAndWait();

            // Güncellenmiş oyunu al (aynı referansa sahip)
            Game updatedGame = controller.getGameFromForm();

            if (updatedGame != null) {
                gameTable.refresh(); // sadece güncelle
                JSONHandler.saveGamesToFile(catalog.getGames(), new File("games.json"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteGameClicked() {
        Game selectedGame = gameTable.getSelectionModel().getSelectedItem();
        if (selectedGame != null) {
            catalog.removeGame(selectedGame);
            gameTable.getItems().remove(selectedGame);
            try {
                JSONHandler.saveGamesToFile(catalog.getGames(), new File("games.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
