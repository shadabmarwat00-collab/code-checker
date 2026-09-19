package com.example.projectoop.ui;

import com.example.projectoop.models.User;
import com.example.projectoop.services.UserManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AuthScreen {

    private StackPane root;
    private Stage     primaryStage;
    private UserManager userManager;

    // Login fields
    private TextField    loginUsernameField;
    private PasswordField loginPasswordField;
    private Label        loginMessageLabel;

    // Register fields
    private TextField    regFullNameField;
    private TextField    regUsernameField;
    private PasswordField regPasswordField;
    private PasswordField regConfirmField;
    private Label        regMessageLabel;

    // Panels
    private VBox loginCard;
    private VBox registerCard;

    public AuthScreen(Stage primaryStage, UserManager userManager) {
        this.primaryStage = primaryStage;
        this.userManager  = userManager;
        buildUI();
    }

    private void buildUI() {
        root = new StackPane();
        root.setPrefSize(1100, 700);

        // Background gradient
        Rectangle bg = new Rectangle(1100, 700);
        bg.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#0d0d1a")),
            new Stop(0.5, Color.web("#111122")),
            new Stop(1, Color.web("#0a0a18"))
        ));

        // Decorative circles in background
        StackPane decorLayer = new StackPane();
        decorLayer.setMouseTransparent(true);
        for (int i = 0; i < 3; i++) {
            Rectangle circle = new Rectangle(200 + i * 80, 200 + i * 80);
            circle.setArcWidth(200 + i * 80);
            circle.setArcHeight(200 + i * 80);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.web("#1e3a6e", 0.15 - i * 0.04));
            circle.setStrokeWidth(1.5);
            decorLayer.getChildren().add(circle);
        }

        // Main content
        HBox content = new HBox(60);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        // Left branding panel
        VBox brandPanel = buildBrandPanel();

        // Cards container
        StackPane cardsContainer = new StackPane();
        loginCard    = buildLoginCard();
        registerCard = buildRegisterCard();
        registerCard.setVisible(false);
        registerCard.setOpacity(0);
        cardsContainer.getChildren().addAll(registerCard, loginCard);

        content.getChildren().addAll(brandPanel, cardsContainer);
        root.getChildren().addAll(bg, decorLayer, content);

        // Fade in on load
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // ── Brand Panel ───────────────────────────────────────────────────────
    private VBox buildBrandPanel() {
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPrefWidth(380);

        Label icon = new Label("⬡");
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        icon.setTextFill(Color.web("#7c9ef8"));

        Label title = new Label("Code Quality\nFingerprinting\nTool");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.WHITE);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setLineSpacing(4);

        Label subtitle = new Label(
            "Analyze your code. Track your habits.\nBecome a better developer.");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#8888aa"));
        subtitle.setLineSpacing(4);

        // Feature bullets
        VBox features = new VBox(12);
        features.setPadding(new Insets(20, 0, 0, 0));
        String[] feats = {
            "⚡  Real-time code analysis",
            "🔍  Personal habit fingerprinting",
            "📈  Track your improvement over time",
            "💾  Secure private profiles"
        };
        for (String f : feats) {
            Label fl = new Label(f);
            fl.setFont(Font.font("Arial", 13));
            fl.setTextFill(Color.web("#6688cc"));
        features.getChildren().add(fl);
        }

        panel.getChildren().addAll(icon, title, subtitle, features);
        return panel;
    }

    // ── Login Card ────────────────────────────────────────────────────────
    private VBox buildLoginCard() {
        VBox card = new VBox(18);
        card.setPrefWidth(380);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: #13131f;" +
            "-fx-border-color: #2a2a4a;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;"
        );

        Label heading = new Label("Welcome back");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        heading.setTextFill(Color.WHITE);

        Label subheading = new Label("Sign in to your developer account");
        subheading.setFont(Font.font("Arial", 13));
        subheading.setTextFill(Color.web("#6666aa"));

        loginUsernameField = authField("Username");
        loginPasswordField = new PasswordField();
        stylePasswordField(loginPasswordField, "Password");

        loginMessageLabel = messageLabel();

        Button loginBtn = primaryButton("Sign In");
        loginBtn.setOnAction(e -> handleLogin());

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2a2a4a;");

        HBox switchBox = new HBox(8);
        switchBox.setAlignment(Pos.CENTER);
        Label switchLabel = new Label("Don't have an account?");
        switchLabel.setTextFill(Color.web("#6666aa"));
        switchLabel.setFont(Font.font("Arial", 12));
        Button switchBtn = linkButton("Create one");
        switchBtn.setOnAction(e -> switchToRegister());
        switchBox.getChildren().addAll(switchLabel, switchBtn);

        card.getChildren().addAll(
            heading, subheading,
            fieldLabel("Username"), loginUsernameField,
            fieldLabel("Password"), loginPasswordField,
            loginMessageLabel, loginBtn, sep, switchBox
        );
        return card;
    }

    // ── Register Card ─────────────────────────────────────────────────────
    private VBox buildRegisterCard() {
        VBox card = new VBox(14);
        card.setPrefWidth(380);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: #13131f;" +
            "-fx-border-color: #2a2a4a;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;"
        );

        Label heading = new Label("Create Account");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        heading.setTextFill(Color.WHITE);

        Label subheading = new Label("Start tracking your code quality today");
        subheading.setFont(Font.font("Arial", 13));
        subheading.setTextFill(Color.web("#6666aa"));

        regFullNameField   = authField("e.g. Ali Hassan");
        regUsernameField   = authField("e.g. ali123");
        regPasswordField   = new PasswordField();
        regConfirmField    = new PasswordField();
        stylePasswordField(regPasswordField, "Min 6 characters");
        stylePasswordField(regConfirmField,  "Repeat password");

        regMessageLabel = messageLabel();

        Button registerBtn = primaryButton("Create Account");
        registerBtn.setOnAction(e -> handleRegister());

        HBox switchBox = new HBox(8);
        switchBox.setAlignment(Pos.CENTER);
        Label switchLabel = new Label("Already have an account?");
        switchLabel.setTextFill(Color.web("#6666aa"));
        switchLabel.setFont(Font.font("Arial", 12));
        Button switchBtn = linkButton("Sign in");
        switchBtn.setOnAction(e -> switchToLogin());
        switchBox.getChildren().addAll(switchLabel, switchBtn);

        card.getChildren().addAll(
            heading, subheading,
            fieldLabel("Full Name"),    regFullNameField,
            fieldLabel("Username"),     regUsernameField,
            fieldLabel("Password"),     regPasswordField,
            fieldLabel("Confirm Password"), regConfirmField,
            regMessageLabel, registerBtn, switchBox
        );
        return card;
    }

    // ── Handlers ──────────────────────────────────────────────────────────
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage(loginMessageLabel, "Please fill in all fields", true);
            return;
        }

        User user = userManager.login(username, password);
        if (user == null) {
            showMessage(loginMessageLabel,
                "Invalid username or password", true);
            shake(loginCard);
            return;
        }

        // Success — go to dashboard
        showMessage(loginMessageLabel, "Welcome back, " + user.getFullName() + "!", false);
        MainDashboard dashboard = new MainDashboard(primaryStage, user, userManager);
        primaryStage.getScene().setRoot(dashboard.getRoot());
    }

    private void handleRegister() {
        String fullName  = regFullNameField.getText().trim();
        String username  = regUsernameField.getText().trim();
        String password  = regPasswordField.getText();
        String confirm   = regConfirmField.getText();

        if (fullName.isEmpty() || username.isEmpty()
                || password.isEmpty() || confirm.isEmpty()) {
            showMessage(regMessageLabel, "Please fill in all fields", true);
            return;
        }
        if (username.length() < 3) {
            showMessage(regMessageLabel,
                "Username must be at least 3 characters", true);
            return;
        }
        if (password.length() < 6) {
            showMessage(regMessageLabel,
                "Password must be at least 6 characters", true);
            return;
        }
        if (!password.equals(confirm)) {
            showMessage(regMessageLabel, "Passwords do not match", true);
            shake(registerCard);
            return;
        }
        if (userManager.usernameExists(username)) {
            showMessage(regMessageLabel,
                "Username already taken — choose another", true);
            return;
        }

        boolean success = userManager.register(username, fullName, password);
        if (success) {
            showMessage(regMessageLabel,
                "Account created! Signing you in...", false);
            User user = userManager.login(username, password);
            MainDashboard dashboard = new MainDashboard(primaryStage, user, userManager);
            primaryStage.getScene().setRoot(dashboard.getRoot());
        } else {
            showMessage(regMessageLabel, "Registration failed — try again", true);
        }
    }

    // ── Transitions ───────────────────────────────────────────────────────
    private void switchToRegister() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), loginCard);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            loginCard.setVisible(false);
            registerCard.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(
                Duration.millis(250), registerCard);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void switchToLogin() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), registerCard);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            registerCard.setVisible(false);
            loginCard.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(
                Duration.millis(250), loginCard);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void shake(VBox card) {
        TranslateTransition shake = new TranslateTransition(
            Duration.millis(60), card);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    // ── UI Helpers ────────────────────────────────────────────────────────
    private TextField authField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #444466;" +
            "-fx-border-color: #2a2a4a;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 14;" +
            "-fx-font-size: 13;"
        );
        tf.setPrefHeight(42);
        return tf;
    }

    private void stylePasswordField(PasswordField pf, String prompt) {
        pf.setPromptText(prompt);
        pf.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #444466;" +
            "-fx-border-color: #2a2a4a;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 14;" +
            "-fx-font-size: 13;"
        );
        pf.setPrefHeight(42);
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.web("#8888aa"));
        VBox.setMargin(l, new Insets(4, 0, -8, 0));
        return l;
    }

    private Label messageLabel() {
        Label l = new Label("");
        l.setFont(Font.font("Arial", 12));
        l.setWrapText(true);
        l.setMinHeight(16);
        return l;
    }

    private void showMessage(Label label, String msg, boolean isError) {
        label.setText(msg);
        label.setTextFill(isError ? Color.web("#ff6b6b") : Color.web("#6bcb77"));
    }

    private Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(300);
        btn.setPrefHeight(44);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setStyle(
            "-fx-background-color: #3a5ac0;" +
            "-fx-text-fill: white;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #4a6ad0;" +
            "-fx-text-fill: white;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: #3a5ac0;" +
            "-fx-text-fill: white;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    private Button linkButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #7c9ef8;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 12;" +
            "-fx-padding: 0;"
        );
        return btn;
    }

    public Parent getRoot() { return root; }
}
