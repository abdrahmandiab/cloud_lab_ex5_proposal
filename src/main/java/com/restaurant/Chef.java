package com.restaurant;

import com.restaurant.observer.Observer;
import com.restaurant.observer.Subject;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Chef implements Subject {
    private final List<Observer> observers;
    private final Queue<String> customerQueue;
    private String currentSandwichProgress;
    private int nextCustomerId;
    private final Ingredient[] ingredients = Ingredient.values();
    private int currentIngredientIndex;
    private Timeline workTimeline;

    public Chef() {
        this.observers = new ArrayList<>();
        this.customerQueue = new LinkedList<>();
        this.nextCustomerId = 1;
        this.currentIngredientIndex = 0;
        this.currentSandwichProgress = "Chef is waiting for customers to come in...";
        addInitialCustomers();
    }

    private void addInitialCustomers() {
        // Add 2 initial customers
        customerQueue.offer("Customer " + nextCustomerId++);
        customerQueue.offer("Customer " + nextCustomerId++);
        customerQueue.offer("Customer " + nextCustomerId++);
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
                 currentSandwichProgress = "Chef is making sandwich for: " + customerQueue.peek();
            }
            currentSandwichProgress += "\n- Adding " + ingredients[currentIngredientIndex].getDisplayName();
            currentIngredientIndex++;
        } else {
            // Sandwich is complete
            currentSandwichProgress += "\nSandwich for " + customerQueue.peek() + " is COMPLETE!";
            notifyObservers();
            serveCustomer();
            prepareForNewSandwich(); // Prepare for the next one or wait
            // The timeline continues, and prepareForNewSandwich will handle pausing if queue is empty
        }
        notifyObservers();
    }

    private void serveCustomer() {
        if (!customerQueue.isEmpty()) {
            String servedCustomer = customerQueue.poll();
            currentSandwichProgress = "Served: " + servedCustomer + ".";
            // Don't notify here, let the next step in processNextStep or prepareForNewSandwich handle it
            // to avoid too many rapid updates. Or notify specifically about serving.
            // For now, the main progress update is sufficient.

            // Schedule a new customer to arrive in 5 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> addNewCustomerToQueue());
            delay.play();
        }
    }

    private void addNewCustomerToQueue() {
        String newCustomer = "Customer " + nextCustomerId++;
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
            currentSandwichProgress = "Chef is preparing for: " + customerQueue.peek();
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
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        String progressSnapshot = this.currentSandwichProgress;
        List<String> queueSnapshot = new ArrayList<>(this.customerQueue); // Defensive copy

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