package com.restaurant;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.hide(); // Hide the default stage as we are creating our own.

        Chef chef = new Chef(); // Our Subject

        // Window dimensions and spacing
        double windowWidth = 400;  // Width of each CustomerView window
        double windowHeight = 350; // Height of each CustomerView window
        double xSpacing = 30;      // Horizontal spacing between windows

        // Get screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Calculate total width of all windows and spacing
        double totalGroupWidth = (3 * windowWidth) + (2 * xSpacing);

        // Calculate starting X for the leftmost window to center the group
        double startX = (screenWidth - totalGroupWidth) / 2;

        // Calculate Y to vertically center the windows
        double startY = (screenHeight - windowHeight) / 2;

        // Create and position Observers (Views)
        CustomerView managerView = new CustomerView("Restaurant Manager");
        managerView.setPosition(startX, startY);

        CustomerView driveThruView = new CustomerView("Drive Thru Display");
        driveThruView.setPosition(startX + windowWidth + xSpacing, startY);

        CustomerView waitingAreaView = new CustomerView("Waiting Area Screen");
        waitingAreaView.setPosition(startX + 2 * (windowWidth + xSpacing), startY);

        // This is where the Chef is informed of observers.
        chef.registerObserver(managerView);
        chef.registerObserver(driveThruView);
        chef.registerObserver(waitingAreaView);

        // Show the views
        managerView.show();
        driveThruView.show();
        waitingAreaView.show();

        // Start the chef's work cycle
        chef.startWorkingCycle();
    }

    public static void main(String[] args) {
        launch();
    }

}