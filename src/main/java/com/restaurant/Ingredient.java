package com.restaurant;

public enum Ingredient {
    BOTTOM_BUN("Bottom Bun"),
    LETTUCE("Lettuce"),
    TOMATO("Tomato"),
    SAUCE("Sauce"),
    PATTY("Patty"),
    CHEESE("Cheese"),
    TOP_BUN("Top Bun");

    private final String displayName;

    Ingredient(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        // This allows us to directly use the enum constant
        // where its display name string is expected.
        return displayName;
    }

    public static int count() {
        return values().length;
    }
}