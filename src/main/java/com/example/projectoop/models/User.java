package com.example.projectoop.models;



import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String fullName;
    private String hashedPassword;
    private LocalDateTime registeredAt;
    private int totalAnalyses;

    public User(String username, String fullName, String hashedPassword) {
        this.username       = username;
        this.fullName       = fullName;
        this.hashedPassword = hashedPassword;
        this.registeredAt   = LocalDateTime.now();
        this.totalAnalyses  = 0;
    }

    public String getUsername()       { return username;       }
    public String getFullName()       { return fullName;       }
    public String getHashedPassword() { return hashedPassword; }
    public int    getTotalAnalyses()  { return totalAnalyses;  }

    public String getRegisteredDate() {
        return registeredAt.format(
                DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public void incrementAnalyses() { totalAnalyses++; }

    @Override
    public String toString() {
        return "User[" + username + " | " + fullName + "]";
    }
}