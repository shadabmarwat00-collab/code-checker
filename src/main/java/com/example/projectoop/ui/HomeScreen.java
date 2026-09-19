package com.example.projectoop.ui;

import com.example.projectoop.models.AnalysisSession;
import com.example.projectoop.models.DeveloperProfile;
import com.example.projectoop.models.User;
import com.example.projectoop.services.ProfileManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalTime;

public class HomeScreen {

    private ScrollPane root;
    private User currentUser;
    private ProfileManager profileManager;
    private DeveloperProfile profile;

    public HomeScreen(User currentUser, ProfileManager profileManager) {
        this.currentUser    = currentUser;
        this.profileManager = profileManager;
        loadProfile();
        buildUI();
    }

    private void loadProfile() {
        try {
            profile = profileManager.loadProfile(currentUser.getUsername());
            if (profile == null)
                profile = new DeveloperProfile(currentUser.getUsername());
        } catch (Exception e) {
            profile = new DeveloperProfile(currentUser.getUsername());
        }
    }

    private void buildUI() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(36, 40, 36, 40));
        content.setStyle("-fx-background-color: #111122;");

        // Greeting
        content.getChildren().add(buildGreeting());

        // Quick stats row
        content.getChildren().add(buildStatsRow());

        // Recent activity
        content.getChildren().add(buildRecentActivity());

        // Tips
        content.getChildren().add(buildTips());

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #111122; -fx-background: #111122;");
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private VBox buildGreeting() {
        VBox box = new VBox(6);
        String timeGreeting = getTimeGreeting();
        Label greeting = new Label(timeGreeting + ", " + currentUser.getFullName() + " 👋");
        greeting.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        greeting.setTextFill(Color.WHITE);

        String subtitle = profile.getTotalSessions() == 0
            ? "Welcome! Load a Java file in Analyze Code to get started."
            : "You have analyzed " + profile.getTotalSessions()
              + " session(s). Your average score is "
              + profile.getAverageScore() + "/100.";

        Label sub = new Label(subtitle);
        sub.setFont(Font.font("Arial", 14));
        sub.setTextFill(Color.web("#8888aa"));
        sub.setWrapText(true);
        box.getChildren().addAll(greeting, sub);
        return box;
    }

    private HBox buildStatsRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        String lastScore = "--";
        String trend     = "No data";
        if (profile.getLastSession() != null) {
            lastScore = profile.getLastSession().getQualityScore().toString();
            trend     = profile.getGrowthTrend();
        }

        row.getChildren().addAll(
            statCard("📁", "Total Sessions",
                String.valueOf(profile.getTotalSessions()), "#3a5ac0"),
            statCard("⭐", "Average Score",
                profile.getAverageScore() + " / 100", "#2e7d52"),
            statCard("📈", "Trend",
                trend, "#7a4a00"),
            statCard("🎯", "Last Score",
                lastScore + (lastScore.equals("--") ? "" : " / 100"), "#6a2a6a")
        );
        return row;
    }

    private VBox statCard(String icon, String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPrefWidth(190);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-border-color: #2a2a4a;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 24));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 11));
        titleLabel.setTextFill(Color.web("#6666aa"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setWrapText(true);

        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }

    private VBox buildRecentActivity() {
        VBox box = new VBox(12);

        Label header = sectionHeader("📋 Recent Sessions");
        box.getChildren().add(header);

        if (profile.getSessions().isEmpty()) {
            Label empty = new Label("No sessions yet — go to Analyze Code to start!");
            empty.setTextFill(Color.web("#555577"));
            empty.setFont(Font.font("Arial", 13));
            box.getChildren().add(empty);
            return box;
        }

        // Show last 5 sessions
        java.util.List<AnalysisSession> sessions = profile.getSessions();
        int start = Math.max(0, sessions.size() - 5);
        for (int i = sessions.size() - 1; i >= start; i--) {
            AnalysisSession s = sessions.get(i);
            box.getChildren().add(sessionRow(s));
        }
        return box;
    }

    private HBox sessionRow(AnalysisSession s) {
        HBox row = new HBox(16);
        row.setPadding(new Insets(14, 18, 14, 18));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-border-color: #2a2a4a;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );

        Label fileIcon = new Label("☕");
        fileIcon.setFont(Font.font("Arial", 18));

        VBox info = new VBox(3);
        Label fileName = new Label(s.getFileName());
        fileName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        fileName.setTextFill(Color.WHITE);
        Label date = new Label(s.getFormattedTime() + "  •  "
            + s.getViolationCount() + " violations");
        date.setFont(Font.font("Arial", 11));
        date.setTextFill(Color.web("#555577"));
        info.getChildren().addAll(fileName, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        int score = s.getQualityScore().intValue();
        String scoreColor = score >= 75 ? "#6bcb77" : score >= 50 ? "#ffd93d" : "#ff6b6b";
        Label scoreLabel = new Label(s.getQualityScore() + "");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreLabel.setTextFill(Color.web(scoreColor));

        row.getChildren().addAll(fileIcon, info, spacer, scoreLabel);
        return row;
    }

    private VBox buildTips() {
        VBox box = new VBox(12);
        box.getChildren().add(sectionHeader("💡 Quick Tips"));

        String[] tips = {
            "⚡  Keep methods under 20 lines for better readability",
            "🔍  Always check for null when accepting object parameters",
            "📦  Avoid nesting code deeper than 3 levels",
            "✏️   Use descriptive variable names instead of x, temp, or data",
            "♻️   If you write the same code twice, extract it to a method"
        };

        for (String tip : tips) {
            Label l = new Label(tip);
            l.setFont(Font.font("Arial", 13));
            l.setTextFill(Color.web("#7788bb"));
            l.setPadding(new Insets(8, 14, 8, 14));
            l.setStyle(
                "-fx-background-color: #151528;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
            );
            l.setMaxWidth(Double.MAX_VALUE);
            box.getChildren().add(l);
        }
        return box;
    }

    private Label sectionHeader(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        l.setTextFill(Color.web("#7c9ef8"));
        return l;
    }

    private String getTimeGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }

    public Parent getRoot() { return root; }

    // Inner ScrollPane class reference
    private static class ScrollPane extends javafx.scene.control.ScrollPane {
        public ScrollPane(javafx.scene.Node content) { super(content); }
    }
}
