package com.example.projectoop.ui;

import com.example.projectoop.models.User;
import com.example.projectoop.services.ProfileManager;
import com.example.projectoop.services.UserManager;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainDashboard {

    private BorderPane root;
    private Stage primaryStage;
    private User currentUser;
    private UserManager userManager;
    private ProfileManager profileManager;

    private Button btnHome;
    private Button btnAnalyze;
    private Button btnProfile;
    private Button btnLeaderboard;
    private Button btnAchievements;
    private Button btnSettings;
    private Button activeBtn;

    public MainDashboard(Stage primaryStage, User currentUser,
                         UserManager userManager) {
        this.primaryStage   = primaryStage;
        this.currentUser    = currentUser;
        this.userManager    = userManager;
        this.profileManager = new ProfileManager();
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();
        root.setLeft(buildSidebar());
        showHome();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color:#0d0d1a;");

        // Logo
        VBox logoBox = new VBox(4);
        logoBox.setPadding(new Insets(28, 20, 24, 20));
        logoBox.setStyle("-fx-border-color:#1e1e3a;-fx-border-width:0 0 1 0;");
        Label icon = new Label("⬡");
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        icon.setTextFill(Color.web("#7c9ef8"));
        Label appName = new Label("CQ Fingerprint");
        appName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        appName.setTextFill(Color.WHITE);
        logoBox.getChildren().addAll(icon, appName);

        // User info
        VBox userBox = new VBox(6);
        userBox.setPadding(new Insets(16, 20, 16, 20));
        userBox.setStyle("-fx-border-color:#1e1e3a;-fx-border-width:0 0 1 0;");
        String initials = String.valueOf(
                currentUser.getFullName().charAt(0)).toUpperCase();
        Label avatar = new Label(initials);
        avatar.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        avatar.setTextFill(Color.web("#7c9ef8"));
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(44, 44);
        avatar.setStyle(
                "-fx-background-color:#1a2a4a;-fx-background-radius:22;");
        Label userName = new Label(currentUser.getFullName());
        userName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        userName.setTextFill(Color.WHITE);
        Label userHandle = new Label("@" + currentUser.getUsername());
        userHandle.setFont(Font.font("Arial", 11));
        userHandle.setTextFill(Color.web("#555577"));
        userBox.getChildren().addAll(avatar, userName, userHandle);

        // Nav buttons
        VBox navBox = new VBox(4);
        navBox.setPadding(new Insets(16, 10, 12, 10));

        btnHome         = navButton("🏠", "Home");
        btnAnalyze      = navButton("⚡", "Analyze Code");
        btnProfile      = navButton("👤", "My Profile");
        btnLeaderboard  = navButton("🏆", "Leaderboard");
        btnAchievements = navButton("🎖", "Achievements");
        btnSettings     = navButton("⚙", "Settings");

        btnHome.setOnAction(e         -> { setActive(btnHome);         showHome();         });
        btnAnalyze.setOnAction(e      -> { setActive(btnAnalyze);      showAnalyze();      });
        btnProfile.setOnAction(e      -> { setActive(btnProfile);      showProfile();      });
        btnLeaderboard.setOnAction(e  -> { setActive(btnLeaderboard);  showLeaderboard();  });
        btnAchievements.setOnAction(e -> { setActive(btnAchievements); showAchievements(); });
        btnSettings.setOnAction(e     -> { setActive(btnSettings);     showSettings();     });

        navBox.getChildren().addAll(
                btnHome, btnAnalyze, btnProfile,
                btnLeaderboard, btnAchievements, btnSettings
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout
        Button logoutBtn = new Button("⏻  Logout");
        logoutBtn.setPrefWidth(200);
        String base  = "-fx-background-color:#2a1a1a;-fx-text-fill:#ff6b6b;" +
                "-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13;" +
                "-fx-padding:10 16;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;";
        String hover = "-fx-background-color:#3a1a1a;-fx-text-fill:#ff6b6b;" +
                "-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13;" +
                "-fx-padding:10 16;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;";
        logoutBtn.setStyle(base);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(hover));
        logoutBtn.setOnMouseExited(e  -> logoutBtn.setStyle(base));
        logoutBtn.setOnAction(e -> {
            AuthScreen auth = new AuthScreen(primaryStage, userManager);
            primaryStage.getScene().setRoot(auth.getRoot());
        });

        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(10, 10, 20, 10));
        bottomBox.getChildren().add(logoutBtn);

        sidebar.getChildren().addAll(logoBox, userBox, navBox, spacer, bottomBox);
        setActive(btnHome);
        return sidebar;
    }

    private Button navButton(String icon, String label) {
        Button btn = new Button(icon + "  " + label);
        btn.setPrefWidth(200);
        btn.setStyle(navStyle(false));
        btn.setOnMouseEntered(e -> { if (btn != activeBtn) btn.setStyle(navHover()); });
        btn.setOnMouseExited(e  -> { if (btn != activeBtn) btn.setStyle(navStyle(false)); });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeBtn != null) activeBtn.setStyle(navStyle(false));
        activeBtn = btn;
        btn.setStyle(navStyle(true));
    }

    private String navStyle(boolean active) {
        return "-fx-background-color:" + (active ? "#1a2a4a" : "transparent") + ";" +
                "-fx-text-fill:" + (active ? "#7c9ef8" : "#8888aa") + ";" +
                "-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13;" +
                "-fx-padding:10 16;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;" +
                (active ? "-fx-border-color:#2a3a6a;-fx-border-width:1;" : "");
    }

    private String navHover() {
        return "-fx-background-color:#141428;-fx-text-fill:#aaaacc;" +
                "-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13;" +
                "-fx-padding:10 16;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;";
    }

    private void switchTo(Parent screen) {
        FadeTransition ft = new FadeTransition(Duration.millis(200), screen);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        root.setCenter(screen);
    }

    private void showHome() {
        switchTo(new HomeScreen(currentUser, profileManager).getRoot());
    }
    private void showAnalyze() {
        switchTo(new AnalyzeScreen(currentUser, profileManager, userManager).getRoot());
    }
    private void showProfile() {
        switchTo(new ProfileScreen(currentUser, profileManager).getRoot());
    }
    private void showLeaderboard() {
        switchTo(new LeaderboardScreen(
                currentUser, profileManager, userManager).getRoot());
    }
    private void showAchievements() {
        switchTo(new AchievementsScreen(currentUser, profileManager).getRoot());
    }
    private void showSettings() {
        switchTo(new SettingsScreen(
                currentUser, userManager, profileManager, primaryStage).getRoot());
    }

    public Parent getRoot() { return root; }
}
