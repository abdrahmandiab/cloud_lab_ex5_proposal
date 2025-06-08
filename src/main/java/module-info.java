module com.restaurant {
    // Requires the javafx.controls module for UI controls
    requires javafx.controls;
    // Requires the javafx.fxml module for FXML loading
    requires javafx.fxml;
    // Requires transitive javafx.graphics because Stage is part of the public API (e.g., App.start() method)
    requires transitive javafx.graphics;

    // Opens the com.restaurant package to javafx.fxml for reflection (e.g., @FXML injection)
    opens com.restaurant to javafx.fxml;
    // Exports the com.restaurant package so App can be launched and controllers can be found
    exports com.restaurant;
    // Exports the observer package so types like Observer and Subject are accessible
    exports com.restaurant.observer;
}