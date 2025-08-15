package com.urban_wash.view.user_ui;

import com.urban_wash.Controller.SessionManager;
import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class userp2 extends baseDashBoard {

    // --- 🎨 UI COLOR SCHEME (Consistent with Dashboard) ---
    private final String COLOR_PRIMARY_ACCENT = "#818cf8";
    private final String COLOR_TEXT_PRIMARY = "#FFFFFF";
    private final String COLOR_TEXT_SECONDARY = "#E5E7EB";
    private final String COLOR_SURFACE_TRANSPARENT = "rgba(0, 0, 0, 0.2)";
    private final String COLOR_BORDER_SUBTLE = "rgba(255, 255, 255, 0.2)";
    private final String COLOR_SUCCESS_GREEN = "#4ade80";

    @Override
    public Node createCenterContent() {
        StackPane root = new StackPane();
        // Use a transparent background to show the main app gradient
        root.setStyle("-fx-background-color: transparent;");

        Node processingView = createProcessingView();
        Node successView = createSuccessView();
        successView.setVisible(false);

        root.getChildren().addAll(processingView, successView);

        // --- Animation Sequence ---
        PauseTransition processingDelay = new PauseTransition(Duration.seconds(3)); // Simulate a 3-second payment process

        processingDelay.setOnFinished(e -> {
            // Hide processing view and show success view
            FadeTransition fadeOutProcessing = new FadeTransition(Duration.millis(300), processingView);
            fadeOutProcessing.setToValue(0);
            fadeOutProcessing.setOnFinished(event -> processingView.setVisible(false));
            fadeOutProcessing.play();

            successView.setVisible(true);
            successView.setOpacity(0);
            successView.setScaleX(0.7);
            successView.setScaleY(0.7);
            
            FadeTransition fadeInSuccess = new FadeTransition(Duration.millis(500), successView);
            fadeInSuccess.setToValue(1);
            
            ScaleTransition scaleInSuccess = new ScaleTransition(Duration.millis(500), successView);
            scaleInSuccess.setToX(1.0);
            scaleInSuccess.setToY(1.0);
            scaleInSuccess.setInterpolator(Interpolator.EASE_OUT);

            ParallelTransition successAnimation = new ParallelTransition(fadeInSuccess, scaleInSuccess);
            successAnimation.play();
            
            // Schedule the final redirect to the dashboard
            PauseTransition redirectDelay = new PauseTransition(Duration.seconds(4));
            redirectDelay.setOnFinished(evt -> {
                // --- 🔴 FIXED: Manually navigate back to the dashboard ---
                // This now uses the successView node to reliably get the scene, avoiding the NullPointerException.
                if (successView.getScene() != null) {
                    userdashboard dashboardPage = new userdashboard();
                    Node dashboardContent = dashboardPage.createCenterContent();
                    BorderPane rootPane = (BorderPane) successView.getScene().getRoot();
                    rootPane.setCenter(dashboardContent);
                }
            });
            redirectDelay.play();
        });
        
        processingDelay.play();
        
        return root;
    }

    private Node createProcessingView() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        
        Node spinner = createSpinner();

        Label processingText = new Label("Processing Payment...");
        processingText.setFont(Font.font("System", FontWeight.SEMI_BOLD, 22));
        processingText.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        
        container.getChildren().addAll(spinner, processingText);
        return container;
    }
    
    // A simple animated loading spinner
    private Node createSpinner() {
        StackPane spinner = new StackPane();
        spinner.setPrefSize(60, 60);

        Circle outerCircle = new Circle(30, Color.TRANSPARENT);
        outerCircle.setStroke(Color.web(COLOR_BORDER_SUBTLE));
        outerCircle.setStrokeWidth(4);

        Circle innerArc = new Circle(30, Color.TRANSPARENT);
        innerArc.setStroke(Color.web(COLOR_PRIMARY_ACCENT));
        innerArc.setStrokeWidth(4);
        innerArc.getStrokeDashArray().addAll(50d, 200d);

        spinner.getChildren().addAll(outerCircle, innerArc);

        RotateTransition rotate = new RotateTransition(Duration.seconds(1.5), innerArc);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        return spinner;
    }

    private Node createSuccessView() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setMaxWidth(450); // Adjusted width
        // Themed glassmorphism style for the success card
        container.setStyle("-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-radius: 12;");

        SVGPath checkIcon = new SVGPath();
        checkIcon.setContent("M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z");
        checkIcon.setFill(Color.WHITE);
        StackPane iconBg = new StackPane(checkIcon);
        iconBg.setStyle("-fx-background-color: " + COLOR_SUCCESS_GREEN + "; -fx-background-radius: 50;");
        iconBg.setPrefSize(60, 60);

        Label title = new Label("Payment Successful");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        double totalAmount = SessionManager.getInstance().getFinalOrderTotal();
        Label amount = new Label(String.format("₹%.2f", totalAmount));
        amount.setFont(Font.font("System", FontWeight.BOLD, 36));
        amount.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        VBox detailsBox = new VBox(8);
        detailsBox.setAlignment(Pos.CENTER);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        String transactionId = "T" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        detailsBox.getChildren().addAll(
            createDetailRow("Paid to:", "UrbanWash Services"),
            createDetailRow("Transaction ID:", transactionId),
            createDetailRow("Date:", LocalDateTime.now().format(dtf))
        );
        
        Button doneButton = new Button("Done");
        doneButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        doneButton.setStyle("-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: white; -fx-padding: 8 40; -fx-background-radius: 8;");
        
        doneButton.setOnAction(e -> {
            if (container.getScene() != null) {
                userdashboard dashboardPage = new userdashboard();
                Node dashboardContent = dashboardPage.createCenterContent();
                BorderPane rootPane = (BorderPane) container.getScene().getRoot();
                rootPane.setCenter(dashboardContent);
            }
        });

        container.getChildren().addAll(iconBg, title, amount, detailsBox, doneButton);
        return container;
    }
    
    private Node createDetailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        Label labelText = new Label(label + " ");
        labelText.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        Label valueText = new Label(value);
        valueText.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        valueText.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        row.getChildren().addAll(labelText, valueText);
        return row;
    }
}