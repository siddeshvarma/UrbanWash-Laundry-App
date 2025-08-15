package com.urban_wash.view.user_ui;

import com.urban_wash.Controller.FirebaseAuthController;
import com.urban_wash.view.common_methods.LandingPage;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

// NOTE: This class is now a standalone Application for demonstration.
// You will need to integrate the createCenterContent() method back into your baseDashBoard.
public class LoginPage extends Application {

    private final FirebaseAuthController authController = new FirebaseAuthController();

    @Override
    public void start(Stage stage) throws Exception {
        // The base of the scene is a StackPane to hold the background and the login card
        StackPane root = new StackPane();
        
        // --- 1. Animated Background ---
        createAnimatedBackground(root);

        // --- 2. Main Content (The Login Card) ---
        Node loginCard = createCenterContent(stage);
        root.getChildren().add(loginCard);
        
        // --- Animate the login card's entrance ---
        playIntroAnimation(loginCard);

        // --- Scene Setup ---
                javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        stage.setTitle("User Login - UrbanWash");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    // This method can be integrated into your baseDashBoard
    public Node createCenterContent(Stage stage) {
        // --- Main Login Card with "Glassmorphism" effect ---
        VBox loginBox = new VBox(15);
        loginBox.setMaxSize(420, 550);
        loginBox.setAlignment(Pos.CENTER_LEFT);
        loginBox.setPadding(new Insets(40, 50, 40, 50));
        loginBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.15);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);"
        );

        // --- Back Button ---
        Button backButton = new Button("← Back to Home");
        backButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        backButton.setCursor(Cursor.HAND);
        backButton.setTextFill(Color.web("#E5E7EB"));
        backButton.setStyle("-fx-background-color: transparent;");
        backButton.setOnMouseEntered(e -> backButton.setTextFill(Color.WHITE));
        backButton.setOnMouseExited(e -> backButton.setTextFill(Color.web("#E5E7EB")));
        backButton.setOnAction(e -> {
            try {
                new LandingPage().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setPadding(new Insets(0, 0, 20, -10)); // Negative margin to align

        // --- Form Content ---
        Label welcomeLabel = new Label("Sign In");
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        welcomeLabel.setTextFill(Color.WHITE);

        Label instruction = new Label("Welcome back! Please enter your details.");
        instruction.setFont(Font.font("System", 14));
        instruction.setTextFill(Color.web("#D1D5DB"));

        // Email Field
        TextField emailField = new TextField();
        emailField.setPromptText("you@example.com");
        Node styledEmailField = createStyledTextField(emailField, "M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75");

        // Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        Node styledPasswordField = createStyledTextField(passwordField, "M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z");
        
        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        forgotPassword.setTextFill(Color.web("#C7D2FE"));
        forgotPassword.setOnMouseEntered(e -> forgotPassword.setUnderline(true));
        forgotPassword.setOnMouseExited(e -> forgotPassword.setUnderline(false));
        HBox forgotPasswordBox = new HBox(forgotPassword);
        forgotPasswordBox.setAlignment(Pos.CENTER_RIGHT);

        // Status Label
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        statusLabel.setVisible(false);

        // Sign In Button
        Button signInButton = new Button("Sign In");
        signInButton.setMaxWidth(Double.MAX_VALUE);
        stylePrimaryButton(signInButton);

        // Sign In Logic (Functionality preserved)
        signInButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                showStatus(statusLabel, "Email and password cannot be empty.", false);
                return;
            }
            showStatus(statusLabel, "Signing in...", true);
            signInButton.setDisable(true);
            new Thread(() -> {
                String loginResult = authController.signInWithEmailAndPassword(email, password);
                Platform.runLater(() -> {
                    signInButton.setDisable(false);
                    if (loginResult.equals("Success")) {
                        showStatus(statusLabel, "Login Successful! Redirecting...", true);
                        try {
                            new userdashboard().start(stage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        showStatus(statusLabel, loginResult, false);
                    }
                });
            }).start();
        });

        // Register Prompt
        Label registerPrompt = new Label("Don't have an account?");
        registerPrompt.setFont(Font.font("System", 13));
        registerPrompt.setTextFill(Color.web("#D1D5DB"));
        Hyperlink registerLink = new Hyperlink("Sign Up");
        registerLink.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        registerLink.setTextFill(Color.web("#C7D2FE"));
        registerLink.setOnMouseClicked(e -> {
            try { new SignupPage().start(stage); } catch (Exception ex) { ex.printStackTrace(); }
        });
        HBox registerBox = new HBox(5, registerPrompt, registerLink);
        registerBox.setAlignment(Pos.CENTER);
        registerBox.setPadding(new Insets(10, 0, 0, 0));

        // Assemble Card
        loginBox.getChildren().addAll(
            backButtonContainer,
            welcomeLabel, instruction,
            new VBox(5, label("Email"), styledEmailField),
            new VBox(5, label("Password"), styledPasswordField),
            forgotPasswordBox,
            statusLabel, signInButton, new Separator(), registerBox
        );
        return loginBox;
    }

    private void createAnimatedBackground(StackPane pane) {
        List<Color> colors = Arrays.asList(
            Color.web("#6D5BBA"), Color.web("#8058B3"),
            Color.web("#9954A9"), Color.web("#4F46E5"), Color.web("#5271C4")
        );
        ObjectProperty<Color> color1 = new SimpleObjectProperty<>(colors.get(0));
        ObjectProperty<Color> color2 = new SimpleObjectProperty<>(colors.get(1));
        pane.backgroundProperty().bind(new SimpleObjectProperty<>(new Background(new BackgroundFill(
            new LinearGradient(0, 1, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, color1.get()), new Stop(1, color2.get())),
            CornerRadii.EMPTY, Insets.EMPTY
        ))));
        Timeline timeline = new Timeline();
        for (int i = 0; i < colors.size(); i++) {
            KeyValue kv1 = new KeyValue(color1, colors.get(i));
            KeyValue kv2 = new KeyValue(color2, colors.get((i + 1) % colors.size()));
            KeyFrame kf = new KeyFrame(Duration.seconds(7 * (i + 1)), kv1, kv2);
            timeline.getKeyFrames().add(kf);
        }
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private Node createStyledTextField(TextField field, String svgPath) {
        field.setFont(Font.font("System", 14));
        String baseStyle = "-fx-background-color: rgba(0, 0, 0, 0.2); -fx-border-color: rgba(255, 255, 255, 0.3); -fx-text-fill: white; -fx-prompt-text-fill: #D1D5DB;";
        String commonStyle = "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 10 10 40;";
        field.setStyle(baseStyle + commonStyle);

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            String focusedStyle = "-fx-background-color: rgba(0, 0, 0, 0.3); -fx-border-color: #A5B4FC; -fx-text-fill: white; -fx-prompt-text-fill: #D1D5DB;";
            field.setStyle((newVal ? focusedStyle : baseStyle) + commonStyle);
        });

        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web("#9CA3AF"));
        icon.setScaleX(0.9);
        icon.setScaleY(0.9);

        StackPane fieldPane = new StackPane(field, icon);
        fieldPane.setAlignment(Pos.CENTER_LEFT);
        StackPane.setMargin(icon, new Insets(0, 0, 0, 12));
        return fieldPane;
    }

    private void stylePrimaryButton(Button button) {
        button.setFont(Font.font("System", FontWeight.BOLD, 15));
        button.setCursor(Cursor.HAND);
        button.setTextFill(Color.web("#4338CA"));
        button.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 12 0;");
        
        DropShadow shadow = new DropShadow(10, Color.rgb(0,0,0, 0.2));
        button.setEffect(shadow);

        ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
        button.setOnMouseEntered(e -> {
            st.setToX(1.03);
            st.setToY(1.03);
            st.playFromStart();
        });
        button.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });
    }

    private void showStatus(Label label, String message, boolean isSuccess) {
        label.setText(message);
        label.setTextFill(isSuccess ? Color.web("#A7F3D0") : Color.web("#FCA5A5"));
        label.setVisible(true);
    }

    private Label label(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#E5E7EB"));
        return lbl;
    }
    
    private void playIntroAnimation(Node node) {
        node.setOpacity(0);
        node.setTranslateY(50);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(800), node);
        tt.setToY(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(800), node);
        ft.setToValue(1);
        
        ParallelTransition pt = new ParallelTransition(node, tt, ft);
        pt.setDelay(Duration.millis(200));
        pt.play();
    }
}
