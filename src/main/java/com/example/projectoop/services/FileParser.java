package com.example.projectoop.services;

import com.example.projectoop.exceptions.InvalidFileException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileParser {

    public String readFile(String filePath) throws InvalidFileException {
        File file = new File(filePath);

        // Validate file exists
        if (!file.exists()) {
            throw new InvalidFileException("File does not exist", filePath);
        }

        // Validate it is a .java file
        if (!filePath.endsWith(".java")) {
            throw new InvalidFileException(
                    "Only .java files are supported", filePath);
        }

        // Validate it is readable
        if (!file.canRead()) {
            throw new InvalidFileException("File cannot be read", filePath);
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            // Chained exception — wraps original IO cause
            throw new InvalidFileException(
                    "Error reading file: " + e.getMessage(), filePath, e);
        }

        return content.toString();
    }

    public String extractFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }
}
