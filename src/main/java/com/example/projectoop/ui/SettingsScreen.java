package com.example.projectoop.ui;

import com.example.projectoop.models.User;
import com.example.projectoop.services.ProfileManager;
import com.example.projectoop.services.UserManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SettingsScreen {

    private ScrollPane root;
    private User currentUser;
    private UserManager userManager;
    private ProfileManager profileManager;
    private Stage primaryStage;

    // Change name fields
    private TextField newNameField;
    private Label     nameMsg;

    // Change password fields
    private PasswordField currentPassField;
    private PasswordField newPassField;
    private PasswordField confirmPassField;
    private Label         passMsg;

    // Delete account
    private PasswordField deletePassField;
    private Label         deleteMsg;

    public SettingsScreen(User currentUser, UserManager userManager,
                          ProfileManager profileManager, Stage primaryStage) {
        this.currentUser    = currentUser;
        this.userManager    = userManager;
        this.profileManager = profileManager;
        this.primaryStage   = primaryStage;
        buildUI();
    }

    private void buildUI() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(32, 40, 40, 40));
        content.setStyle("-fx-background-color:#111122;");

        Label title = new Label("⚙ Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        // Account info card
        content.getChildren().addAll(
                title,
                buildAccountInfoCard(),
                buildChangeNameCard(),
                buildChangePasswordCard(),
                buildDangerZoneCard()
        );

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:#111122;-fx-background:#111122;");
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    // ── Account Info ──────────────────────────────────────────────────────
    private VBox buildAccountInfoCard() {
        VBox card = settingsCard("👤  Account Information");

        HBox row1 = infoRow("Full Name",   currentUser.getFullName());
        HBox row2 = infoRow("Username",    "@" + currentUser.getUsername());
        HBox row3 = infoRow("Member Since", currentUser.getRegisteredDate());
        HBox row4 = infoRow("Total Analyses",
                String.valueOf(currentUser.getTotalAnalyses()));

        card.getChildren().addAll(row1, row2, row3, row4);
        return card;
    }

    // ── Change Name ───────────────────────────────────────────────────────
    private VBox buildChangeNameCard() {
        VBox card = settingsCard("✏️  Change Display Name");

        Label current = new Label("Current name: " + currentUser.getFullName());
        current.setFont(Font.font("Arial", 12));
        current.setTextFill(Color.web("#555577"));

        newNameField = styledField("Enter new display name");

        nameMsg = msgLabel();

        Button saveBtn = primaryBtn("Save Name");
        saveBtn.setOnAction(e -> handleChangeName());

        card.getChildren().addAll(current, fieldLabel("New Name"),
                newNameField, nameMsg, saveBtn);
        return card;
    }

    // ── Change Password ───────────────────────────────────────────────────
    private VBox buildChangePasswordCard() {
        VBox card = settingsCard("🔒  Change Password");

        currentPassField = styledPass("Current password");
        newPassField     = styledPass("New password (min 6 chars)");
        confirmPassField = styledPass("Confirm new password");
        passMsg          = msgLabel();

        Button saveBtn = primaryBtn("Update Password");
        saveBtn.setOnAction(e -> handleChangePassword());

        card.getChildren().addAll(
                fieldLabel("Current Password"), currentPassField,
                fieldLabel("New Password"),     newPassField,
                fieldLabel("Confirm Password"), confirmPassField,
                passMsg, saveBtn
        );
        return card;
    }

    // ── Danger Zone ───────────────────────────────────────────────────────
    private VBox buildDangerZoneCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color:#1a0a0a;" +
                        "-fx-border-color:#4a1a1a;" +
                        "-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;"
        );

        Label header = new Label("⚠  Danger Zone");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        header.setTextFill(Color.web("#ff6b6b"));

        Label warning = new Label(
                "Deleting your account will permanently remove all your sessions, "
                        + "profile data, and achievements. This cannot be undone.");
        warning.setFont(Font.font("Arial", 12));
        warning.setTextFill(Color.web("#885555"));
        warning.setWrapText(true);

        deletePassField = styledPass("Enter your password to confirm");
        deleteMsg       = msgLabel();

        Button deleteBtn = new Button("🗑  Delete My Account");
        deleteBtn.setPrefHeight(40);
        deleteBtn.setStyle(
                "-fx-background-color:#5a1a1a;-fx-text-fill:#ff6b6b;" +
                        "-fx-border-radius:8;-fx-background-radius:8;" +
                        "-fx-font-size:13;-fx-cursor:hand;"
        );
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(
                "-fx-background-color:#7a2a2a;-fx-text-fill:#ff6b6b;" +
                        "-fx-border-radius:8;-fx-background-radius:8;" +
                        "-fx-font-size:13;-fx-cursor:hand;"
        ));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(
                "-fx-background-color:#5a1a1a;-fx-text-fill:#ff6b6b;" +
                        "-fx-border-radius:8;-fx-background-radius:8;" +
                        "-fx-font-size:13;-fx-cursor:hand;"
        ));
        deleteBtn.setOnAction(e -> handleDeleteAccount());

        card.getChildren().addAll(header, warning,
                fieldLabel("Password Confirmation"),
                deletePassField, deleteMsg, deleteBtn);
        return card;
    }

    // ── Handlers ──────────────────────────────────────────────────────────
    private void handleChangeName() {
        String newName = newNameField.getText().trim();
        if (newName.isEmpty()) {
            showMsg(nameMsg, "Please enter a new name", true); return;
        }
        if (newName.length() < 2) {
            showMsg(nameMsg, "Name must be at least 2 characters", true); return;
        }
        // Update and save user
        User updatedUser = new User(
                currentUser.getUsername(), newName,
                currentUser.getHashedPassword());
        userManager.saveUser(updatedUser);
        currentUser = updatedUser;
        showMsg(nameMsg, "Name updated successfully!", false);
        newNameField.clear();
    }

    private void handleChangePassword() {
        String current = currentPassField.getText();
        String newPass  = newPassField.getText();
        String confirm  = confirmPassField.getText();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showMsg(passMsg, "Please fill in all fields", true); return;
        }
        // Verify current password by attempting login
        User check = userManager.login(currentUser.getUsername(), current);
        if (check == null) {
            showMsg(passMsg, "Current password is incorrect", true); return;
        }
        if (newPass.length() < 6) {
            showMsg(passMsg, "New password must be at least 6 characters", true); return;
        }
        if (!newPass.equals(confirm)) {
            showMsg(passMsg, "New passwords do not match", true); return;
        }

        // Re-register with new password by using internal save
        boolean success = userManager.changePassword(
                currentUser.getUsername(), newPass);
        if (success) {
            showMsg(passMsg, "Password changed successfully!", false);
            currentPassField.clear();
            newPassField.clear();
            confirmPassField.clear();
        } else {
            showMsg(passMsg, "Failed to update password", true);
        }
    }

    private void handleDeleteAccount() {
        String pass = deletePassField.getText();
        if (pass.isEmpty()) {
            showMsg(deleteMsg, "Please enter your password", true); return;
        }
        User check = userManager.login(currentUser.getUsername(), pass);
        if (check == null) {
            showMsg(deleteMsg, "Incorrect password", true); return;
        }

        // Delete profile file
        try {
            java.io.File profileFile = new java.io.File(
                    "profiles/" + currentUser.getUsername() + ".profile");
            profileFile.delete();
        } catch (Exception ignored) {}

        // Delete user account
        userManager.deleteUser(currentUser.getUsername());

        // Go back to login screen
        AuthScreen auth = new AuthScreen(primaryStage,  userManager);
        primaryStage.getScene().setRoot(auth.getRoot());
    }

    // ── UI Helpers ────────────────────────────────────────────────────────
    private VBox settingsCard(String headerText) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color:#1a1a2e;-fx-border-color:#2a2a4a;" +
                        "-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;"
        );
        Label header = new Label(headerText);
        header.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        header.setTextFill(Color.web("#7c9ef8"));
        card.getChildren().add(header);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a2a4a;");
        card.getChildren().add(sep);
        return card;
    }

    private HBox infoRow(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));

        Label l = new Label(label + ":");
        l.setFont(Font.font("Arial", 12));
        l.setTextFill(Color.web("#555577"));
        l.setPrefWidth(130);

        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        v.setTextFill(Color.WHITE);

        row.getChildren().addAll(l, v);
        return row;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(400);
        tf.setStyle(
                "-fx-background-color:#111133;-fx-text-fill:white;" +
                        "-fx-prompt-text-fill:#333355;-fx-border-color:#2a2a4a;" +
                        "-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;" +
                        "-fx-font-size:13;"
        );
        tf.setPrefHeight(42);
        return tf;
    }

    private PasswordField styledPass(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setMaxWidth(400);
        pf.setStyle(
                "-fx-background-color:#111133;-fx-text-fill:white;" +
                        "-fx-prompt-text-fill:#333355;-fx-border-color:#2a2a4a;" +
                        "-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;" +
                        "-fx-font-size:13;"
        );
        pf.setPrefHeight(42);
        return pf;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.web("#555577"));
        VBox.setMargin(l, new Insets(4, 0, -6, 0));
        return l;
    }

    private Label msgLabel() {
        Label l = new Label("");
        l.setFont(Font.font("Arial", 12));
        l.setWrapText(true);
        l.setMinHeight(16);
        return l;
    }

    private void showMsg(Label label, String msg, boolean error) {
        label.setText(msg);
        label.setTextFill(error ? Color.web("#ff6b6b") : Color.web("#6bcb77"));
    }

    private Button primaryBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        btn.setMaxWidth(400);
        btn.setStyle(
                "-fx-background-color:#3a5ac0;-fx-text-fill:white;" +
                        "-fx-border-radius:8;-fx-background-radius:8;" +
                        "-fx-font-size:13;-fx-cursor:hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color:#4a6ad0;-fx-text-fill:white;" +
                        "-fx-border-radius:8;-fx-background-radius:8;" +
                        "-fx-font-size:13;-fx-cursor:hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color:#3a5ac0;-fx-text-fill:white;" +
                        "-fx-border-radius:8;-fx-background-radius:8;" +
                        "-fx-font-size:13;-fx-cursor:hand;"
        ));
        return btn;
    }

    public Parent getRoot() { return root; }
}
