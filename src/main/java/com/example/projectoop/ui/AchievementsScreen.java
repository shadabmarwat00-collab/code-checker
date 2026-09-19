package com.example.projectoop.ui;

import com.example.projectoop.models.DeveloperProfile;
import com.example.projectoop.models.User;
import com.example.projectoop.services.ProfileManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class AchievementsScreen {

    private ScrollPane root;
    private User currentUser;
    private DeveloperProfile profile;

    // Holds achievement data
    private static class Achievement {
        String icon;
        String title;
        String description;
        boolean unlocked;

        Achievement(String icon, String title,
                    String description, boolean unlocked) {
            this.icon        = icon;
            this.title       = title;
            this.description = description;
            this.unlocked    = unlocked;
        }
    }

    public AchievementsScreen(User currentUser, ProfileManager profileManager) {
        this.currentUser = currentUser;
        try {
            profile = profileManager.loadProfile(currentUser.getUsername());
            if (profile == null)
                profile = new DeveloperProfile(currentUser.getUsername());
        } catch (Exception e) {
            profile = new DeveloperProfile(currentUser.getUsername());
        }
        buildUI();
    }

    private void buildUI() {
        VBox content = new VBox(24);
        content.setPadding(new Insets(32, 40, 32, 40));
        content.setStyle("-fx-background-color:#111122;");

        Label title = new Label("🎖 Achievements");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        int unlocked = (int) buildAchievements().stream()
            .filter(a -> a.unlocked).count();
        int total    = buildAchievements().size();

        Label progress = new Label("Unlocked: " + unlocked + " / " + total);
        progress.setFont(Font.font("Arial", 13));
        progress.setTextFill(Color.web("#7c9ef8"));

        content.getChildren().addAll(title, progress, buildProgressBar(unlocked, total));

        // Grid of achievements
        FlowPane grid = new FlowPane(16, 16);
        grid.setPrefWrapLength(820);

        for (Achievement a : buildAchievements()) {
            grid.getChildren().add(buildBadgeCard(a));
        }

        content.getChildren().add(grid);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#111122;-fx-background:#111122;");
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private List<Achievement> buildAchievements() {
        List<Achievement> list = new ArrayList<>();
        int sessions  = profile.getTotalSessions();
        int avgScore  = profile.getAverageScore().intValue();
        int total     = profile.getTotalViolations();

        // Get last session score safely
        int lastScore = 0;
        if (profile.getLastSession() != null)
            lastScore = profile.getLastSession().getQualityScore().intValue();

        // Beginner achievements
        list.add(new Achievement("🚀", "First Analysis",
            "Run your first code analysis",
            sessions >= 1));

        list.add(new Achievement("🔥", "Getting Started",
            "Complete 5 analysis sessions",
            sessions >= 5));

        list.add(new Achievement("💪", "Dedicated Developer",
            "Complete 10 analysis sessions",
            sessions >= 10));

        list.add(new Achievement("🏃", "Marathon Coder",
            "Complete 25 analysis sessions",
            sessions >= 25));

        // Score achievements
        list.add(new Achievement("⭐", "First Star",
            "Score above 50 in any session",
            lastScore >= 50 || avgScore >= 50));

        list.add(new Achievement("🌟", "Good Code",
            "Score above 75 in any session",
            lastScore >= 75 || avgScore >= 75));

        list.add(new Achievement("✨", "Clean Code",
            "Score above 90 in any session",
            lastScore >= 90 || avgScore >= 90));

        list.add(new Achievement("💎", "Perfect Code",
            "Score 100 in any session",
            lastScore == 100));

        // Average score achievements
        list.add(new Achievement("📈", "Consistent",
            "Maintain average score above 60",
            avgScore >= 60));

        list.add(new Achievement("🎯", "Sharp Developer",
            "Maintain average score above 80",
            avgScore >= 80));

        // Improvement achievements
        list.add(new Achievement("📉", "Violation Hunter",
            "Have more than 10 total violations detected",
            total >= 10));

        list.add(new Achievement("🧹", "Clean Sweep",
            "Analyze a file with zero violations",
            hasCleanSession()));

        list.add(new Achievement("🔄", "Improving",
            "Show an improving trend",
            profile.getGrowthTrend().contains("▲")));

        list.add(new Achievement("🏆", "Champion",
            "Average score above 90 with 5+ sessions",
            avgScore >= 90 && sessions >= 5));

        return list;
    }

    private boolean hasCleanSession() {
        return profile.getSessions().stream()
            .anyMatch(s -> s.getViolationCount() == 0);
    }

    private VBox buildBadgeCard(Achievement a) {
        VBox card = new VBox(10);
        card.setPrefWidth(200);
        card.setPrefHeight(150);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);

        String bg     = a.unlocked ? "#1a2a1a" : "#1a1a2e";
        String border = a.unlocked ? "#2a5a2a" : "#2a2a4a";
        String opacity = a.unlocked ? "1.0" : "0.4";

        card.setStyle(
            "-fx-background-color:" + bg + ";" +
            "-fx-border-color:" + border + ";" +
            "-fx-border-width:1;" +
            "-fx-border-radius:14;" +
            "-fx-background-radius:14;" +
            "-fx-opacity:" + opacity + ";"
        );

        Label icon = new Label(a.unlocked ? a.icon : "🔒");
        icon.setFont(Font.font("Arial", 32));

        Label title = new Label(a.title);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        title.setTextFill(a.unlocked ? Color.WHITE : Color.web("#555577"));
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);

        Label desc = new Label(a.description);
        desc.setFont(Font.font("Arial", 10));
        desc.setTextFill(Color.web("#444466"));
        desc.setWrapText(true);
        desc.setAlignment(Pos.CENTER);

        if (a.unlocked) {
            Label badge = new Label("✓ Unlocked");
            badge.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            badge.setTextFill(Color.web("#6bcb77"));
            badge.setStyle(
                "-fx-background-color:#0a2a0a;" +
                "-fx-padding:2 8;" +
                "-fx-background-radius:10;"
            );
            card.getChildren().addAll(icon, title, desc, badge);
        } else {
            card.getChildren().addAll(icon, title, desc);
        }
        return card;
    }

    private HBox buildProgressBar(int unlocked, int total) {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);

        double pct = total == 0 ? 0 : (double) unlocked / total;

        StackPane track = new StackPane();
        track.setPrefWidth(500);
        track.setPrefHeight(8);
        track.setStyle(
            "-fx-background-color:#1e1e3a;" +
            "-fx-background-radius:4;"
        );

        HBox fill = new HBox();
        fill.setPrefWidth(500 * pct);
        fill.setPrefHeight(8);
        fill.setStyle(
            "-fx-background-color:#7c9ef8;" +
            "-fx-background-radius:4;"
        );
        fill.setAlignment(Pos.CENTER_LEFT);

        track.getChildren().add(fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        Label pctLabel = new Label(
            String.format("%.0f%%", pct * 100) + " complete");
        pctLabel.setFont(Font.font("Arial", 12));
        pctLabel.setTextFill(Color.web("#555577"));

        bar.getChildren().addAll(track, pctLabel);
        return bar;
    }

    public Parent getRoot() { return root; }
}
