package org.example.util;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.Year;
import java.util.Optional;

public class YearPickerDialog {

    private static int currentStartYear = 1950;
    private static final int YEARS_PER_PAGE = 12;
    private static final int MIN_YEAR = 1950;
    private static final int MAX_YEAR = Year.now().getValue();
    private static Optional<Integer> selectedYear = Optional.empty();

    public static Optional<Integer> showAndWaitSelect() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Select Year");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Button back = new Button("←");
        Button forward = new Button("→");
        Text rangeLabel = new Text();

        updateGrid(grid, dialog, rangeLabel);

        back.setOnAction(e -> {
            if (currentStartYear - YEARS_PER_PAGE >= MIN_YEAR) {
                currentStartYear -= YEARS_PER_PAGE;
                updateGrid(grid, dialog, rangeLabel);
            }
        });

        forward.setOnAction(e -> {
            if (currentStartYear + YEARS_PER_PAGE <= MAX_YEAR) {
                currentStartYear += YEARS_PER_PAGE;
                updateGrid(grid, dialog, rangeLabel);
            }
        });

        grid.add(back, 0, 0);
        grid.add(rangeLabel, 1, 0, 2, 1);
        grid.add(forward, 3, 0);

        Scene scene = new Scene(grid, 300, 220);
        dialog.setScene(scene);
        dialog.showAndWait();

        return selectedYear;
    }

    private static void updateGrid(GridPane grid, Stage dialog, Text rangeLabel) {
        grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        int year = currentStartYear;
        int row = 1;
        int col = 0;

        for (int i = 0; i < YEARS_PER_PAGE && year <= MAX_YEAR; i++) {
            Button yearBtn = new Button(String.valueOf(year));
            yearBtn.setPrefWidth(60);
            int selected = year;
            yearBtn.setOnAction(e -> {
                selectedYear = Optional.of(selected);
                dialog.close();
            });

            grid.add(yearBtn, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
            year++;
        }

        int endYear = Math.min(currentStartYear + YEARS_PER_PAGE - 1, MAX_YEAR);
        rangeLabel.setText(currentStartYear + " - " + endYear);
    }
}
