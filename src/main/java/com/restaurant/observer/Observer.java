package com.restaurant.observer;

import java.util.List;

public interface Observer {
    void update(String currentProgressText, List<String> customerQueue, int currentStep);
}