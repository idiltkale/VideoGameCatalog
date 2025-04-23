package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Game;
import org.example.model.GameCatalog;
import org.example.model.JSONHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private TableView<Game> gameTable;
    @FXML private TextField searchBar;

    private List<String> selectedTagFilters = new ArrayList<>();
    private final GameCatalog catalog = new GameCatalog();
    private String activeSearchField = null;

    @FXML
    public void initialize() {
        TableColumn<Game, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Game, String> developerCol = new TableColumn<>("Developer");
        developerCol.setCellValueFactory(new PropertyValueFactory<>("developer"));

        gameTable.getColumns().addAll(titleCol, developerCol);

        try {
            List<Game> loadedGames = JSONHandler.loadGamesFromFile(new File("games.json"));
            catalog.setGames(loadedGames);
            gameTable.getItems().setAll(loadedGames);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameTable.setRowFactory(tv -> {
            TableRow<Game> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Game clickedGame = row.getItem();
                    showGameDetailPopup(clickedGame);
                }
            });
            return row;
        });

        searchBar.setVisible(false);
        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (activeSearchField == null || newVal.isBlank()) {
                gameTable.getItems().setAll(catalog.getGames());
                return;
            }

            String input = newVal.toLowerCase();
            List<Game> result = catalog.getGames().stream().filter(game -> {
                return switch (activeSearchField) {
                    case "Title" -> game.getTitle() != null && game.getTitle().toLowerCase().contains(input);
                    case "Developer" -> game.getDeveloper() != null && game.getDeveloper().toLowerCase().contains(input);
                    case "Publisher" -> game.getPublisher() != null && game.getPublisher().toLowerCase().contains(input);
                    case "Year" -> String.valueOf(game.getReleaseYear()).contains(input);
                    default -> false;
                };
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
    private void onFilterByTags() {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Filter by Tags");

        VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        String[] allTags = {"RPG", "Turn-based", "Multiplayer", "Shooter", "Adventure", "Strategy"};
        List<CheckBox> checkBoxes = new ArrayList<>();

        for (String tag : allTags) {
            CheckBox cb = new CheckBox(tag);
            if (selectedTagFilters.contains(tag)) cb.setSelected(true);
            checkBoxes.add(cb);
            container.getChildren().add(cb);
        }

        ButtonType filterButtonType = new ButtonType("Filter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(filterButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(container);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == filterButtonType) {
                return checkBoxes.stream()
                        .filter(CheckBox::isSelected)
                        .map(CheckBox::getText)
                        .toList();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedTags -> {
            selectedTagFilters = selectedTags;

            if (selectedTags.isEmpty()) {
                gameTable.getItems().setAll(catalog.getGames());
                return;
            }

            List<Game> filtered = catalog.getGames().stream().filter(game ->
                    game.getTags() != null &&
                            selectedTags.stream().allMatch(tag ->
                                    game.getTags().stream()
                                            .map(String::toLowerCase)
                                            .anyMatch(g -> g.contains(tag.toLowerCase()))
                            )
            ).toList();

            gameTable.getItems().setAll(filtered);
        });
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

    private void showGameDetailPopup(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/game_detail.fxml"));
            Parent root = loader.load();

            GameDetailController controller = loader.getController();
            controller.setGame(game);

            Stage detailStage = new Stage();
            detailStage.setTitle("Game Details - " + game.getTitle());
            detailStage.setScene(new Scene(root));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}
