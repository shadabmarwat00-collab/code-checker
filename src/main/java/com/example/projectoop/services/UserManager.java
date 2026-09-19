package com.example.projectoop.services;

import com.example.projectoop.models.User;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private static final String USERS_FILE = "data/users.dat";
    private Map<String, User> users;

    public UserManager() {
        new File("data").mkdirs();
        users = loadUsers();
    }

    public boolean register(String username, String fullName, String password) {
        if (users.containsKey(username.toLowerCase())) return false;
        String hashed = hashPassword(password);
        User newUser  = new User(username.toLowerCase(), fullName, hashed);
        users.put(username.toLowerCase(), newUser);
        saveUsers();
        return true;
    }

    public User login(String username, String password) {
        User user = users.get(username.toLowerCase());
        if (user == null) return null;
        if (user.getHashedPassword().equals(hashPassword(password))) return user;
        return null;
    }

    public void saveUser(User user) {
        users.put(user.getUsername().toLowerCase(), user);
        saveUsers();
    }

    public boolean usernameExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    public boolean changePassword(String username, String newPassword) {
        User user = users.get(username.toLowerCase());
        if (user == null) return false;
        User updated = new User(user.getUsername(),
                user.getFullName(), hashPassword(newPassword));
        users.put(username.toLowerCase(), updated);
        saveUsers();
        return true;
    }

    public boolean deleteUser(String username) {
        if (!users.containsKey(username.toLowerCase())) return false;
        users.remove(username.toLowerCase());
        saveUsers();
        return true;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash)
                hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return new StringBuilder(password).reverse().toString();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, User> loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }
}
