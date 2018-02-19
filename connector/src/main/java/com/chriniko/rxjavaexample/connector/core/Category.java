package com.chriniko.rxjavaexample.connector.core;

public enum Category {

    BUSINESS,
    ENTERTAINMENT,
    GENERAL,
    HEALTH,
    SCIENCE,
    SPORTS,
    TECHNOLOGY;

    public static String getValue(Category category) {
        return category.name().toLowerCase();
    }
}
