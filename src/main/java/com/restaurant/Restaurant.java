package com.restaurant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.restaurant.observer.Observer;
import com.restaurant.observer.Subject;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Restaurant extends Subject {

    private final Queue<Customer> customerQueue;
    private String currentSandwichProgress;
    private int nextCustomerId;
    private final Ingredient[] ingredients = Ingredient.values();
    private int currentIngredientIndex;
    private Timeline workTimeline;

    public Restaurant() {
        this.customerQueue = new LinkedList<>();
        this.nextCustomerId = 0;
        this.currentIngredientIndex = 0;
        this.currentSandwichProgress = "Chef is waiting for customers to come in...";
    }

    public void startWorkingCycle() {
        // Notify initial state
        notifyObservers();

        // 1.5 seconds per ingredient/step
        workTimeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> processNextStep()));
        workTimeline.setCycleCount(Timeline.INDEFINITE);

        if (!customerQueue.isEmpty()) {
            prepareForNewSandwich(); // Set initial message for the first sandwich
            workTimeline.play();
        } else {
            this.currentSandwichProgress = "No customers. Chef is waiting.";
            notifyObservers();
        }
    }

    private void processNextStep() {
        if (customerQueue.isEmpty()) {
            currentSandwichProgress = "No customers. Chef is idle.";
            notifyObservers();
            workTimeline.pause();
            return;
        }

        if (currentIngredientIndex < ingredients.length) {
            // Adding an ingredient
            if (currentIngredientIndex == 0) { // Just started this sandwich
                currentSandwichProgress = "Chef is making sandwich for: " + customerQueue.peek().getName();
            }
            currentSandwichProgress += "\n- Adding " + ingredients[currentIngredientIndex].getDisplayName();
            currentIngredientIndex++;
        } else {
            // Sandwich is complete
            // TODO shown?
            currentSandwichProgress += "\nSandwich for " + customerQueue.peek().getName() + " is COMPLETE!";
            notifyObservers();
            serveCustomer();
            prepareForNewSandwich(); // Prepare for the next one or wait
            // The timeline continues, and prepareForNewSandwich will handle pausing if queue is empty
        }
        notifyObservers();
    }

    private void serveCustomer() {
        if (!customerQueue.isEmpty()) {
            Customer servedCustomer = customerQueue.poll();
            // TODO shown?
            currentSandwichProgress = "Served: " + servedCustomer.getName() + ".";
            // Don't notify here, let the next step in processNextStep or prepareForNewSandwich handle it
            // to avoid too many rapid updates. Or notify specifically about serving.
            // For now, the main progress update is sufficient.

            // Schedule a new customer to arrive in 5 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> addNewCustomerToQueue());
            delay.play();
        }
    }

    public void addNewCustomerToQueue() {
        boolean nextCustomerinCar = nextCustomerId % 3 == 0;
        Customer newCustomer = new Customer(nextCustomerId++, nextCustomerinCar);
        customerQueue.offer(newCustomer);
        notifyObservers();

        // If chef was idle, and now there's a customer, resume work.
        if (workTimeline != null && workTimeline.getStatus() == Timeline.Status.PAUSED && !customerQueue.isEmpty()) {
            prepareForNewSandwich(); // This will set initial progress for the new sandwich
            workTimeline.play();
        }
    }

    private void prepareForNewSandwich() {
        currentIngredientIndex = 0;
        if (!customerQueue.isEmpty()) {
            currentSandwichProgress = "Chef is preparing for: " + customerQueue.peek().getName();
            if (workTimeline != null && workTimeline.getStatus() != Timeline.Status.RUNNING) {
                workTimeline.play(); // Ensure timeline is running
            }
        } else {
            currentSandwichProgress = "No more customers. Chef is resting.";
            if (workTimeline != null) {
                workTimeline.pause();
            }
        }
        // No notifyObservers() here, as processNextStep or addNewCustomer will call it.
        // Or, if called after serving, the next processNextStep will update.
    }

    @Override
    public void notifyObservers() {
        String progressSnapshot = this.currentSandwichProgress;
        List<Customer> queueSnapshot = new ArrayList<>(this.customerQueue); // Defensive copy

        int currentStep;

        if (customerQueue.isEmpty()) {
            currentStep = 0; // No customer, so no progress on a specific sandwich, progress bar at 0
        } else {
            // If there are customers, currentIngredientIndex reflects progress on the current sandwich
            // currentIngredientIndex is 0 when preparing new, up to ingredients.length when complete.
            currentStep = this.currentIngredientIndex;
        }

        for (Observer observer : observers) {
            observer.update(progressSnapshot, queueSnapshot, currentStep);
        }
    }
}
