package com.restaurant.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {

    protected final List<Observer> observers;

    public Subject() {
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to the list of observers.
     *
     * @param o The observer to be registered.
     */
    public void registerObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            this.observers.add(o);
        }
    }

    /**
     * Removes an observer from the list of observers.
     *
     * @param o The observer to be removed.
     */
    public void removeObserver(Observer o) {
        if (o != null) {
            this.observers.remove(o);
        }
    }

    public abstract void notifyObservers();
}
