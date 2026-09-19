package com.example.projectoop.ui;

import com.example.projectoop.models.AnalysisSession;
import com.example.projectoop.models.DeveloperProfile;
import com.example.projectoop.models.User;
import com.example.projectoop.models.Violation;
import com.example.projectoop.services.ProfileManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class ProfileScreen {

    private ScrollPane root;
    private User currentUser;
    private DeveloperProfile profile;

    public ProfileScreen(User currentUser, ProfileManager profileManager) {
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
        content.setStyle("-fx-background-color: #111122;");

        Label title = new Label("👤 My Profile");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        content.getChildren().addAll(title, buildProfileCard(),
            buildFingerprintCard(), buildSessionHistory());

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#111122;-fx-background:#111122;");
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private HBox buildProfileCard() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        String initials = String.valueOf(
            currentUser.getFullName().charAt(0)).toUpperCase();
        Label avatar = new Label(initials);
        avatar.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        avatar.setTextFill(Color.web("#7c9ef8"));
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(80, 80);
        avatar.setStyle(
            "-fx-background-color:#1a2a4a;-fx-background-radius:40;");

        VBox info = new VBox(6);
        Label name = new Label(currentUser.getFullName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        name.setTextFill(Color.WHITE);
        Label handle = new Label("@" + currentUser.getUsername());
        handle.setFont(Font.font("Arial", 13));
        handle.setTextFill(Color.web("#555577"));
        Label joined = new Label("Member since: " + currentUser.getRegisteredDate());
        joined.setFont(Font.font("Arial", 12));
        joined.setTextFill(Color.web("#444466"));
        Label analyses = new Label("Total analyses run: " + currentUser.getTotalAnalyses());
        analyses.setFont(Font.font("Arial", 12));
        analyses.setTextFill(Color.web("#444466"));
        info.getChildren().addAll(name, handle, joined, analyses);

        row.getChildren().addAll(avatar, info);
        return row;
    }

    private VBox buildFingerprintCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color:#1a1a2e;-fx-border-color:#2a2a4a;" +
            "-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");

        Label header = sectionLabel("🔍 Developer Fingerprint");
        card.getChildren().add(header);

        if (profile.getSessions().isEmpty()) {
            Label empty = new Label("Analyze some files to build your fingerprint!");
            empty.setTextFill(Color.web("#555577"));
            card.getChildren().add(empty);
            return card;
        }

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            miniStat("Sessions", String.valueOf(profile.getTotalSessions())),
            miniStat("Avg Score", profile.getAverageScore() + "/100"),
            miniStat("Trend", profile.getGrowthTrend()),
            miniStat("Total Violations",
                String.valueOf(profile.getTotalViolations()))
        );
        card.getChildren().add(statsRow);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a2a4a;");
        card.getChildren().add(sep);

        Label weakTitle = new Label("Your Top Weaknesses:");
        weakTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        weakTitle.setTextFill(Color.web("#aaaacc"));
        card.getChildren().add(weakTitle);

        List<String> weaknesses = profile.getTopWeaknesses();
        for (int i = 0; i < weaknesses.size(); i++) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label num = new Label((i + 1) + ".");
            num.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            num.setTextFill(Color.web("#7c9ef8"));
            num.setPrefWidth(20);
            Label w = new Label(weaknesses.get(i));
            w.setFont(Font.font("Arial", 13));
            w.setTextFill(Color.web("#ff9f43"));
            row.getChildren().addAll(num, w);
            card.getChildren().add(row);
        }
        return card;
    }

    private VBox buildSessionHistory() {
        VBox box = new VBox(10);
        box.getChildren().add(sectionLabel("📋 All Sessions"));

        if (profile.getSessions().isEmpty()) {
            Label empty = new Label("No sessions yet.");
            empty.setTextFill(Color.web("#555577"));
            box.getChildren().add(empty);
            return box;
        }

        List<AnalysisSession> sessions = profile.getSessions();
        for (int i = sessions.size() - 1; i >= 0; i--) {
            AnalysisSession s = sessions.get(i);
            box.getChildren().add(buildSessionCard(s, i + 1));
        }
        return box;
    }

    private VBox buildSessionCard(AnalysisSession s, int num) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle(
            "-fx-background-color:#1a1a2e;-fx-border-color:#2a2a4a;" +
            "-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label numLabel = new Label("#" + num);
        numLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        numLabel.setTextFill(Color.web("#555577"));

        Label fileName = new Label(s.getFileName());
        fileName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        fileName.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        int score = s.getQualityScore().intValue();
        String col = score >= 75 ? "#6bcb77" : score >= 50 ? "#ffd93d" : "#ff6b6b";
        Label scoreLabel = new Label(s.getQualityScore() + " / 100");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        scoreLabel.setTextFill(Color.web(col));

        header.getChildren().addAll(numLabel, fileName, spacer, scoreLabel);

        HBox meta = new HBox(20);
        Label date = infoLabel("📅 " + s.getFormattedTime());
        Label viol = infoLabel("⚠ " + s.getViolationCount() + " violations");
        Label grade = infoLabel("🎓 " + s.getScoreGrade());
        meta.getChildren().addAll(date, viol, grade);

        card.getChildren().addAll(header, meta);
        return card;
    }

    private VBox miniStat(String label, String value) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(12, 16, 12, 16));
        box.setStyle(
            "-fx-background-color:#111133;-fx-border-radius:8;" +
            "-fx-background-radius:8;");
        Label l = new Label(label);
        l.setFont(Font.font("Arial", 10));
        l.setTextFill(Color.web("#555577"));
        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        v.setTextFill(Color.WHITE);
        box.getChildren().addAll(l, v);
        return box;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        l.setTextFill(Color.web("#7c9ef8"));
        return l;
    }

    private Label infoLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", 11));
        l.setTextFill(Color.web("#555577"));
        return l;
    }

    public Parent getRoot() { return root; }
}
