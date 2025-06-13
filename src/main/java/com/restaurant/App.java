package com.restaurant;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.hide(); // Hide the default stage as we are creating our own.

        Restaurant restaurant = new Restaurant(); // Our Subject

        //  Add 3 initial customers
        restaurant.addNewCustomerToQueue();
        restaurant.addNewCustomerToQueue();
        restaurant.addNewCustomerToQueue();

        // Window dimensions and spacing
        double windowWidth = 400;  // Width of each CustomerView window
        double windowHeight = 400; // Height of each CustomerView window
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
        ManagerView managerView = new ManagerView("Restaurant Manager");
        managerView.setPosition(startX, startY);

        CustomerView driveThruView = new CustomerView("Drive Thru Display", true);
        driveThruView.setPosition(startX + windowWidth + xSpacing, startY);

        CustomerView waitingAreaView = new CustomerView("Waiting Area Screen", false);
        waitingAreaView.setPosition(startX + 2 * (windowWidth + xSpacing), startY);

        // This is where the restaurant is informed of observers.
        restaurant.registerObserver(managerView);
        restaurant.registerObserver(driveThruView);
        restaurant.registerObserver(waitingAreaView);

        // Show the views
        managerView.show();
        driveThruView.show();
        waitingAreaView.show();

        // Start the restaurant's work cycle
        restaurant.startWorkingCycle();
    }

    public static void main(String[] args) {
        launch();
    }

}
