package com.urban_wash.view.common_methods;

// Import animation classes
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;

// Assuming these are your project's classes
import com.urban_wash.view.buisness_ui.LaundryLoginPage;
import com.urban_wash.view.user_ui.LoginPage;
import com.urban_wash.view.admin_ui.Login;
import com.urban_wash.view.common_methods.AboutUs; 


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField; // Added for Footer
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

// Imports needed for the integrated footer functionality
import java.awt.Desktop;
import java.net.URI;

public class LandingPage extends Application {

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();

        // --- NEW: Linear Gradient Background ---
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #2563EB, #8B5CF6);");

        // --- Navigation Bar (Header) ---
        HBox navBar = new HBox();
        navBar.setPadding(new Insets(15, 40, 15, 40));
        navBar.setSpacing(25);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-border-color: transparent transparent rgba(255, 255, 255, 0.2) transparent; -fx-border-width: 1;");
        navBar.setPrefHeight(65);

        // --- EDITED: Logo with a blue background ---
        // 1. Load the image data into an Image object
        Image logoImage = new Image(getClass().getResourceAsStream("/logo2.png"));

// 2. Create an ImageView to display the Image
ImageView logoView = new ImageView(logoImage);

// 3. Set the size on the ImageView
logoView.setFitWidth(40);
logoView.setFitHeight(40);


        Label brand = new Label("UrbanWash");
        brand.setFont(Font.font("System", FontWeight.BOLD, 22));
        brand.setTextFill(Color.WHITE);

        Hyperlink aboutUsLink = new Hyperlink("About Us");
        aboutUsLink.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        aboutUsLink.setTextFill(Color.WHITE);
        aboutUsLink.setBorder(Border.EMPTY);
        aboutUsLink.setStyle("-fx-underline: false;");
        aboutUsLink.setOnMouseEntered(e -> aboutUsLink.setStyle("-fx-underline: true; -fx-text-fill: white;"));
        aboutUsLink.setOnMouseExited(e -> aboutUsLink.setStyle("-fx-underline: false; -fx-text-fill: white;"));
        aboutUsLink.setOnAction(e -> {
            try {
                new AboutUs().start(stage); 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        navBar.getChildren().addAll(logoView,brand, spacer, aboutUsLink);

        // --- EDITED: "Glassmorphism" Center Card ---
        VBox card = new VBox(30);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(450);
        // This style creates a semi-transparent "frosted glass" effect
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.15);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);"
        );


        // --- Card Logo ---
        ImageView cardLogoView = new ImageView(logoView.getImage());
        cardLogoView.setFitWidth(50);
        cardLogoView.setFitHeight(50);
    

        Label heading = new Label("Your Laundry, Our Priority.\nFast, Fresh, and Flawless.");
        heading.setFont(Font.font("System", FontWeight.BOLD, 28));
        heading.setWrapText(true);
        heading.setTextAlignment(TextAlignment.CENTER);
        heading.setTextFill(Color.WHITE);
        // Adjusted shadow for better readability on the glass background
        heading.setEffect(new DropShadow(1, 1, 2, Color.rgb(0, 0, 0, 0.6)));

        Label subtext = new Label("Connecting you with the best laundry services in your neighborhood.");
        subtext.setFont(Font.font("System", 16));
        subtext.setWrapText(true);
        subtext.setTextAlignment(TextAlignment.CENTER);
        subtext.setTextFill(Color.web("#E5E7EB"));
        subtext.setEffect(new DropShadow(1, 1, 2, Color.rgb(0, 0, 0, 0.6)));

        // --- Buttons ---
        Button userBtn = createAnimatedButton("I'm a User", "-fx-background-color: white; -fx-text-fill: #2563EB;");
        Button shopBtn = createAnimatedButton("I'm a Laundry Shop Owner", "-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2;");
        Button adminBtn = createAnimatedButton("I'm an Admin", "-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 2; -fx-border-style: dashed;");
        
        // --- Button Actions ---
        userBtn.setOnAction(e -> { try { new LoginPage().start(stage); } catch (Exception ex) { ex.printStackTrace(); } });
        shopBtn.setOnAction(e -> { try { new LaundryLoginPage().start(stage); } catch (Exception ex) { ex.printStackTrace(); } });
        adminBtn.setOnAction(e -> { try { new Login().start(stage); } catch (Exception ex) { ex.printStackTrace(); } });
        
        VBox buttonContainer = new VBox(12, userBtn, shopBtn, adminBtn);
        buttonContainer.setAlignment(Pos.CENTER);
        card.getChildren().addAll(heading, subtext, buttonContainer);

        // --- Feature Panels ---
        VBox leftFeatures = new VBox(50);
        leftFeatures.setAlignment(Pos.CENTER);
        leftFeatures.setPadding(new Insets(0, 0, 0, 20));
        leftFeatures.getChildren().addAll(createFeatureBox("https://img.icons8.com/ios-filled/100/ffffff/delivery-time.png", "24-Hour Turnaround"), createFeatureBox("https://img.icons8.com/material-rounded/50/ffffff/leaf.png", "Eco-Friendly Options"));

        VBox rightFeatures = new VBox(50);
        rightFeatures.setAlignment(Pos.CENTER);
        rightFeatures.setPadding(new Insets(0, 20, 0, 0));
        rightFeatures.getChildren().addAll(createFeatureBox("https://img.icons8.com/ios-filled/100/ffffff/sparkling.png", "Premium Quality Care"), createFeatureBox("https://img.icons8.com/ios-filled/100/ffffff/online-support.png", "Simple Online Booking"));

        HBox centerLayout = new HBox(80);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.getChildren().addAll(leftFeatures, card, rightFeatures);

