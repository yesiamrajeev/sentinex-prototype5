package com.example.sentenix_prototype2;

public class Report {
    private String description;
    private String location;

    public Report(String description, String location) {
        this.description = description;
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }
}
