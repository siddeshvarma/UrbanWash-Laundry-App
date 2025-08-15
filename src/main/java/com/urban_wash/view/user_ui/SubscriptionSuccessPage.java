package com.urban_wash.view.user_ui;

import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// --- IMPORTS ADDED ---
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
// --- END IMPORTS ---
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class SubscriptionSuccessPage extends baseDashBoard {

    private final String planName;
    
    // --- Themed Colors ---
    private final String COLOR_PRIMARY_ACCENT = "#6366F1";
    private final String COLOR_TEXT_PRIMARY = "#FFFFFF";
    private final String COLOR_TEXT_SECONDARY = "#D1D5DB";
    private final String COLOR_SUCCESS_GREEN = "#22c55e";
    private final String COLOR_BORDER_SUBTLE = "rgba(255, 255, 255, 0.2)";


    public SubscriptionSuccessPage(String planName) {
        this.planName = planName;
    }

    @Override
    protected Node createCenterContent() {
        // --- Main Layout ---
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
        // Use a transparent background to show the main app gradient
        layout.setStyle("-fx-background-color: transparent;");

        // --- Success Icon (Themed for dark background) ---
        ImageView successIcon = new ImageView(new Image("https://img.icons8.com/fluency/96/ffffff/checked-2.png"));
        successIcon.setFitWidth(80);
        successIcon.setFitHeight(80);

        // --- Title ---
        Label title = new Label("Payment Successful!");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web(COLOR_SUCCESS_GREEN)); // Use a bright success green

        // --- Subtitle (Themed for dark background) ---
        Label subtitle = new Label("Welcome to the " + planName + "! Your subscription is now active. You can now enjoy all the premium benefits.");
        subtitle.setFont(Font.font("System", 16));
        subtitle.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(TextAlignment.CENTER);
        subtitle.setMaxWidth(450);

        // --- Go to Dashboard Button ---
        Button dashboardButton = new Button("Go to My Dashboard");
        stylePrimaryButton(dashboardButton);
        // Use the navigateTo method from baseDashBoard for seamless navigation
        dashboardButton.setOnAction(e -> navigateTo("Dashboard"));

        // --- Add all nodes to layout ---
        layout.getChildren().addAll(successIcon, title, subtitle, dashboardButton);
        return layout;
    }

    /**
     * Overrides the sidebar creation to provide a consistent, themed navigation experience.
     * @return A VBox containing the themed sidebar.
     */
    @Override
    protected VBox createLeftSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: transparent;");
        sidebar.setPadding(new Insets(15, 10, 15, 10));

        VBox navItems = new VBox(10);
        navItems.setPadding(new Insets(15));

        // Using themed white icons
        createNavButton(navItems, "Dashboard", "https://img.icons8.com/ios-filled/50/ffffff/dashboard-layout.png");
        createNavButton(navItems, "ShopList", "https://img.icons8.com/ios-filled/50/ffffff/purchase-order.png");
        createNavButton(navItems, "Order Tracking", "https://img.icons8.com/ios-filled/50/ffffff/delivery.png");
        createNavButton(navItems, "Subscription", "https://img.icons8.com/ios-filled/50/ffffff/service.png");
        createNavButton(navItems, "Profile", "https://img.icons8.com/ios-filled/50/ffffff/user-male-circle.png");
        createNavButton(navItems, "AI Assistant", "https://img.icons8.com/ios-filled/50/ffffff/bot.png");

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // A themed user info box at the bottom of the sidebar
        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(15, 10, 10, 10));
        userBox.setStyle("-fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-width: 1 0 0 0;");

        ImageView avatar = new ImageView(new Image("https://img.icons8.com/ios-glyphs/60/ffffff/user-male-circle.png"));
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);

      

   
        
        VBox bottomNav = new VBox(10);
        bottomNav.setPadding(new Insets(0, 15, 15, 15));
        createNavButton(bottomNav, "Logout", "https://img.icons8.com/ios-filled/50/ffffff/logout-rounded-left.png");

        sidebar.getChildren().addAll(navItems, spacer, userBox, bottomNav);
        
        // Highlight the "Dashboard" nav item by default on this page
        Platform.runLater(() -> highlightNavItem("Dashboard", false));

        return sidebar;
    }

    /**
     * Helper method to style the primary action button.
     * @param btn The button to style.
     */
    private void stylePrimaryButton(Button btn) {
        String baseStyle = "-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;";
        String hoverStyle = "-fx-background-color: #4338ca; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
    }
}