package com.urban_wash.view.user_ui;

import com.urban_wash.Controller.FirebaseAuthController;
import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Model.User;
import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate; // ADDED IMPORT

public class SignupPage extends baseDashBoard {

    private final FirebaseAuthController authController = new FirebaseAuthController();
    private final FirestoreService firestoreService = new FirestoreService();

    @Override
    protected Node createCenterContent() {
        StackPane centerRoot = new StackPane();
        centerRoot.setStyle("-fx-background-color: #F9FAFB;");
        centerRoot.setPadding(new Insets(20));

        VBox card = new VBox(18);
        card.setPadding(new Insets(40));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #E5E7EB;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 15, 0, 0, 4);"
        );
        card.setAlignment(Pos.CENTER);

        ImageView logoView = new ImageView(new Image("https://imgs.search.brave.com/I8hj3DM5psHH-i1mcn2nYjg1F8VYffWDNwa7sRYPKN4/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9kYWZn/cjF5M2gzdmx3LmNs/b3VkZnJvbnQubmV0/L2ltYWdlcy9sb2dv/cy8zNjMwMTY3NDg2/MzAxMC5qcGc"));
        logoView.setFitWidth(60);
        logoView.setFitHeight(60);
        logoView.setPreserveRatio(true);

        Label heading = new Label("Create Your UrbanWash Account");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        heading.setTextFill(Color.web("#1f2937"));

        Label subheading = new Label("Sign up to get started with convenient and reliable laundry services.");
        subheading.setFont(Font.font("Arial", 14));
        subheading.setTextFill(Color.web("#6b7280"));
        subheading.setWrapText(true);
        subheading.setMaxWidth(380);
        subheading.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // --- Form Fields (UI Unchanged) ---
        TextField firstNameField = createStyledTextField("Siddesh");
        TextField lastNameField = createStyledTextField("Varma");
        HBox nameRow = new HBox(15, createLabeledControl("First Name", firstNameField), createLabeledControl("Last Name", lastNameField));

        TextField phoneField = createStyledTextField("8421426117");
        VBox phoneBox = createLabeledControl("Phone Number", phoneField);

        TextField emailField = createStyledTextField("siddeshvarma2005@gmail.com");
        VBox emailBox = createLabeledControl("Email Address", emailField);

        PasswordField passwordField = createStyledPasswordField("••••••••");
        VBox passwordBox = createLabeledControl("Password", passwordField);

        TextField cityField = createStyledTextField("Pune");
        TextField stateField = createStyledTextField("Maharashtra");
        HBox cityStateRow = new HBox(15, createLabeledControl("City", cityField), createLabeledControl("State", stateField));

        TextField countryField = createStyledTextField("India");
        TextField zipCodeField = createStyledTextField("411041");
        HBox countryZipRow = new HBox(15, createLabeledControl("Country", countryField), createLabeledControl("Zip Code", zipCodeField));

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        statusLabel.setVisible(false);

        Button createAccountButton = new Button("Create Account");
        stylePrimaryButton(createAccountButton);

        // --- RECTIFIED AND COMPLETE SIGN-UP LOGIC (REPLACED AS REQUESTED) ---
        createAccountButton.setOnAction(e -> {
            // 1. Get all values from the form fields
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String country = countryField.getText().trim();
            String zipCode = zipCodeField.getText().trim();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                showStatus(statusLabel, "All name, email, and password fields are required.", false);
                return;
            }

            showStatus(statusLabel, "Creating account...", true);

            // 2. Perform network operations on a background thread
            new Thread(() -> {
                // This now correctly returns the UID or an error message
                String signUpResult = authController.signUpWithEmailAndPassword(email, password);

                // 3. Check if signup was successful (result is a UID, not an error)
                if (signUpResult != null && !signUpResult.startsWith("Error:")) {
                    User newUser = new User();
                    newUser.setUid(signUpResult); // The result is now the correct UID
                    
                    // ✅ CORRECTED: Set all the fields for the new user model
                    newUser.setName(firstName + " " + lastName);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    newUser.setEmail(email);
                    newUser.setPhone(phone);
                    newUser.setCity(city);
                    newUser.setState(state);
                    newUser.setCountry(country);
                    newUser.setZipCode(zipCode);
                    newUser.setAddress(city + ", " + state); // Construct a simple location string
                    newUser.setDateJoined(LocalDate.now().toString()); // Set current date
                    newUser.setSubscriptionStatus("Active"); // Set a default status

                    // 4. Save the complete user profile to Firestore
                    String createProfileResult = firestoreService.createUserProfile(newUser);
                    
                    // 5. Update UI based on the result of saving the profile
                    if (createProfileResult.startsWith("Error:")) {
                         Platform.runLater(() -> showStatus(statusLabel, "DB Error: " + createProfileResult, false));
                    } else {
                         Platform.runLater(() -> showStatus(statusLabel, "Account created successfully! Please log in.", true));
                    }

                } else {
                    // 6. Show the error message from Firebase if signup failed
                    Platform.runLater(() -> showStatus(statusLabel, signUpResult, false));
                }
            }).start();
        });

        Label loginPrefix = new Label("Already have an account? ");
        Label loginLink = new Label("Log in");
        styleLink(loginLink);
        loginLink.setOnMouseClicked(e -> {
             try { new LoginPage().start((Stage) loginLink.getScene().getWindow()); } catch (Exception ex) { ex.printStackTrace(); }
        });
        HBox loginBox = new HBox(5, loginPrefix, loginLink);
        loginBox.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(
            logoView, heading, subheading, new Separator(),
            nameRow, phoneBox, emailBox, passwordBox,
            cityStateRow, countryZipRow,
            statusLabel, createAccountButton, loginBox
        );

        ScrollPane scrollPane = new ScrollPane(card);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setMaxWidth(450);

        centerRoot.getChildren().add(scrollPane);
        
        return centerRoot;
    }

    @Override
    protected VBox createLeftSidebar() {
        return null; // No sidebar on the signup page
    }
    
    // --- Helper and Styling Methods (Unchanged) ---

    private void showStatus(Label label, String message, boolean isSuccess) {
        label.setText(message);
        label.setTextFill(isSuccess ? Color.web("#16A34A") : Color.web("#EF4444"));
        label.setVisible(true);
    }
    
    private VBox createLabeledControl(String labelText, Control control) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        label.setTextFill(Color.web("#374151"));
        VBox box = new VBox(5, label, control);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }
    
    private TextField createStyledTextField(String text) {
        TextField textField = new TextField(text);
        styleTextField(textField);
        return textField;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(prompt);
        styleTextField(passwordField);
        return passwordField;
    }

    private void styleTextField(TextInputControl field) {
        String baseStyle = "-fx-font-size: 14px; -fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-width: 1; -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 10;";
        String focusStyle = "-fx-font-size: 14px; -fx-background-color: white; -fx-border-color: #4f46e5; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 9;";
        field.setStyle(baseStyle);
        field.focusedProperty().addListener((obs, oldVal, newVal) -> field.setStyle(newVal ? focusStyle : baseStyle));
    }

    private void stylePrimaryButton(Button btn) {
        String baseStyle = "-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 18; -fx-background-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #4338CA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 18; -fx-background-radius: 8; -fx-cursor: hand;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
    }

    private void styleLink(Label label) {
        label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        label.setTextFill(Color.web("#4F46E5"));
        label.setCursor(Cursor.HAND);
        label.setOnMouseEntered(e -> label.setUnderline(true));
        label.setOnMouseExited(e -> label.setUnderline(false));
    }
}
