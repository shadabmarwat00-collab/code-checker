package com.example.projectoop.services;

public interface Reportable {
    String generateReport();
    void exportToFile(String path);
}

