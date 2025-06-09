package com.restaurant;

import java.util.List;

import com.restaurant.observer.Observer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ManagerView implements Observer {

    private final Stage stage;
    private final Label progressLabel;
    private final Label queueTitleLabel;
    private final ProgressBar sandwichProgressBar;
    private final HBox customerQueueBox;
    private final Label percentageLabel;
    private final int totalSteps; // Store total steps for sandwich making
    private final String[] customerBoxColors = {"#FFB6C1", "#ADD8E6", "#90EE90", "#FFD700", "#E6E6FA"};
    private int servedCustomerCount;

    public ManagerView(String title) {
        stage = new Stage();
        stage.setTitle(title);

        BorderPane root = new BorderPane();
        this.totalSteps = Ingredient.count(); // Initialize total steps

        this.servedCustomerCount = 0;

        root.setPadding(new Insets(15));

        // --- Top Section ---
        VBox topSection = new VBox(5); // 5px spacing between elements in top section
        Label viewTitleLabel = new Label(title + " View");
        viewTitleLabel.setFont(Font.font(null, javafx.scene.text.FontWeight.BOLD, 16));

        progressLabel = new Label("Waiting for updates...");
        progressLabel.setFont(Font.font(14));
        progressLabel.setWrapText(true);
        progressLabel.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(progressLabel, javafx.scene.layout.Priority.ALWAYS);

        topSection.getChildren().addAll(viewTitleLabel, progressLabel);
        root.setTop(topSection);

        // --- Bottom Section ---
        VBox bottomSection = new VBox(8); // 8px spacing between elements in bottom section
        bottomSection.setPadding(new Insets(10, 0, 0, 0)); // Add some top padding to separate from content above

        queueTitleLabel = new Label("Customer Queue (0):");
        queueTitleLabel.setFont(Font.font(14));

        customerQueueBox = new HBox(5); // 5px spacing between customer boxes
        customerQueueBox.setAlignment(Pos.CENTER_LEFT);
        // customerQueueBox.setStyle("-fx-border-color: lightgrey; -fx-padding: 2;"); // Optional: border around the queue area

        // HBox for ProgressBar and Percentage Label
        HBox progressBox = new HBox(5); // 5px spacing
        progressBox.setAlignment(Pos.CENTER_LEFT);

        sandwichProgressBar = new ProgressBar(0); // Initial progress is 0
        // Let the HBox manage the width of the progress bar by setting Hgrow
        HBox.setHgrow(sandwichProgressBar, javafx.scene.layout.Priority.ALWAYS);
        sandwichProgressBar.setMaxWidth(Double.MAX_VALUE);

        percentageLabel = new Label("0%");
        percentageLabel.setFont(Font.font(14));

        progressBox.getChildren().addAll(sandwichProgressBar, percentageLabel);
        bottomSection.getChildren().addAll(progressBox, queueTitleLabel, customerQueueBox);
        root.setBottom(bottomSection);

        Scene scene = new Scene(root, 400, 400); // Increased height to accommodate progress bar
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }

    public void setPosition(double x, double y) {
        stage.setX(x);
        stage.setY(y);
    }

    @Override
    public void update(String currentProgressText, List<Customer> customerQueue, int currentStep) {
        Platform.runLater(() -> {
            progressLabel.setText("Served customers: " + this.servedCustomerCount + "\nCurrent Status:\n" + currentProgressText);

            // Update queue title
            queueTitleLabel.setText("Customer Queue (" + customerQueue.size() + "):");

            // Update customer queue display
            customerQueueBox.getChildren().clear(); // Clear previous customer boxes
            if (customerQueue.isEmpty()) {
                Label emptyLabel = new Label("Empty");
                emptyLabel.setFont(Font.font(12));
                customerQueueBox.getChildren().add(emptyLabel);
            } else {
                for (Customer customer : customerQueue) {
                    String customerName = customer.getName();
                    if (customer.isInCar()) {
                        // customer is in drive thru
                        customerName = customerName + " (D)";
                    } else {
                        // customer is in restaurant
                        customerName = customerName + " (R)";
                    }
                    // mapping customer ids to unique colors
                    int colorIndex = customer.getCustomerId() % customerBoxColors.length;
                    String chosenColor = customerBoxColors[colorIndex];
                    Label customerBox = new Label(customerName);
                    customerBox.setFont(Font.font(12));
                    customerBox.setStyle("-fx-border-color: black; -fx-padding: 5px; -fx-background-color: " + chosenColor + ";");
                    customerQueueBox.getChildren().add(customerBox);
                }
            }

            int percentComplete = 0;
            if (this.totalSteps > 0) {
                percentComplete = (int) (((double) currentStep / this.totalSteps) * 100);
                sandwichProgressBar.setProgress((double) currentStep / this.totalSteps);
                if (currentProgressText != null && currentProgressText.endsWith("COMPLETE!")) {
                    // new served customer
                    this.servedCustomerCount++;
                }
            } else {
                sandwichProgressBar.setProgress(0); //handling of div by 0
            }
            percentageLabel.setText(percentComplete + "%");

        });
    }
}
