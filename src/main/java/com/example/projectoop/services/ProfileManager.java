package com.example.projectoop.services;

import com.example.projectoop.exceptions.CorruptProfileException;
import com.example.projectoop.models.DeveloperProfile;
import java.io.*;

public class ProfileManager {

    private static final String PROFILE_DIR = "profiles/";

    public ProfileManager() {
        // Create profiles directory if it doesn't exist
        File dir = new File(PROFILE_DIR);
        if (!dir.exists()) dir.mkdirs();
    }


    public void saveProfile(DeveloperProfile profile) throws CorruptProfileException {
        String path = PROFILE_DIR + profile.getDeveloperName() + ".profile";
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path))) {
            oos.writeObject(profile);
        } catch (IOException e) {
            throw new CorruptProfileException(
                    "Failed to save profile: " + e.getMessage(),
                    profile.getDeveloperName(), e);
        }
    }

    // Load developer profile from file
    public DeveloperProfile loadProfile(String developerName)
            throws CorruptProfileException {
        String path = PROFILE_DIR + developerName + ".profile";
        File file = new File(path);

        if (!file.exists()) return null; // No profile yet — first time

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (DeveloperProfile) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new CorruptProfileException(
                    "Profile file is corrupt or unreadable: " + e.getMessage(),
                    developerName, e);
        }
    }

    public boolean profileExists(String developerName) {
        return new File(PROFILE_DIR + developerName + ".profile").exists();
    }
}
