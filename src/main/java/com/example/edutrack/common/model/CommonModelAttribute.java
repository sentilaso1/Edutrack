package com.example.edutrack.common.model;

public enum CommonModelAttribute {
    LOGGED_IN_USER("loggedInUser"),
    ERROR("error"),
    SUCCESS("success");

    private final String attributeName;

    CommonModelAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public String toString() {
        return attributeName;
    }
}
