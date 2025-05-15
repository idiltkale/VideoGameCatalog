package org.example.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML private ComboBox<String> searchFieldCombo;
    @FXML private TextField searchBar;

    private List<String> selectedTagFilters = new ArrayList<>();
    private final GameCatalog catalog = new GameCatalog();
    private String activeSearchField = null;

    @FXML
    public void initialize() {
        TableColumn<Game, ImageView> imageCol = new TableColumn<>("Image");
        imageCol.setPrefWidth(80); // Genişlik artırıldı

        imageCol.setCellValueFactory(data -> {
            String path = data.getValue().getImagePath();
            ImageView view = new ImageView();
            if (path != null && !path.isBlank()) {
                File file = new File(path);
                if (file.exists()) {
                    view.setImage(new Image(file.toURI().toString(), 100, 100, true, true));
                }
            }
            return new ReadOnlyObjectWrapper<>(view);
        });

        TableColumn<Game, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Game, String> developerCol = new TableColumn<>("Developer");
        developerCol.setCellValueFactory(new PropertyValueFactory<>("developer"));

        TableColumn<Game, String> publisherCol = new TableColumn<>("Publisher");
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        TableColumn<Game, String> genreCol = createListColumn("Genre", Game::getGenre);
        TableColumn<Game, String> platformsCol = createListColumn("Platforms", Game::getPlatforms);
        TableColumn<Game, String> translatorsCol = createListColumn("Translators", Game::getTranslators);

        TableColumn<Game, String> steamIdCol = new TableColumn<>("Steam ID");
        steamIdCol.setCellValueFactory(new PropertyValueFactory<>("steamId"));

        TableColumn<Game, Integer> yearCol = new TableColumn<>("Release Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));

        TableColumn<Game, Double> playtimeCol = new TableColumn<>("Playtime");
        playtimeCol.setCellValueFactory(new PropertyValueFactory<>("playtime"));

        TableColumn<Game, String> formatCol = new TableColumn<>("Format");
        formatCol.setCellValueFactory(new PropertyValueFactory<>("format"));

        TableColumn<Game, String> languageCol = new TableColumn<>("Language");
        languageCol.setCellValueFactory(new PropertyValueFactory<>("language"));

        TableColumn<Game, Double> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

        TableColumn<Game, String> tagsCol = createListColumn("Tags", Game::getTags);

        TableColumn<Game, Void> deleteCol = new TableColumn<>("🗑");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("🗑");

            {
                deleteButton.setOnAction(e -> {
                    Game game = getTableView().getItems().get(getIndex());
                    catalog.removeGame(game);
                    gameTable.getItems().remove(game);
                    try {
                        JSONHandler.saveGamesToFile(catalog.getGames(), new File("games.json"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        gameTable.getColumns().addAll(
                imageCol, titleCol, developerCol, publisherCol, genreCol, platformsCol, translatorsCol,
                steamIdCol, yearCol, playtimeCol, formatCol, languageCol, ratingCol, tagsCol, deleteCol
        );

        searchFieldCombo.getItems().addAll(
                "Title", "Developer", "Publisher", "Genre", "Platforms", "Translators",
                "Steam ID", "Release Year", "Playtime", "Format", "Language", "Rating", "Tags"
        );
        searchFieldCombo.getSelectionModel().selectFirst();
        activeSearchField = searchFieldCombo.getValue();
        searchFieldCombo.setOnAction(e -> activeSearchField = searchFieldCombo.getValue());

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
                    case "Genre" -> game.getGenre() != null && game.getGenre().stream().anyMatch(g -> g.toLowerCase().contains(input));
                    case "Platforms" -> game.getPlatforms() != null && game.getPlatforms().stream().anyMatch(p -> p.toLowerCase().contains(input));
                    case "Translators" -> game.getTranslators() != null && game.getTranslators().stream().anyMatch(t -> t.toLowerCase().contains(input));
                    case "Steam ID" -> game.getSteamId() != null && game.getSteamId().toLowerCase().contains(input);
                    case "Release Year" -> String.valueOf(game.getReleaseYear()).contains(input);
                    case "Playtime" -> String.valueOf(game.getPlaytime()).contains(input);
                    case "Format" -> game.getFormat() != null && game.getFormat().toLowerCase().contains(input);
                    case "Language" -> game.getLanguage() != null && game.getLanguage().toLowerCase().contains(input);
                    case "Rating" -> String.valueOf(game.getRating()).contains(input);
                    case "Tags" -> game.getTags() != null && game.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(input));
                    default -> false;
                };
            }).toList();

            gameTable.getItems().setAll(result);
        });

        try {
            List<Game> loadedGames = JSONHandler.loadGamesFromFile(new File("games.json"));
            catalog.setGames(loadedGames);
            gameTable.getItems().setAll(loadedGames);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameTable.setRowFactory(tv -> {
            TableRow<Game> row = new TableRow<>(){
            @Override
            protected void updateItem(Game item, boolean empty) {
                super.updateItem(item, empty);
                setPrefHeight(60);  // satır yüksekliği artırıldı
            }
        };
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Game clickedGame = row.getItem();
                    showGameDetailPopup(clickedGame);
                }
            });
            return row;
        });
    }

    private TableColumn<Game, String> createListColumn(String title, java.util.function.Function<Game, List<String>> extractor) {
        TableColumn<Game, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> {
            List<String> list = extractor.apply(data.getValue());
            return new ReadOnlyStringWrapper(list != null ? String.join(", ", list) : "-");
        });
        return col;
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
    private void onHelpClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Video Game Catalog Help");
        alert.setContentText("Use the Add menu to add a new game.\nUse the File menu to import/export games.\nSearch by selecting a field and typing.");
        alert.showAndWait();
    }

    @FXML
    private void onImport() {
        try {
            List<Game> games = JSONHandler.loadGamesFromFile(new File("games.json"));
            catalog.setGames(games);
            gameTable.getItems().setAll(games);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExport() {
        try {
            JSONHandler.saveGamesToFile(catalog.getGames(), new File("games.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
