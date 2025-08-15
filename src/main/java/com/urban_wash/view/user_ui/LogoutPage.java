package com.urban_wash.view.user_ui;

import com.urban_wash.view.common_methods.baseDashBoard;
import com.urban_wash.view.common_methods.LandingPage;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LogoutPage extends baseDashBoard {

    @Override
    public Node createCenterContent() {
        // A StackPane to center the logout card in the available space.
        StackPane centerContainer = new StackPane();
        centerContainer.setPadding(new Insets(40));
        centerContainer.setAlignment(Pos.CENTER);

        // --- "Confirm Logout" Card ---
        VBox card = new VBox(25); // Increased spacing
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(450);
        // --- STYLE: Updated to "glassmorphism" for dark theme ---
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );

        // --- STYLE: White logout icon ---
        SVGPath icon = new SVGPath();
        icon.setContent("M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z");
        icon.setFill(Color.WHITE);
        icon.setScaleX(1.8);
        icon.setScaleY(1.8);

        // --- STYLE: White heading text ---
        Label heading = new Label("Confirm Logout");
        heading.setFont(Font.font("System", FontWeight.BOLD, 24));
        heading.setTextFill(Color.WHITE);

        // --- STYLE: Light gray subtext ---
        Label subtext = new Label(
            "Are you sure you want to log out? You will need to sign in again to access your account."
        );
        subtext.setFont(Font.font("System", 15));
        subtext.setTextFill(Color.web("#E5E7EB"));
        subtext.setWrapText(true);
        subtext.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        subtext.setLineSpacing(4);

        // --- STYLE: Buttons updated for dark theme ---
        Button logoutBtn = new Button("Log Out");
        logoutBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        logoutBtn.setPrefWidth(130);
        logoutBtn.setPrefHeight(40);
        logoutBtn.setCursor(Cursor.HAND);
        final String logoutNormalStyle = "-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 8;";
        final String logoutHoverStyle = "-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 8;";
        logoutBtn.setStyle(logoutNormalStyle);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutHoverStyle));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutNormalStyle));


        Button cancelBtn = new Button("Cancel");
        cancelBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        cancelBtn.setPrefWidth(130);
        cancelBtn.setPrefHeight(40);
        cancelBtn.setCursor(Cursor.HAND);
        final String cancelNormalStyle = "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-background-radius: 8;";
        final String cancelHoverStyle = "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-background-radius: 8;";
        cancelBtn.setStyle(cancelNormalStyle);
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(cancelHoverStyle));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(cancelNormalStyle));


        // --- Button Actions (Functionality Unchanged) ---
        logoutBtn.setOnAction(e -> {
            try {
                Stage stage = (Stage) logoutBtn.getScene().getWindow();
                new LandingPage().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelBtn.setOnAction(e -> {
            navigateTo("Dashboard");
        });

        HBox buttonRow = new HBox(18, cancelBtn, logoutBtn);
        buttonRow.setAlignment(Pos.CENTER);

        // Assemble card
        card.getChildren().addAll(icon, heading, subtext, buttonRow);
        
        // --- ANIMATION: Add a fade-in effect to the card ---
        card.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), card);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
        
        centerContainer.getChildren().add(card);
        return centerContainer;
    }
}