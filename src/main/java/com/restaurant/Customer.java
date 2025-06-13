package com.restaurant;

public class Customer {

    private final int customerId;
    private final boolean inCar; // true if the customer is in a car, false if they are inside the restaurant

    public Customer(int customerId, boolean inCar) {
        this.customerId = customerId;
        this.inCar = inCar;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public String getName() {
        return "Customer " + this.customerId;
    }

    public boolean isInCar() {
        return this.inCar;
    }
}
