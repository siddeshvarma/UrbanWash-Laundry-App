package com.urban_wash.view.user_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.Model.Business;
import com.urban_wash.Model.Order;
import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ordertracking extends baseDashBoard {

    private static final String ICON_BOX = "M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z M3.27 6.96 12 12.01l8.73-5.05 M12 22.08V12";
    private static final String ICON_WASHING_MACHINE = "M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-1.5 3h-9c-.28 0-.5.22-.5.5s.22.5.5.5h9c.28 0 .5-.22.5-.5s-.22-.5-.5-.5zM12 19c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5z";
    private static final String ICON_TRUCK = "M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4zM6 18c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm13.5-8.5 1.96 2.5H17V9.5h2.5zM18 18c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1z";
    private static final String ICON_CHECK = "M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z";
    private static final String ICON_LIST = "M3 13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z";
    private static final String ICON_SHOP = "M12 7V3H2v18h20V7H12zM6 19H4v-2h2v2zm0-4H4v-2h2v2zm0-4H4V9h2v2zm0-4H4V5h2v2zm4 12H8v-2h2v2zm0-4H8v-2h2v2zm0-4H8V9h2v2zm0-4H8V5h2v2zm10 12h-8v-2h2v-2h-2v-2h2v-2h-2V9h8v10z";
    private static final String ICON_ITEMS = "M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z";
    private static final String ICON_CALENDAR = "M17 12h-5v5h5v-5zM16 1v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2h-1V1h-2zm3 18H5V8h14v11z";

    private VBox mainContainer;
    private final FirestoreService firestoreService = new FirestoreService();

    public Node createCenterContent() {
        mainContainer = new VBox(30);
        mainContainer.setPadding(new Insets(30));
        
        // Use styled title from base class
        Label title = createSectionTitle("Order Tracking");
        mainContainer.getChildren().add(title);
        
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        mainContainer.getChildren().add(loadingIndicator);
        
        loadOrderData();
        
        return mainContainer;
    }
    
    private void loadOrderData() {
        String userId = SessionManager.getInstance().getCurrentUserUid();
        if (userId == null) {
            Platform.runLater(() -> {
                mainContainer.getChildren().clear();
                Label errorLabel = new Label("Could not find user session. Please log in again.");
                errorLabel.setTextFill(Color.WHITE);
                mainContainer.getChildren().add(errorLabel);
            });
            return;
        }

        new Thread(() -> {
            Order latestOrder = firestoreService.fetchLatestOrderByUserId(userId);
            
            Business business = null;
            if (latestOrder != null && latestOrder.getBusinessId() != null && !latestOrder.getBusinessId().isEmpty()) {
                business = firestoreService.fetchBusinessById(latestOrder.getBusinessId());
            }
            final Business finalBusiness = business;

            Platform.runLater(() -> {
                mainContainer.getChildren().removeIf(node -> node instanceof ProgressIndicator);
                if (latestOrder != null) {
                    Node statusPanel = createOrderStatusPanel(latestOrder);
                    Node detailsPanel = createOrderDetailsPanel(latestOrder, finalBusiness);

                    // Add animation to the panels
                    statusPanel.setOpacity(0);
                    detailsPanel.setOpacity(0);
                    
                    FadeTransition ft1 = new FadeTransition(Duration.millis(500), statusPanel);
                    ft1.setToValue(1.0);
                    
                    FadeTransition ft2 = new FadeTransition(Duration.millis(500), detailsPanel);
                    ft2.setToValue(1.0);
                    ft2.setDelay(Duration.millis(100)); // Stagger the animation
                    
                    ft1.play();
                    ft2.play();

                    mainContainer.getChildren().addAll(statusPanel, detailsPanel);
                } else {
                    Label noOrdersLabel = new Label("No active orders found.");
                    noOrdersLabel.setFont(Font.font("System", FontWeight.NORMAL, 18));
                    noOrdersLabel.setTextFill(Color.WHITE); // White text
                    mainContainer.getChildren().add(noOrdersLabel);
                }
            });
        }).start();
    }
    
    private Node createOrderStatusPanel(Order order) {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25));
        // --- STYLE: Matched to dark "glass" theme ---
        panel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );

        Label title = new Label("Order Status: #" + (order.getDocumentId() != null ? order.getDocumentId().substring(0,6).toUpperCase() : "N/A"));
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE); // White text

        VBox stepper = new VBox();
        stepper.setPadding(new Insets(20, 0, 0, 10));
        
        List<String> statusSequence = Arrays.asList("Picked Up", "Washing", "On Delivery", "Delivered");
        String currentStatus = order.getStatus();
        int currentStatusIndex = statusSequence.indexOf(currentStatus);
        
        // Handle variations in status names
        if ("Out for Delivery".equalsIgnoreCase(currentStatus) || "In Transit".equalsIgnoreCase(currentStatus)) {
            currentStatusIndex = statusSequence.indexOf("On Delivery");
        }

        stepper.getChildren().addAll(
            createStatusStep(ICON_BOX, "Picked Up", currentStatusIndex >= 0, currentStatusIndex == 0, false),
            createStatusStep(ICON_WASHING_MACHINE, "Washing", currentStatusIndex >= 1, currentStatusIndex == 1, false),
            createStatusStep(ICON_TRUCK, "On Delivery", currentStatusIndex >= 2, currentStatusIndex == 2, false),
            createStatusStep(ICON_CHECK, "Delivered", currentStatusIndex >= 3, currentStatusIndex == 3, true)
        );

        panel.getChildren().addAll(title, stepper);
        return panel;
    }
    
    private Node createStatusStep(String svgIcon, String title, boolean isCompleted, boolean isCurrent, boolean isLast) {
        HBox step = new HBox(20);
        step.setAlignment(Pos.CENTER_LEFT);

        Circle iconBg = new Circle(20);
        SVGPath icon = createIcon(svgIcon, 20);
        StackPane iconPane = new StackPane(iconBg, icon);

        Line line = new Line(0, 0, 0, 60);
        line.setStrokeWidth(2);
        line.setVisible(!isLast);

        StackPane connectorPane = new StackPane(line, iconPane);
        StackPane.setAlignment(line, Pos.TOP_CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        VBox textVBox = new VBox(2, titleLabel);
        
        // --- STYLE: Colors matched to dark theme ---
        if (isCompleted) {
            iconBg.setFill(Color.web("#A855F7")); // Accent Purple
            icon.setFill(Color.WHITE);
            line.setStroke(Color.web("#A855F7"));
            titleLabel.setTextFill(Color.WHITE);
        } else {
            iconBg.setFill(Color.web("rgba(255, 255, 255, 0.1)")); // Dim background
            icon.setFill(Color.web("#9CA3AF")); // Dim icon
            line.setStroke(Color.web("rgba(255, 255, 255, 0.2)")); // Dim line
            titleLabel.setTextFill(Color.web("#9CA3AF")); // Dim text
        }
        
        if (isCurrent) {
            Label statusLabel = new Label("Current Status");
            statusLabel.setFont(Font.font("System", 14));
            statusLabel.setTextFill(Color.web("#A855F7"));
            textVBox.getChildren().add(statusLabel);
        } else if (isCompleted) {
             Label statusLabel = new Label("Completed");
             statusLabel.setFont(Font.font("System", 14));
             statusLabel.setTextFill(Color.web("#A0AEC0")); // Light gray for completed status text
             textVBox.getChildren().add(statusLabel);
        }

        step.getChildren().addAll(connectorPane, textVBox);
        return step;
    }

    private Node createOrderDetailsPanel(Order order, Business business) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(25));
        // --- STYLE: Matched to dark "glass" theme ---
        panel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );

        Label title = new Label("Order Details");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE); // White text
        
        String shopName = (business != null) ? business.getShopName() : "N/A";
        
        String deliveryDate = "N/A";
        if (order.getOrderDate() != null && !order.getOrderDate().isEmpty()) {
            try {
                Instant instant = Instant.parse(order.getOrderDate());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'by' h:mm a").withZone(ZoneId.systemDefault());
                deliveryDate = formatter.format(instant);
            } catch (Exception e) {
                deliveryDate = order.getOrderDate();
            }
        }
        
        panel.getChildren().addAll(
            title,
            createDetailRow(ICON_LIST, "Order Number", (order.getDocumentId() != null ? order.getDocumentId().substring(0,6).toUpperCase() : "N/A")),
            createDetailRow(ICON_SHOP, "Laundry Shop", shopName),
            createDetailRow(ICON_ITEMS, "Total Amount", String.format("₹%.2f", order.getTotalAmount())),
            createDetailRow(ICON_CALENDAR, "Estimated Delivery", deliveryDate)
        );
        return panel;
    }
    
    private Node createDetailRow(String svgIcon, String label, String value) {
        BorderPane row = new BorderPane();
        row.setPadding(new Insets(10, 0, 10, 0));

        // --- STYLE: Colors matched to dark theme ---
        SVGPath icon = createIcon(svgIcon, 18);
        icon.setFill(Color.web("#A855F7")); // Accent purple
        Label labelText = new Label(label);
        labelText.setTextFill(Color.web("#E5E7EB")); // Light gray text
        HBox left = new HBox(15, icon, labelText);
        left.setAlignment(Pos.CENTER_LEFT);

        Label valueText = new Label(value);
        valueText.setFont(Font.font("System", FontWeight.BOLD, 14));
        valueText.setTextFill(Color.WHITE); // White text

        row.setLeft(left);
        row.setRight(valueText);
        return row;
    }

    private SVGPath createIcon(String content, double size) {
        SVGPath path = new SVGPath();
        path.setContent(content);
        path.setScaleX(size / 24.0);
        path.setScaleY(size / 24.0);
        return path;
    }
}