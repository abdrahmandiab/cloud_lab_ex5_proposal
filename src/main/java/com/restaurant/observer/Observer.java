package com.restaurant.observer;

import java.util.List;

import com.restaurant.Customer;

public interface Observer {

    void update(String currentProgressText, List<Customer> customerQueue, int currentStep);
}