        // --- Layout Assembly ---
        StackPane contentHolder = new StackPane(centerLayout);
        VBox.setVgrow(contentHolder, Priority.ALWAYS);

        // --- FIXED: Integrated footer is now created by a local method ---
        Node footer = createFooterNode();

        VBox mainVerticalLayout = new VBox(navBar, contentHolder, footer);
        mainVerticalLayout.setAlignment(Pos.TOP_CENTER);

        root.getChildren().add(mainVerticalLayout);

           javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        stage.setTitle("UrbanWash - Welcome!");
        stage.setScene(scene);
        stage.setMaximized(true);

        stage.setOnShown(e -> playIntroAnimations(navBar, leftFeatures, card, rightFeatures, footer));
        stage.show();
    }

    /**
     * Creates a footer styled to match the gradient theme.
     * This method contains the logic from your original Footer class.
     * @return A VBox node representing the footer.
     */
    private Node createFooterNode() {
        VBox footer = new VBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(30, 20, 30, 20));
        footer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");

        Label brand = new Label("UrbanWash");
        brand.setFont(Font.font("System", FontWeight.BOLD, 20));
        brand.setTextFill(Color.WHITE);

        Label tagline = new Label("Stay updated with the latest offers");
        tagline.setFont(Font.font("System", 14));
        tagline.setTextFill(Color.web("#E5E7EB"));

        HBox emailBox = new HBox(10);
        emailBox.setAlignment(Pos.CENTER);
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(250);
        emailField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-prompt-text-fill: #D1D5DB; -fx-background-radius: 6; -fx-border-color: rgba(255, 255, 255, 0.3); -fx-border-radius: 6;");
        
        Button subscribe = new Button("Subscribe");
        subscribe.setStyle("-fx-background-color: white; -fx-text-fill: #2563EB; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-font-weight: bold;");
        subscribe.setCursor(Cursor.HAND);
        emailBox.getChildren().addAll(emailField, subscribe);

        HBox socialIcons = new HBox(20);
        socialIcons.setAlignment(Pos.CENTER);
        socialIcons.getChildren().addAll(
            createSocialLink("https://cdn-icons-png.flaticon.com/24/2111/2111463.png", "https://instagram.com/urbanwash"),
            createSocialLink("https://cdn-icons-png.flaticon.com/24/733/733585.png", "https://wa.me/1234567890"),
            createSocialLink("https://cdn-icons-png.flaticon.com/24/733/733547.png", "https://facebook.com/urbanwash")
        );

        Label copyright = new Label("© 2025 UrbanWash. All Rights Reserved.");
        copyright.setFont(Font.font("System", 12));
        copyright.setTextFill(Color.web("#D1D5DB"));

        footer.getChildren().addAll(brand, tagline, emailBox, socialIcons, copyright);
        return footer;
    }

    /**
     * Helper to create a social media icon with a hyperlink.
     * This method was originally in your Footer class.
     */
    private Hyperlink createSocialLink(String iconUrl, String pageUrl) {
        ImageView icon = new ImageView(new Image(iconUrl));
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        
        Hyperlink link = new Hyperlink("", icon);
        link.setOnAction(e -> openInBrowser(pageUrl));
        
        ScaleTransition st = new ScaleTransition(Duration.millis(150), link);
        st.setToX(1.2);
        st.setToY(1.2);
        link.setOnMouseEntered(e -> st.playFromStart());
        link.setOnMouseExited(e -> {
            st.stop();
            link.setScaleX(1.0);
            link.setScaleY(1.0);
        });

        return link;
    }

    /**
     * Helper to open a URL in the default system browser.
     * This method was originally in your Footer class.
     */
    private void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Button createAnimatedButton(String text, String baseStyle) {
        Button button = new Button(text);
        button.setPrefWidth(280);
        button.setPrefHeight(45);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setStyle(baseStyle + " -fx-background-radius: 8;");
        button.setCursor(Cursor.HAND);

        ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
        st.setToX(1.05);
        st.setToY(1.05);
        button.setOnMouseEntered(e -> st.playFromStart());
        button.setOnMouseExited(e -> {
            st.stop();
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        return button;
    }

    private VBox createFeatureBox(String iconUrl, String text) {
        ImageView icon = new ImageView(new Image(iconUrl));
        icon.setFitWidth(48);
        icon.setFitHeight(48);
        icon.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));

        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.WHITE);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setWrapText(true);
        label.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.8)));

        VBox featureBox = new VBox(12, icon, label);
        featureBox.setAlignment(Pos.CENTER);
        featureBox.setPrefWidth(160);
        return featureBox;
    }

    private void playIntroAnimations(Node navBar, Node leftFeatures, Node card, Node rightFeatures, Node footer) {
        createSlideIn(navBar, Duration.ZERO, 0, -50).play();
        createSlideIn(leftFeatures, Duration.millis(200), -50, 0).play();
        createSlideIn(card, Duration.millis(200), 0, 50).play();
        createSlideIn(rightFeatures, Duration.millis(200), 50, 0).play();
        createSlideIn(footer, Duration.millis(400), 0, 50).play();
    }

    private ParallelTransition createSlideIn(Node node, Duration delay, double fromX, double fromY) {
        node.setOpacity(0);
        node.setTranslateX(fromX);
        node.setTranslateY(fromY);

        FadeTransition ft = new FadeTransition(Duration.millis(700), node);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(700), node);
        tt.setToX(0);
        tt.setToY(0);

        ParallelTransition transition = new ParallelTransition(node, ft, tt);
        transition.setDelay(delay);
        return transition;
    }
}
