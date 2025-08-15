package com.urban_wash.view.admin_ui;

import com.urban_wash.view.common_methods.LandingPage;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class Login extends Application {

    @Override
    public void start(Stage stage) {
        // --- Root Background ---
        StackPane background = new StackPane();
        background.setAlignment(Pos.CENTER);
        createAnimatedBackground(background);

        // --- Main Login Card ---
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setMaxWidth(900);
        root.setMaxHeight(600);
        root.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 15px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 30, 0, 0, 5);"
        );

        // --- Left Side: Login Form ---
        Node card = createLoginForm(stage);

        // --- Right Side: Testimonial/Branding ---
        VBox testimonialBox = createTestimonialBox();

        // --- Assemble All ---
        root.getChildren().addAll(card, testimonialBox);
        background.getChildren().add(root);

        // --- Scene Setup ---
        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(background, visualBounds.getWidth(), visualBounds.getHeight());
        stage.setTitle("UrbanWash Admin Login");
        stage.setScene(scene);
        stage.show();
    }

    private void createAnimatedBackground(StackPane pane) {
        List<Color> colors = Arrays.asList(
                Color.web("#6D5BBA"), Color.web("#8058B3"),
                Color.web("#9954A9"), Color.web("#4F46E5"), Color.web("#5271C4")
        );

        ObjectProperty<Color> color1 = new SimpleObjectProperty<>(colors.get(0));
        ObjectProperty<Color> color2 = new SimpleObjectProperty<>(colors.get(1));

        pane.backgroundProperty().addListener((obs, old, newv) -> {
            pane.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 1, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, color1.get()), new Stop(1, color2.get())),
                CornerRadii.EMPTY, Insets.EMPTY
            )));
        });

        Timeline timeline = new Timeline();
        for (int i = 0; i < colors.size(); i++) {
            KeyValue kv1 = new KeyValue(color1, colors.get(i));
            KeyValue kv2 = new KeyValue(color2, colors.get((i + 1) % colors.size()));
            KeyFrame kf = new KeyFrame(Duration.seconds(5 * (i + 1)), kv1, kv2);
            timeline.getKeyFrames().add(kf);
        }

        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private Node createLoginForm(Stage stage) {
        VBox contentPane = new VBox(20);
        contentPane.setPadding(new Insets(40, 50, 40, 50));
        contentPane.setAlignment(Pos.CENTER_LEFT);
        contentPane.setPrefWidth(450);

        Label title = new Label("Admin Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#111827"));

        Label subtitle = new Label("Access your laundry management dashboard.");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#6b7280"));

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your Admin Email");
        Node styledEmailField = createStyledTextField(emailField, "M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75");
        VBox emailBox = new VBox(5, createLabel("Admin Email"), styledEmailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        Node styledPasswordField = createStyledTextField(passwordField, "M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z");
        VBox passwordBox = new VBox(5, createLabel("Password"), styledPasswordField);

        Button signInButton = new Button("Login");
        signInButton.setMaxWidth(Double.MAX_VALUE);
        stylePrimaryButton(signInButton);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        statusLabel.setTextFill(Color.RED);
        statusLabel.setVisible(false);

        signInButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            if ("UrbanWash@gmail.com".equals(email) && "UrbanWash".equals(password)) {
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("Login Successful! Redirecting...");
                statusLabel.setVisible(true);
                try {
                    new PendingRegistrationsView().start(stage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Invalid credentials. Please try again.");
                statusLabel.setVisible(true);
            }
        });

        contentPane.getChildren().addAll(title, subtitle, emailBox, passwordBox, statusLabel, signInButton);

        Button backButton = new Button();
        SVGPath backIcon = new SVGPath();
        backIcon.setContent("M10 19l-7-7m0 0l7-7m-7 7h18");
        backIcon.setStroke(Color.web("#6B7280"));
        backIcon.setStrokeWidth(2);
        backButton.setGraphic(backIcon);
        backButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 50;");
        backButton.setCursor(Cursor.HAND);
        backButton.setOnAction(e -> {
            try {
                new LandingPage().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 50;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 50;"));

        StackPane cardPane = new StackPane();
        cardPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15px 0 0 15px;");
        cardPane.getChildren().addAll(contentPane, backButton);
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(15, 0, 0, 15));

        return cardPane;
    }

    // --- THIS METHOD IS NOW CORRECTED ---
 private VBox createTestimonialBox() {
    VBox testimonialBox = new VBox(25);
    testimonialBox.setAlignment(Pos.CENTER);
    testimonialBox.setPrefWidth(450);
    testimonialBox.setPadding(new Insets(40, 50, 40, 50));
    testimonialBox.setStyle("-fx-background-color: rgba(249, 250, 251, 0.95); -fx-background-radius: 0 15px 15px 0;");

    // Create a dedicated container for the avatar image or placeholder
    StackPane avatarContainer = new StackPane();
    avatarContainer.setPrefSize(100, 100);

    try {
        // --- MODIFIED CODE: Load the image from project resources ---
        Image testimonialImage = new Image(getClass().getResourceAsStream("/logo2.png"));

        // If the image is null, it means the file was not found.
        if (testimonialImage.isError()) {
            throw new NullPointerException("Cannot find resource /logo2.png");
        }

        ImageView testimonialView = new ImageView(testimonialImage);
        testimonialView.setFitWidth(100);
        testimonialView.setFitHeight(100);

        // Apply circular clip to the ImageView
        Circle clip = new Circle(50, 50, 50);
        testimonialView.setClip(clip);

        // If loading is successful, add the ImageView to the container
        avatarContainer.getChildren().add(testimonialView);

    } catch (Exception e) {
        // If loading fails, create and add the placeholder Node directly to the container
        System.err.println("Warning: Could not load image from resources (/logo2.png). Using a placeholder. Error: " + e.getMessage());
        
        Label placeholderLabel = new Label("UW");
        placeholderLabel.setStyle("-fx-font-size: 32; -fx-text-fill: white; -fx-font-weight: bold;");

        // Style the container itself to be the circular placeholder
        avatarContainer.setStyle("-fx-background-color: #6366F1; -fx-background-radius: 50;");
        avatarContainer.getChildren().add(placeholderLabel);
    }

    // --- The rest of the method is unchanged ---
    Label quote = new Label("\"Our system has revolutionized laundry management, making operations seamless and efficient for businesses of all sizes.\"");
    quote.setFont(Font.font("Arial", 16));
    quote.setTextFill(Color.web("#374151"));
    quote.setWrapText(true);
    quote.setMaxWidth(350);
    quote.setStyle("-fx-font-style: italic; -fx-text-alignment: center;");

    VBox authorBox = new VBox(-2);
    authorBox.setAlignment(Pos.CENTER);
    Label ceoName = new Label("DevDynamos");
    ceoName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    ceoName.setTextFill(Color.web("#111827"));
    Label ceoTitle = new Label("Team, UrbanWash");
    ceoTitle.setFont(Font.font("Arial", 12));
    ceoTitle.setTextFill(Color.web("#6B7280"));
    authorBox.getChildren().addAll(ceoName, ceoTitle);

    testimonialBox.getChildren().addAll(avatarContainer, quote, authorBox);
    return testimonialBox;
}

    private Label createLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#374151"));
        return lbl;
    }

    private Node createStyledTextField(TextField field, String svgPath) {
        field.setFont(Font.font("Arial", 14));
        String unfocusedStyle = "-fx-background-color: #F9FAFB; -fx-border-color: #D1D5DB; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 10 10 40;";
        field.setStyle(unfocusedStyle);

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            String focusedStyle = "-fx-background-color: white; -fx-border-color: #4F46E5; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 10 10 40; -fx-effect: dropshadow(three-pass-box, rgba(79,70,229,0.3), 5, 0, 0, 0);";
            field.setStyle(newVal ? focusedStyle : unfocusedStyle);
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

    private void stylePrimaryButton(Button btn) {
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        btn.setCursor(Cursor.HAND);
        btn.setTextFill(Color.WHITE);
        btn.setStyle("-fx-background-color: linear-gradient(to right, #6366F1, #818CF8); -fx-background-radius: 8; -fx-padding: 12 0;");
        
        DropShadow shadow = new DropShadow(10, Color.rgb(99, 102, 241, 0.3));
        btn.setEffect(shadow);

        Timeline hoverTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                new KeyValue(btn.translateYProperty(), -3),
                new KeyValue(shadow.radiusProperty(), 15),
                new KeyValue(shadow.colorProperty(), Color.rgb(99, 102, 241, 0.4))
        ));
        Timeline exitTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                new KeyValue(btn.translateYProperty(), 0),
                new KeyValue(shadow.radiusProperty(), 10),
                new KeyValue(shadow.colorProperty(), Color.rgb(99, 102, 241, 0.3))
        ));
        btn.setOnMouseEntered(e -> hoverTimeline.playFromStart());
        btn.setOnMouseExited(e -> exitTimeline.playFromStart());
        
        ScaleTransition stPress = new ScaleTransition(Duration.millis(100), btn);
        stPress.setToX(0.98);
        stPress.setToY(0.98);
        ScaleTransition stRelease = new ScaleTransition(Duration.millis(100), btn);
        stRelease.setToX(1.0);
        stRelease.setToY(1.0);
        btn.setOnMousePressed(e -> stPress.playFromStart());
        btn.setOnMouseReleased(e -> stRelease.playFromStart());
    }
}