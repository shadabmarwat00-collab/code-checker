package com.example.projectoop.ui;

import com.example.projectoop.models.DeveloperProfile;
import com.example.projectoop.models.User;
import com.example.projectoop.services.ProfileManager;
import com.example.projectoop.services.UserManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardScreen {

    private ScrollPane root;
    private User currentUser;
    private ProfileManager profileManager;
    private UserManager userManager;

    // Holds leaderboard entry data
    private static class Entry {
        String username;
        String fullName;
        BigDecimal avgScore;
        int sessions;

        Entry(String username, String fullName,
              BigDecimal avgScore, int sessions) {
            this.username = username;
            this.fullName = fullName;
            this.avgScore = avgScore;
            this.sessions = sessions;
        }
    }

    public LeaderboardScreen(User currentUser, ProfileManager profileManager,
                             UserManager userManager) {
        this.currentUser    = currentUser;
        this.profileManager = profileManager;
        this.userManager    = userManager;
        buildUI();
    }

    private void buildUI() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(32, 40, 32, 40));
        content.setStyle("-fx-background-color:#111122;");

        Label title = new Label("🏆 Leaderboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label(
            "Rankings based on average quality score across all sessions");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#555577"));

        content.getChildren().addAll(title, subtitle);

        List<Entry> entries = buildEntries();

        if (entries.isEmpty()) {
            Label empty = new Label(
                "No data yet — analyze some files to appear on the leaderboard!");
            empty.setTextFill(Color.web("#555577"));
            content.getChildren().add(empty);
        } else {
            // Header row
            content.getChildren().add(buildHeaderRow());
            for (int i = 0; i < entries.size(); i++) {
                content.getChildren().add(
                    buildEntryRow(entries.get(i), i + 1));
            }
        }

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#111122;-fx-background:#111122;");
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private List<Entry> buildEntries() {
        List<Entry> entries = new ArrayList<>();

        // Load profiles directory and build entries
        java.io.File profileDir = new java.io.File("profiles");
        if (!profileDir.exists()) return entries;

        java.io.File[] files = profileDir.listFiles(
            (dir, name) -> name.endsWith(".profile"));
        if (files == null) return entries;

        for (java.io.File f : files) {
            String username = f.getName().replace(".profile", "");
            try {
                DeveloperProfile p = profileManager.loadProfile(username);
                if (p != null && p.getTotalSessions() > 0) {
                    // Get full name from profile username
                    entries.add(new Entry(
                        username,
                        formatName(username),
                        p.getAverageScore(),
                        p.getTotalSessions()
                    ));
                }
            } catch (Exception ignored) {}
        }

        // Sort by average score descending
        entries.sort((a, b) -> b.avgScore.compareTo(a.avgScore));
        return entries;
    }

    private String formatName(String username) {
        // Capitalize first letter
        if (username == null || username.isEmpty()) return username;
        return username.substring(0, 1).toUpperCase() + username.substring(1);
    }

    private HBox buildHeaderRow() {
        HBox row = new HBox();
        row.setPadding(new Insets(10, 20, 10, 20));

        Label rank  = headerCell("Rank",    60);
        Label name  = headerCell("Developer", 280);
        Label score = headerCell("Avg Score", 140);
        Label sess  = headerCell("Sessions",  100);

        row.getChildren().addAll(rank, name, score, sess);
        return row;
    }

    private HBox buildEntryRow(Entry entry, int rank) {
        boolean isMe = entry.username.equalsIgnoreCase(currentUser.getUsername());

        HBox row = new HBox();
        row.setPadding(new Insets(14, 20, 14, 20));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color:" + (isMe ? "#1a2a4a" : "#1a1a2e") + ";" +
            "-fx-border-color:" + (isMe ? "#3a5ac0" : "#2a2a4a") + ";" +
            "-fx-border-width:1;" +
            "-fx-border-radius:10;" +
            "-fx-background-radius:10;"
        );
        VBox.setMargin(row, new Insets(0, 0, 4, 0));

        // Rank medal
        String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉"
                     : String.valueOf(rank);
        Label rankLabel = new Label(medal);
        rankLabel.setFont(Font.font("Arial", FontWeight.BOLD,
            rank <= 3 ? 22 : 15));
        rankLabel.setTextFill(Color.web("#aaaacc"));
        rankLabel.setPrefWidth(60);

        // Name
        VBox nameBox = new VBox(2);
        nameBox.setPrefWidth(280);
        Label nameLabel = new Label(formatName(entry.username)
            + (isMe ? "  (You)" : ""));
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(isMe ? Color.web("#7c9ef8") : Color.WHITE);
        Label handleLabel = new Label("@" + entry.username);
        handleLabel.setFont(Font.font("Arial", 11));
        handleLabel.setTextFill(Color.web("#444466"));
        nameBox.getChildren().addAll(nameLabel, handleLabel);

        // Score
        int scoreInt = entry.avgScore.intValue();
        String scoreColor = scoreInt >= 75 ? "#6bcb77"
                          : scoreInt >= 50 ? "#ffd93d" : "#ff6b6b";
        Label scoreLabel = new Label(entry.avgScore.toString());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        scoreLabel.setTextFill(Color.web(scoreColor));
        scoreLabel.setPrefWidth(140);

        // Sessions
        Label sessLabel = new Label(entry.sessions + " sessions");
        sessLabel.setFont(Font.font("Arial", 12));
        sessLabel.setTextFill(Color.web("#555577"));
        sessLabel.setPrefWidth(100);

        row.getChildren().addAll(rankLabel, nameBox, scoreLabel, sessLabel);
        return row;
    }

    private Label headerCell(String text, double width) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.web("#444466"));
        l.setPrefWidth(width);
        return l;
    }

    public Parent getRoot() { return root; }
}
