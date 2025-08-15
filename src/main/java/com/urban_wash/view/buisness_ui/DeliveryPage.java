package com.urban_wash.view.buisness_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.Model.Business;
import com.urban_wash.Model.Order;
import com.urban_wash.view.common_methods.baseBuisness;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeliveryPage extends baseBuisness {

    // --- SVG Paths for Icons ---
    private static final String ICON_IN_PROGRESS = "M12 6v6l4 2-1 1.73L11 13V6h1z M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z";
    private static final String ICON_COMPLETED = "M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z";

    private final FirestoreService firestoreService = new FirestoreService();
    private VBox inProgressOrdersList;
    private VBox completedOrdersList;
    private Label inProgressCountLabel;
    private Label completedCountLabel;


    @Override
    public Node createCenterContent() {
        VBox mainContainer = new VBox(30);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        
        // --- STYLE: Use parent's createSectionTitle for a consistent heading ---
        Label title = createSectionTitle("Delivery Management");

        FlowPane panelsContainer = new FlowPane();
        panelsContainer.setHgap(30);
        panelsContainer.setVgap(30);
        panelsContainer.setAlignment(Pos.TOP_CENTER);

        inProgressOrdersList = new VBox(20);
        completedOrdersList = new VBox(20);

        panelsContainer.getChildren().addAll(
                createDeliveryPanel("In Progress", "#FF9800", inProgressOrdersList, true),
                createDeliveryPanel("Completed", "#4CAF50", completedOrdersList, false)
        );
        
        loadAndCategorizeOrders();

        // --- ANIMATION: Fade in the main container ---
        mainContainer.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), mainContainer);
        ft.setToValue(1.0);
        ft.play();
        
        mainContainer.getChildren().addAll(title, panelsContainer);
        return mainContainer;
    }

    private void loadAndCategorizeOrders() {
        inProgressOrdersList.getChildren().add(new ProgressIndicator());
        completedOrdersList.getChildren().add(new ProgressIndicator());

        Business currentBusiness = SessionManager.getInstance().getSelectedBusiness();
        if (currentBusiness == null || currentBusiness.getDocumentId() == null) {
            Label errorLabel = new Label("Business session not found.");
            errorLabel.setTextFill(Color.WHITE);
            inProgressOrdersList.getChildren().setAll(errorLabel);
            completedOrdersList.getChildren().clear();
            return;
        }

        new Thread(() -> {
            List<Order> allOrders = firestoreService.fetchOrdersForBusiness(currentBusiness.getDocumentId());

            List<Node> inProgressCards = allOrders.stream()
                    .filter(o -> o.getStatus() != null && !"Delivered".equalsIgnoreCase(o.getStatus()) && !"Cancelled".equalsIgnoreCase(o.getStatus()))
                    .map(this::createInProgressOrderCard)
                    .collect(Collectors.toList());

            List<Node> completedCards = allOrders.stream()
                    .filter(o -> o.getStatus() != null && "Delivered".equalsIgnoreCase(o.getStatus()))
                    .map(this::createCompletedOrderCard)
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                // --- ANIMATION: Apply staggered fade-in to cards ---
                populateListWithAnimation(inProgressOrdersList, inProgressCards, "No orders in progress.");
                populateListWithAnimation(completedOrdersList, completedCards, "No completed orders.");
                
                inProgressCountLabel.setText(String.valueOf(inProgressCards.size()));
                completedCountLabel.setText(String.valueOf(completedCards.size()));
            });
        }).start();
    }

    private void populateListWithAnimation(VBox listContainer, List<Node> cards, String placeholderText) {
        listContainer.getChildren().clear();
        if (cards.isEmpty()) {
            Label placeholder = new Label(placeholderText);
            placeholder.setTextFill(Color.web("#E5E7EB"));
            listContainer.getChildren().add(placeholder);
        } else {
            AtomicInteger index = new AtomicInteger(0);
            cards.forEach(card -> {
                card.setOpacity(0);
                listContainer.getChildren().add(card);
                FadeTransition ft = new FadeTransition(Duration.millis(400), card);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.setDelay(Duration.millis(index.getAndIncrement() * 70));
                ft.play();
            });
        }
    }

    private VBox createDeliveryPanel(String title, String accentColor, VBox ordersList, boolean isInProgress) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(0)); // Padding will be on inner content
        // --- STYLE: Glassmorphism effect ---
        panel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        panel.setPrefWidth(450);
        panel.setMaxWidth(450);

        // --- STYLE: Gradient header for the panel ---
        Node panelHeader = createPanelTitle(title, accentColor, isInProgress);
        
        ScrollPane scrollPane = new ScrollPane(ordersList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(600);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        // --- STYLE: Transparent scrollpane ---
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        ordersList.setPadding(new Insets(20));

        panel.getChildren().addAll(panelHeader, scrollPane);
        return panel;
    }

    private Node createPanelTitle(String title, String accentColor, boolean isInProgress) {
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(15, 20, 15, 20));
        // --- STYLE: Gradient header for each panel ---
        titleBox.setStyle("-fx-background-color: linear-gradient(to right, " + accentColor + " 0%, " + accentColor + "20 100%); -fx-background-radius: 12 12 0 0;");
        
        SVGPath icon = createIcon(isInProgress ? ICON_IN_PROGRESS : ICON_COMPLETED, 20, Color.WHITE);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label countLabel = new Label("0");
        countLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        countLabel.setTextFill(Color.WHITE);
        countLabel.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-background-radius: 6; -fx-padding: 2 8;");
        
        if (isInProgress) {
            inProgressCountLabel = countLabel;
        } else {
            completedCountLabel = countLabel;
        }

        titleBox.getChildren().addAll(icon, titleLabel, spacer, countLabel);
        return titleBox;
    }

    private Node createOrderCard(Order order, String statusBgColor, String statusTextColor) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        // --- STYLE: Nested glass effect for cards ---
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 10;");

        BorderPane topRow = new BorderPane();
        Label orderIdLabel = new Label("Order #" + (order.getDocumentId() != null ? order.getDocumentId().substring(0, 6).toUpperCase() : "N/A"));
        orderIdLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        orderIdLabel.setTextFill(Color.WHITE);
        
        Label statusLabel = new Label(order.getStatus());
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusLabel.setTextFill(Color.web(statusTextColor));
        statusLabel.setPadding(new Insets(4, 9, 4, 9));
        statusLabel.setStyle("-fx-background-color: " + statusBgColor + "; -fx-background-radius: 12;");
        
        topRow.setLeft(orderIdLabel);
        topRow.setRight(statusLabel);

        // Details Section
        VBox detailsVBox = new VBox(5);
        detailsVBox.setPadding(new Insets(8, 0, 8, 0));

        Map<String, String> addressMap = order.getDeliveryAddress();
        String fullAddress = "N/A";
        if (addressMap != null && !addressMap.isEmpty()) {
            fullAddress = Stream.of(addressMap.getOrDefault("address1", ""), addressMap.getOrDefault("city", ""), addressMap.getOrDefault("state", ""), addressMap.getOrDefault("pincode", ""))
                    .filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(", "));
            if (fullAddress.isEmpty()) fullAddress = "N/A";
        }
        
        detailsVBox.getChildren().addAll(
            createDetailRow("Name:", order.getCustomerName()),
            createDetailRow("Address:", fullAddress),
            createDetailRow("Mobile:", order.getCustomerPhone() != null ? order.getCustomerPhone() : "N/A"),
            createDetailRow("Total:", String.format("₹%.2f", order.getTotalAmount()))
        );

        card.getChildren().addAll(topRow, detailsVBox);
        return card;
    }

    private Node createInProgressOrderCard(Order order) {
        String status = order.getStatus();
        String statusBgColor = "#4F46E5"; // Default Indigo
        String statusTextColor = "#FFFFFF";

        if ("Picked Up".equalsIgnoreCase(status)) {
            statusBgColor = "#F59E0B"; // Amber
        } else if ("Washing".equalsIgnoreCase(status)) {
            statusBgColor = "#3B82F6"; // Blue
        }
        
        return createOrderCard(order, statusBgColor, statusTextColor);
    }
    
    private Node createCompletedOrderCard(Order order) {
        return createOrderCard(order, "#10B981", "#FFFFFF");
    }

    private Node createDetailRow(String label, String value) {
        HBox row = new HBox(5);
        Label labelText = new Label(label);
        labelText.setFont(Font.font("System", FontWeight.BOLD, 13));
        labelText.setTextFill(Color.web("#A5B4FC")); // Light accent color for label
        
        Label valueText = new Label(value);
        valueText.setFont(Font.font("System", FontWeight.NORMAL, 13));
        valueText.setTextFill(Color.web("#E5E7EB")); // Main light text
        valueText.setWrapText(true);
        row.setMaxWidth(400); // Ensure wrapping within the card
        HBox.setHgrow(valueText, Priority.ALWAYS);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }
    
    private SVGPath createIcon(String content, double size, Color color) {
        SVGPath path = new SVGPath();
        path.setContent(content);
        path.setFill(color);
        path.setScaleX(size / 24.0);
        path.setScaleY(size / 24.0);
        return path;
    }
}