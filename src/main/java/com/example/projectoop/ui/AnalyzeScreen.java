package com.example.projectoop.ui;

import com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.exceptions.CorruptProfileException;
import com.example.projectoop.exceptions.InvalidFileException;
import com.example.projectoop.models.AnalysisSession;
import com.example.projectoop.models.DeveloperProfile;
import com.example.projectoop.models.User;
import com.example.projectoop.models.Violation;
import com.example.projectoop.services.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class AnalyzeScreen {

    private BorderPane root;
    private User currentUser;
    private ProfileManager profileManager;
    private UserManager userManager;

    private FileParser      fileParser = new FileParser();
    private RuleEngine      ruleEngine = new RuleEngine();
    private ScoreCalculator scoreCalc  = new ScoreCalculator();

    private DeveloperProfile currentProfile;
    private String           loadedFilePath;

    private Label    loadedFileLabel;
    private VBox     violationsContainer;
    private Label    scoreLabel;
    private Label    gradeLabel;
    private Label    statusLabel;
    private VBox     breakdownBox;
    private ScrollPane violationsScroll;

    public AnalyzeScreen(User currentUser, ProfileManager profileManager,
                         UserManager userManager) {
        this.currentUser    = currentUser;
        this.profileManager = profileManager;
        this.userManager    = userManager;
        loadProfile();
        buildUI();
    }

    private void loadProfile() {
        try {
            currentProfile = profileManager.loadProfile(currentUser.getUsername());
            if (currentProfile == null)
                currentProfile = new DeveloperProfile(currentUser.getUsername());
        } catch (Exception e) {
            currentProfile = new DeveloperProfile(currentUser.getUsername());
        }
    }

    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color:#111122;");

        // Top bar
        HBox topBar = new HBox(16);
        topBar.setPadding(new Insets(24, 32, 12, 32));
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("⚡ Analyze Code");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);
        topBar.getChildren().add(title);
        root.setTop(topBar);

        // Main content
        HBox content = new HBox(20);
        content.setPadding(new Insets(0, 24, 16, 32));
        content.setStyle("-fx-background-color:#111122;");

        content.getChildren().addAll(buildLeftPanel(), buildRightPanel());
        root.setCenter(content);

        // Status bar
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(8, 32, 14, 32));
        statusLabel = new Label("Load a Java file and click Analyze");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setTextFill(Color.web("#555577"));
        bottomBar.getChildren().add(statusLabel);
        root.setBottom(bottomBar);
    }

    // ── LEFT — file controls + violation cards ────────────────────────────
    private VBox buildLeftPanel() {
        VBox panel = new VBox(12);
        panel.setPrefWidth(480);

        Button chooseBtn = actionButton("📂  Load Java File", "#3a5ac0");
        chooseBtn.setPrefWidth(460);
        chooseBtn.setOnAction(e -> chooseFile());

        loadedFileLabel = new Label("No file loaded");
        loadedFileLabel.setTextFill(Color.web("#555577"));
        loadedFileLabel.setFont(Font.font("Arial", 11));

        Button analyzeBtn = actionButton("▶  Analyze File", "#2e7d52");
        analyzeBtn.setPrefWidth(460);
        analyzeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        analyzeBtn.setOnAction(e -> runAnalysis());

        // Violations label
        Label violLabel = new Label("Violations");
        violLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        violLabel.setTextFill(Color.web("#7c9ef8"));

        // Scrollable violation cards
        violationsContainer = new VBox(10);
        violationsContainer.setPadding(new Insets(4, 4, 4, 0));

        Label placeholder = new Label("Violations will appear here after analysis.");
        placeholder.setTextFill(Color.web("#333355"));
        placeholder.setFont(Font.font("Arial", 13));
        violationsContainer.getChildren().add(placeholder);

        violationsScroll = new ScrollPane(violationsContainer);
        violationsScroll.setPrefHeight(440);
        violationsScroll.setFitToWidth(true);
        violationsScroll.setStyle(
                "-fx-background-color:#111122;-fx-background:#111122;" +
                        "-fx-border-color:transparent;"
        );
        violationsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Button exportBtn = actionButton("💾  Export Report", "#3a3a6a");
        exportBtn.setPrefWidth(460);
        exportBtn.setOnAction(e -> exportReport());

        panel.getChildren().addAll(
                chooseBtn, loadedFileLabel, analyzeBtn,
                violLabel, violationsScroll, exportBtn
        );
        return panel;
    }

    // ── RIGHT — score + breakdown ─────────────────────────────────────────
    private VBox buildRightPanel() {
        VBox panel = new VBox(16);
        panel.setPrefWidth(380);

        // Score card
        VBox scoreCard = new VBox(8);
        scoreCard.setAlignment(Pos.CENTER);
        scoreCard.setPadding(new Insets(28));
        scoreCard.setStyle(
                "-fx-background-color:#1a1a2e;-fx-border-color:#2a2a4a;" +
                        "-fx-border-width:1;-fx-border-radius:16;-fx-background-radius:16;"
        );

        Label scoreTitleLbl = new Label("Quality Score");
        scoreTitleLbl.setFont(Font.font("Arial", 12));
        scoreTitleLbl.setTextFill(Color.web("#555577"));

        scoreLabel = new Label("--");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        scoreLabel.setTextFill(Color.web("#7c9ef8"));

        Label outOf = new Label("out of 100");
        outOf.setTextFill(Color.web("#333355"));
        outOf.setFont(Font.font("Arial", 12));

        gradeLabel = new Label("Analyze a file to begin");
        gradeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gradeLabel.setTextFill(Color.web("#8888aa"));
        gradeLabel.setWrapText(true);
        gradeLabel.setAlignment(Pos.CENTER);

        scoreCard.getChildren().addAll(scoreTitleLbl, scoreLabel, outOf, gradeLabel);

        // Breakdown card
        VBox breakdownCard = new VBox(10);
        breakdownCard.setPadding(new Insets(20));
        breakdownCard.setStyle(
                "-fx-background-color:#1a1a2e;-fx-border-color:#2a2a4a;" +
                        "-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;"
        );

        Label breakdownTitle = new Label("📊 Rule Breakdown");
        breakdownTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        breakdownTitle.setTextFill(Color.web("#7c9ef8"));

        breakdownBox = new VBox(10);
        String[] rules = {"Method Length","Nesting Depth",
                "Naming Convention","Null Check","Duplicate Logic"};
        for (String r : rules) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label dot = new Label("●");
            dot.setTextFill(Color.web("#3a3a6a"));
            dot.setFont(Font.font("Arial", 10));
            Label rl = new Label(r + ":  awaiting analysis");
            rl.setFont(Font.font("Courier New", 11));
            rl.setTextFill(Color.web("#555577"));
            rl.setId("rule_" + r.replace(" ", "_"));
            row.getChildren().addAll(dot, rl);
            breakdownBox.getChildren().add(row);
        }
        breakdownCard.getChildren().addAll(breakdownTitle, breakdownBox);

        // Tips card
        VBox tipsCard = new VBox(8);
        tipsCard.setPadding(new Insets(16));
        tipsCard.setStyle(
                "-fx-background-color:#131320;-fx-border-color:#1e1e3a;" +
                        "-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;"
        );
        Label tipsTitle = new Label("💡 How scoring works");
        tipsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        tipsTitle.setTextFill(Color.web("#555577"));
        String[] tips = {
                "HIGH violation = -8 points",
                "MEDIUM violation = -4 points",
                "LOW violation = -2 points",
                "Start score: 100"
        };
        tipsCard.getChildren().add(tipsTitle);
        for (String t : tips) {
            Label tl = new Label("• " + t);
            tl.setFont(Font.font("Arial", 11));
            tl.setTextFill(Color.web("#444466"));
            tipsCard.getChildren().add(tl);
        }

        panel.getChildren().addAll(scoreCard, breakdownCard, tipsCard);
        return panel;
    }

    // ── ACTIONS ───────────────────────────────────────────────────────────
    private void chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Java File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Java Files", "*.java"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            loadedFilePath = file.getAbsolutePath();
            loadedFileLabel.setText("📄 " + file.getName());
            setStatus("File loaded: " + file.getName(), false);
        }
    }

    private void runAnalysis() {
        if (loadedFilePath == null) {
            setStatus("Please load a Java file first", true);
            return;
        }
        try {
            String content  = fileParser.readFile(loadedFilePath);
            String fileName = fileParser.extractFileName(loadedFilePath);

            List<Violation> violations = ruleEngine.runAllRules(content);
            BigDecimal score = scoreCalc.computeScore(violations);

            AnalysisSession session = new AnalysisSession(
                    fileName, currentUser.getUsername());
            session.addAllViolations(violations);
            session.setQualityScore(score);

            currentProfile.addSession(session);
            profileManager.saveProfile(currentProfile);
            currentUser.incrementAnalyses();
            userManager.saveUser(currentUser);

            displayResults(session, content);
            setStatus("Analysis complete — " + violations.size()
                    + " violations | Score: " + score, false);

        } catch (InvalidFileException e) {
            setStatus("File error: " + e.getMessage(), true);
        } catch (AnalysisFailureException e) {
            setStatus("Analysis failed: " + e.getMessage(), true);
        } catch (CorruptProfileException e) {
            setStatus("Could not save: " + e.getMessage(), true);
        }
    }

    private void displayResults(AnalysisSession session, String fileContent) {
        // Animate score
        int target = session.getQualityScore().intValue();
        int[] cur = {0};
        if (target == 0) {
            scoreLabel.setText("0");
            scoreLabel.setTextFill(Color.web("#ff6b6b"));
        } else {
            Timeline tl = new Timeline(new KeyFrame(Duration.millis(18), e -> {
                if (cur[0] < target) {
                    cur[0]++;
                    scoreLabel.setText(String.valueOf(cur[0]));
                    scoreLabel.setTextFill(cur[0] >= 75
                            ? Color.web("#6bcb77")
                            : cur[0] >= 50 ? Color.web("#ffd93d") : Color.web("#ff6b6b"));
                }
            }));
            tl.setCycleCount(target);
            tl.play();
        }
        gradeLabel.setText(session.getScoreGrade());

        // Build violation cards
        violationsContainer.getChildren().clear();
        List<Violation> violations = session.getViolations();

        if (violations.isEmpty()) {
            Label clean = new Label("✅  No violations found — excellent code!");
            clean.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            clean.setTextFill(Color.web("#6bcb77"));
            clean.setPadding(new Insets(20));
            violationsContainer.getChildren().add(clean);
        } else {
            String[] fileLines = fileContent.split("\n");
            for (Violation v : violations) {
                violationsContainer.getChildren().add(
                        buildViolationCard(v, fileLines));
            }
        }

        // Update rule breakdown
        String[] ruleNames = {"Method Length","Nesting Depth",
                "Naming Convention","Null Check","Duplicate Logic"};
        for (javafx.scene.Node node : breakdownBox.getChildren()) {
            if (node instanceof HBox) {
                for (javafx.scene.Node child : ((HBox) node).getChildren()) {
                    if (child instanceof Label && ((Label)child).getId() != null) {
                        Label lbl = (Label) child;
                        for (String rn : ruleNames) {
                            if (lbl.getId().equals("rule_" + rn.replace(" ", "_"))) {
                                long count = violations.stream()
                                        .filter(v -> v.getRuleName().equals(rn)).count();
                                lbl.setText(rn + ":  " + count
                                        + " violation" + (count == 1 ? "" : "s"));
                                lbl.setTextFill(count > 0
                                        ? Color.web("#ff6b6b") : Color.web("#6bcb77"));
                            }
                        }
                    }
                }
            }
        }
    }

    // ── VIOLATION CARD ────────────────────────────────────────────────────
    private VBox buildViolationCard(Violation v, String[] fileLines) {
        VBox card = new VBox(0);
        card.setStyle(
                "-fx-background-color:#1a1a2e;-fx-border-color:#2a2a4a;" +
                        "-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;"
        );

        // Header bar
        String severityColor = v.getSeverity() == Violation.HIGH   ? "#ff6b6b"
                : v.getSeverity() == Violation.MEDIUM ? "#ffd93d"
                : "#6bcb77";

        HBox header = new HBox(10);
        header.setPadding(new Insets(10, 14, 10, 14));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color:#141428;" +
                        "-fx-background-radius:10 10 0 0;"
        );

        Label severityBadge = new Label(v.getSeverityLabel());
        severityBadge.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        severityBadge.setTextFill(Color.web(severityColor));
        severityBadge.setStyle(
                "-fx-background-color:#0d0d1e;" +
                        "-fx-padding:2 8;-fx-background-radius:4;"
        );

        Label ruleName = new Label(v.getRuleName());
        ruleName.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ruleName.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lineNum = new Label("Line " + v.getLineNumber());
        lineNum.setFont(Font.font("Courier New", 11));
        lineNum.setTextFill(Color.web("#555577"));

        header.getChildren().addAll(severityBadge, ruleName, spacer, lineNum);

        // Body
        VBox body = new VBox(8);
        body.setPadding(new Insets(12, 14, 12, 14));

        // Message
        Label msgLabel = new Label(v.getMessage());
        msgLabel.setFont(Font.font("Arial", 12));
        msgLabel.setTextFill(Color.web("#aaaacc"));
        msgLabel.setWrapText(true);

        body.getChildren().add(msgLabel);

        // Code snippet — show actual line from file if available
        String snippet = v.getCodeSnippet();
        if (snippet == null || snippet.isEmpty()) {
            // Try to get from file lines directly
            int lineIdx = v.getLineNumber() - 1;
            if (lineIdx >= 0 && lineIdx < fileLines.length) {
                snippet = fileLines[lineIdx].trim();
            }
        }

        if (snippet != null && !snippet.isEmpty()) {
            VBox snippetBox = new VBox(4);
            snippetBox.setStyle(
                    "-fx-background-color:#0d0d1e;" +
                            "-fx-padding:8 12;-fx-border-radius:6;-fx-background-radius:6;" +
                            "-fx-border-color:#1e1e3a;-fx-border-width:1;"
            );
            Label snippetTitle = new Label("Code at line " + v.getLineNumber() + ":");
            snippetTitle.setFont(Font.font("Arial", 10));
            snippetTitle.setTextFill(Color.web("#333355"));

            // Truncate if very long
            String displaySnippet = snippet.length() > 80
                    ? snippet.substring(0, 80) + "..." : snippet;
            Label snippetCode = new Label(displaySnippet);
            snippetCode.setFont(Font.font("Courier New", 11));
            snippetCode.setTextFill(Color.web("#ffd93d"));
            snippetCode.setWrapText(true);
            snippetBox.getChildren().addAll(snippetTitle, snippetCode);
            body.getChildren().add(snippetBox);
        }

        // Fix suggestion
        if (v.getFixSuggestion() != null && !v.getFixSuggestion().isEmpty()) {
            VBox fixBox = new VBox(4);
            fixBox.setStyle(
                    "-fx-background-color:#0a1a0a;" +
                            "-fx-padding:8 12;-fx-border-radius:6;-fx-background-radius:6;" +
                            "-fx-border-color:#1a3a1a;-fx-border-width:1;"
            );
            Label fixTitle = new Label("✅  How to fix:");
            fixTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            fixTitle.setTextFill(Color.web("#2a5a2a"));

            Label fixText = new Label(v.getFixSuggestion());
            fixText.setFont(Font.font("Arial", 11));
            fixText.setTextFill(Color.web("#6bcb77"));
            fixText.setWrapText(true);
            fixBox.getChildren().addAll(fixTitle, fixText);
            body.getChildren().add(fixBox);
        }

        card.getChildren().addAll(header, body);
        return card;
    }

    private void exportReport() {
        if (currentProfile.getLastSession() == null) {
            setStatus("No session to export", true); return;
        }
        ReportBuilder builder = new ReportBuilder(currentProfile.getLastSession());
        new File("reports").mkdirs();
        String path = "reports/" + currentUser.getUsername() + "_report.txt";
        builder.exportToFile(path);
        setStatus("Report exported to: " + path, false);
    }

    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(error
                ? Color.web("#ff6b6b") : Color.web("#6bcb77"));
    }

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        btn.setStyle(
                "-fx-background-color:" + color + ";-fx-text-fill:white;" +
                        "-fx-border-radius:8;-fx-background-radius:8;" +
                        "-fx-font-size:13;-fx-cursor:hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color:derive(" + color + ",20%);" +
                        "-fx-text-fill:white;-fx-border-radius:8;" +
                        "-fx-background-radius:8;-fx-font-size:13;-fx-cursor:hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color:" + color + ";-fx-text-fill:white;" +
                        "-fx-border-radius:8;-fx-background-radius:8;" +
                        "-fx-font-size:13;-fx-cursor:hand;"
        ));
        return btn;
    }

    public Parent getRoot() { return root; }
}
