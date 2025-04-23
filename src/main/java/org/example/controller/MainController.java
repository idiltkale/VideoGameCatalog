package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Game;
import org.example.model.GameCatalog;
import org.example.model.JSONHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML
    private TableView<Game> gameTable;

    @FXML
    private TextField searchBar;

    private final GameCatalog catalog = new GameCatalog();
    private String activeSearchField = null;

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
        gameTable.getItems().setAll(catalog.getGames());

        searchBar.setVisible(false);
        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (activeSearchField == null || newVal.isBlank()) {
                gameTable.getItems().setAll(catalog.getGames());
                return;
            }

            String input = newVal.toLowerCase();
            List<Game> result = catalog.getGames().stream().filter(game -> {
                switch (activeSearchField) {
                    case "Title":
                        return game.getTitle() != null && game.getTitle().toLowerCase().contains(input);
                    case "Developer":
                        return game.getDeveloper() != null && game.getDeveloper().toLowerCase().contains(input);
                    case "Publisher":
                        return game.getPublisher() != null && game.getPublisher().toLowerCase().contains(input);
                    case "Year":
                        return String.valueOf(game.getReleaseYear()).contains(input);
                    default:
                        return false;
                }
            }).toList();

            gameTable.getItems().setAll(result);
        });
    }

    @FXML
    private void onAddGameClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/game_form.fxml"));
            Parent root = loader.load();
            Stage formStage = new Stage();
            formStage.setTitle("Add Game");
            formStage.setScene(new Scene(root));
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.showAndWait();

            GameFormController formController = loader.getController();
            Game newGame = formController.getGameFromForm();

            if (newGame != null && newGame.getTitle() != null && !newGame.getTitle().isBlank()) {
                catalog.addGame(newGame);
                gameTable.getItems().add(newGame);
                JSONHandler.saveGamesToFile(catalog.getGames(), new File("games.json"));
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
            controller.setGame(selectedGame);

            Stage formStage = new Stage();
            formStage.setTitle("Edit Game");
            formStage.setScene(new Scene(root));
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.showAndWait();

            Game updatedGame = controller.getGameFromForm();
            if (updatedGame != null) {
                gameTable.refresh();
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

    @FXML
    private void onSearchByTitle() {
        activateSearch("Title");
    }

    @FXML
    private void onSearchByDeveloper() {
        activateSearch("Developer");
    }

    @FXML
    private void onSearchByPublisher() {
        activateSearch("Publisher");
    }

    @FXML
    private void onSearchByYear() {
        activateSearch("Year");
    }


    private void activateSearch(String field) {
        activeSearchField = field;
        searchBar.setPromptText("Search by " + field + "...");
        searchBar.setVisible(true);
        searchBar.clear();
    }
}
