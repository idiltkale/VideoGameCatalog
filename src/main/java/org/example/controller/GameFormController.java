package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.model.Game;
import org.example.util.YearPickerDialog;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameFormController {

    @FXML private TextField titleField;
    @FXML private TextField developerField;
    @FXML private TextField publisherField;
    @FXML private MenuButton genreMenuButton;
    @FXML private Label selectedGenresLabel;
    @FXML private TextField platformsField;
    @FXML private TextField translatorsField;
    @FXML private TextField steamIdField;
    @FXML private Button yearButton;
    @FXML private TextField playtimeField;
    @FXML private TextField formatField;
    @FXML private TextField languageField;
    @FXML private HBox ratingStarsBox;
    @FXML private VBox tagBox;

    private Game game;
    private File selectedImageFile;
    private Integer selectedYear;
    private int selectedRating = 0;

    private final String[] defaultGenres = {
            "Adventure", "Action", "Sports", "Simulation", "Platformer", "RPG",
            "First-person shooter", "Action-adventure", "Fighting", "Real-time strategy",
            "Racing", "Shooter", "Puzzle", "Casual", "Strategy game",
            "Massively multiplayer online role-playing", "Stealth", "Party",
            "Action RPG", "Tactical role-playing", "Survival", "Battle Royale"
    };

    public void initialize() {
        setupGenreMenu();
        setupRatingStars();
        yearButton.setText("Select Year");
        updateSelectedGenresLabel();
    }

    private void setupGenreMenu() {
        for (String genre : defaultGenres) {
            CheckMenuItem item = new CheckMenuItem(genre);
            item.setOnAction(e -> updateSelectedGenresLabel());
            genreMenuButton.getItems().add(item);
        }
    }

    private void updateSelectedGenresLabel() {
        List<String> selected = getSelectedGenres();
        selectedGenresLabel.setText("Selected: " + (selected.isEmpty() ? "None" : String.join(", ", selected)));
    }

    private void setupRatingStars() {
        ratingStarsBox.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(new Image(getClass().getResource("/empty-star.png").toExternalForm()));
            final int starValue = i;
            star.setFitWidth(24);
            star.setFitHeight(24);
            star.setOnMouseClicked(e -> {
                selectedRating = starValue;
                refreshStarImages();
            });
            ratingStarsBox.getChildren().add(star);
        }
    }

    private void refreshStarImages() {
        for (int i = 0; i < ratingStarsBox.getChildren().size(); i++) {
            ImageView star = (ImageView) ratingStarsBox.getChildren().get(i);
            String imgPath = i < selectedRating ? "/filled-star.png" : "/empty-star.png";
            star.setImage(new Image(getClass().getResource(imgPath).toExternalForm()));
        }
    }

    private List<String> getSelectedGenres() {
        return genreMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected())
                .map(MenuItem::getText)
                .toList();
    }

    public void setGame(Game game) {
        this.game = game;
        if (game != null) {
            titleField.setText(game.getTitle());
            developerField.setText(game.getDeveloper());
            publisherField.setText(game.getPublisher());
            platformsField.setText(String.join(",", safeList(game.getPlatforms())));
            translatorsField.setText(String.join(",", safeList(game.getTranslators())));
            steamIdField.setText(game.getSteamId());
            selectedYear = game.getReleaseYear();
            yearButton.setText(String.valueOf(selectedYear));
            playtimeField.setText(String.valueOf(game.getPlaytime()));
            formatField.setText(game.getFormat());
            languageField.setText(game.getLanguage());

            selectedRating = (int) Math.round(game.getRating());
            refreshStarImages();

            genreMenuButton.getItems().forEach(item -> {
                if (item instanceof CheckMenuItem cmi) {
                    cmi.setSelected(game.getGenre() != null && game.getGenre().contains(cmi.getText()));
                }
            });
            updateSelectedGenresLabel();

            for (Node node : tagBox.getChildren()) {
                if (node instanceof CheckBox cb && game.getTags() != null) {
                    cb.setSelected(game.getTags().contains(cb.getText()));
                }
            }

            if (game.getImagePath() != null && !game.getImagePath().isBlank()) {
                selectedImageFile = new File(game.getImagePath());
            }
        }
    }

    @FXML
    private void onYearSelectClicked() {
        Optional<Integer> result = YearPickerDialog.showAndWaitSelect();
        result.ifPresent(year -> {
            selectedYear = year;
            yearButton.setText(String.valueOf(year));
        });
    }

    @FXML
    private void onChooseImageClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) titleField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedImageFile = file;
        }
    }

    @FXML
    private void onSave() {
        if (titleField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Title cannot be empty!");
            alert.showAndWait();
            return;
        }

        if (game == null) {
            game = new Game();
        }

        game.setTitle(titleField.getText());
        game.setDeveloper(developerField.getText());
        game.setPublisher(publisherField.getText());
        game.setGenre(getSelectedGenres());
        game.setPlatforms(splitByComma(platformsField.getText()));
        game.setTranslators(splitByComma(translatorsField.getText()));
        game.setSteamId(steamIdField.getText());

        game.setReleaseYear(selectedYear != null ? selectedYear : 0);

        try {
            game.setPlaytime(Double.parseDouble(playtimeField.getText()));
        } catch (NumberFormatException e) {
            game.setPlaytime(0.0);
        }

        game.setFormat(formatField.getText());
        game.setLanguage(languageField.getText());
        game.setRating(selectedRating);

        List<String> selectedTags = tagBox.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());
        game.setTags(selectedTags);

        if (selectedImageFile != null) {
            game.setImagePath(selectedImageFile.getAbsolutePath());
        }

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