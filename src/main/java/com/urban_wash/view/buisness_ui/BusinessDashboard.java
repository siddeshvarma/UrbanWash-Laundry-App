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
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BusinessDashboard extends baseBuisness {

    // --- UI ELEMENTS ---
    private Label totalOrdersValue = new Label("0");
    private Label pendingDeliveriesValue = new Label("0");
    private Label revenueTodayValue = new Label("₹0.00");
    private Label activeServicesValue = new Label("0");
    private TextFlow revenueMonthText = new TextFlow();
    private Label readyForPickupValue = new Label("0");
    private Label outForDeliveryValue = new Label("0");
    private Label completedTodayValue = new Label("0");
    private VBox recentOrdersContainer;

    @Override
  
public Node createCenterContent() {
    // Main container with only vertical padding, allowing for full-width elements
    VBox content = new VBox(30);
    content.setPadding(new Insets(30, 0, 40, 0)); 
    content.setStyle("-fx-background-color: transparent;");

    // Uses the correctly styled title from the parent class
    Label title = createSectionTitle("Shopkeeper Dashboard");

    HBox statsRow = new HBox(20);
    statsRow.getChildren().addAll(
            createStatCard("Total Orders", totalOrdersValue),
            createStatCard("Pending Deliveries", pendingDeliveriesValue),
            createStatCard("Revenue Today", revenueTodayValue),
            createStatCard("Active Services", activeServicesValue)
    );

    HBox mainPanels = new HBox(20);
    mainPanels.getChildren().addAll(
            createRecentOrdersPanel(),
            createEarningsOverviewPanel()
    );
    
    // --- ANIMATION ---
    // Set initial state for animations
    statsRow.setOpacity(0);
    mainPanels.setOpacity(0);
    title.setOpacity(0); // Also animate the title

    // A nested container to apply horizontal padding to specific sections
    VBox paddedContent = new VBox(30);
    paddedContent.setPadding(new Insets(0, 30, 0, 30)); // Apply horizontal padding here
    paddedContent.getChildren().addAll(title, statsRow, mainPanels);

    content.getChildren().add(paddedContent);
    
    // Pass nodes to be animated to the data loading method
    loadDashboardData(title, statsRow, mainPanels);

    // --- SCROLLPANE SETUP (Created but not returned) ---
    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setStyle(
        "-fx-background: transparent; -fx-background-color: transparent; " 
    );

    // As requested, returning the VBox 'content' directly
    return content;
}

    private void loadDashboardData(Node... nodesToAnimate) {
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        recentOrdersContainer.getChildren().add(loadingIndicator);

        new Thread(() -> {
            try {
                FirestoreService firestoreService = new FirestoreService();
                Business currentBusiness = SessionManager.getInstance().getSelectedBusiness();
                if (currentBusiness == null || currentBusiness.getDocumentId() == null) {
                    Platform.runLater(() -> {
                        Label errorLabel = new Label("Error: Business not found.");
                        errorLabel.setTextFill(Color.WHITE);
                        recentOrdersContainer.getChildren().setAll(errorLabel);
                    });
                    return;
                }
                String businessId = currentBusiness.getDocumentId();

                List<Order> orders = firestoreService.fetchOrdersForBusiness(businessId);

                // --- Original Logic (No Changes) ---
                long totalOrderCount = orders.size();
                List<String> finalStatuses = Arrays.asList("completed", "delivered", "cancelled");
                long pendingCount = orders.stream()
                        .filter(o -> o.getStatus() != null && !finalStatuses.contains(o.getStatus().toLowerCase()))
                        .count();
                double todayRevenue = calculateRevenueForDate(orders, LocalDate.now());
                int serviceCount = currentBusiness.getServices() != null ? currentBusiness.getServices().size() : 0;
                double monthRevenue = calculateRevenueForMonth(orders, LocalDate.now());

                Platform.runLater(() -> {
                    totalOrdersValue.setText(String.valueOf(totalOrderCount));
                    pendingDeliveriesValue.setText(String.valueOf(pendingCount));
                    revenueTodayValue.setText(formatCurrency(todayRevenue));
                    activeServicesValue.setText(String.valueOf(serviceCount));
                    populateRecentOrders(orders);
                    populateEarnings(monthRevenue);

                    // --- ANIMATION ---
                    for (int i = 0; i < nodesToAnimate.length; i++) {
                        FadeTransition ft = new FadeTransition(Duration.millis(500), nodesToAnimate[i]);
                        ft.setFromValue(0.0);
                        ft.setToValue(1.0);
                        ft.setDelay(Duration.millis(i * 100));
                        ft.play();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Label errorLabel = new Label("Failed to load order data.");
                    errorLabel.setTextFill(Color.WHITE);
                    recentOrdersContainer.getChildren().setAll(errorLabel);
                });
            }
        }).start();
    }

    // --- PANEL CREATION METHODS ---

    private Node createRecentOrdersPanel() {
        VBox panel = createBasePanel("Recent Orders");
        recentOrdersContainer = new VBox(10);
        panel.getChildren().add(recentOrdersContainer);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Button viewAllButton = createStyledButton("View All Orders");
        viewAllButton.setOnAction(e -> System.out.println("Navigate to Orders page..."));
        
        panel.getChildren().addAll(spacer, viewAllButton);
        return panel;
    }

    private Node createEarningsOverviewPanel() {
        VBox panel = createBasePanel("Earnings Overview");
        populateEarnings(0.0);
        panel.getChildren().add(revenueMonthText);
        return panel;
    }

    // --- DATA POPULATION & HELPER METHODS (STYLES UPDATED FOR DARK THEME) ---

    private void populateRecentOrders(List<Order> allOrders) {
        recentOrdersContainer.getChildren().clear();
        if (allOrders == null || allOrders.isEmpty()) {
            Label noOrdersLabel = new Label("No recent orders found.");
            noOrdersLabel.setTextFill(Color.web("#E5E7EB"));
            recentOrdersContainer.getChildren().add(noOrdersLabel);
            return;
        }
        
        List<Order> recentOrders = allOrders.stream()
                .filter(order -> order.getDocumentId() != null && !order.getDocumentId().isEmpty())
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(4)
                .collect(Collectors.toList());

        for (Order order : recentOrders) {
            StatusStyle style = getStatusStyle(order.getStatus());
            String orderId = (order.getDocumentId().length() > 6)
                    ? "#" + order.getDocumentId().substring(0, 6).toUpperCase()
                    : "#" + order.getDocumentId().toUpperCase();
            recentOrdersContainer.getChildren().add(createOrderRow(
                    order.getCustomerName(), "Order " + orderId, order.getStatus(),
                    style.bgColor, style.textColor, formatCurrency(order.getTotalAmount())
            ));
        }
    }

    private void populateEarnings(double revenue) {
        Text intro = new Text("Total Revenue this month: ");
        intro.setFont(Font.font("System", FontWeight.NORMAL, 14));
        intro.setFill(Color.web("#E5E7EB")); // Light text

        Text value = new Text(formatCurrency(revenue));
        value.setFont(Font.font("System", FontWeight.BOLD, 16));
        value.setFill(Color.web("#34D399")); // Bright green
        revenueMonthText.getChildren().setAll(intro, value);
    }

    private VBox createStatCard(String title, Node valueNode) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        // --- STYLE: "Glassmorphism" effect for stat cards ---
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 12;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        titleLabel.setTextFill(Color.web("#E5E7EB"));
        
        valueNode.setStyle("-fx-font-family: 'System'; -fx-font-weight: bold; -fx-font-size: 28px; -fx-text-fill: white;");
        card.getChildren().addAll(titleLabel, valueNode);
        return card;
    }

    @Override
    protected Label createSectionTitle(String text) {
        // Overridden to match the parent's dark theme style
        Label title = new Label(text);
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.4)));
        return title;
    }

    private static class StatusStyle {
        final String bgColor; final String textColor;
        StatusStyle(String bgColor, String textColor) { this.bgColor = bgColor; this.textColor = textColor; }
    }

    private StatusStyle getStatusStyle(String status) {
        if (status == null) status = "";
        switch (status.toLowerCase()) {
            // --- STYLE: Updated text color to white for better contrast ---
            case "ready for delivery": case "completed": return new StatusStyle("#10B981", "#FFFFFF");
            case "in progress": return new StatusStyle("#F59E0B", "#FFFFFF");
            case "new": case "pending": return new StatusStyle("#EF4444", "#FFFFFF");
            default: return new StatusStyle("#4B5563", "#E5E7EB");
        }
    }
    
    private boolean isOrderDateToday(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return false;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isEqual(LocalDate.now());
        } catch (DateTimeParseException e) { return false; }
    }

    private double calculateRevenueForDate(List<Order> orders, LocalDate date) {
        return orders.stream().filter(o -> isOrderDateToday(o.getOrderDate())).mapToDouble(Order::getTotalAmount).sum();
    }
    
    private double calculateRevenueForMonth(List<Order> orders, LocalDate date) {
        return orders.stream().filter(o -> {
            if(o.getOrderDate() == null || o.getOrderDate().isEmpty()) return false;
            try {
                LocalDate orderDate = LocalDate.parse(o.getOrderDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return orderDate.getMonth() == date.getMonth() && orderDate.getYear() == date.getYear();
            } catch (DateTimeParseException e) { return false; }
        }).mapToDouble(Order::getTotalAmount).sum();
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(amount);
    }
    
    private Node createDeliveryStatusRow(String status, Label countLabel) {
        BorderPane row = new BorderPane();
        Label statusLabel = new Label(status);
        statusLabel.setTextFill(Color.web("#E5E7EB"));
        countLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        countLabel.setTextFill(Color.WHITE);
        row.setLeft(statusLabel);
        row.setRight(countLabel);
        return row;
    }

    private VBox createBasePanel(String title) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        // --- STYLE: "Glassmorphism" effect for main panels ---
        panel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        panel.setPrefHeight(300);
        HBox.setHgrow(panel, Priority.ALWAYS);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        panel.getChildren().add(titleLabel);
        return panel;
    }
    
    private Node createOrderRow(String name, String orderId, String status, String statusBgColor, String statusTextColor, String price) {
        BorderPane row = new BorderPane();
        row.setPadding(new Insets(5, 0, 5, 0));

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.WHITE);
        Label orderIdLabel = new Label(orderId);
        orderIdLabel.setTextFill(Color.web("#E5E7EB"));
        
        Label statusLabel = new Label(status);
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusLabel.setTextFill(Color.web(statusTextColor));
        statusLabel.setPadding(new Insets(4, 9, 4, 9));
        statusLabel.setStyle("-fx-background-color: " + statusBgColor + "; -fx-background-radius: 12;");
        
        VBox leftSide = new VBox(2, nameLabel, orderIdLabel);
        
        Label priceLabel = new Label(price);
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        priceLabel.setTextFill(Color.WHITE);
        
        VBox rightSide = new VBox(2, priceLabel, statusLabel);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        row.setLeft(leftSide);
        row.setRight(rightSide);
        return row;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        // --- STYLE: Button style for dark theme ---
        final String normalStyle = "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
        final String hoverStyle = "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
        return button;
    }
}