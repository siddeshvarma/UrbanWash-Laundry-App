package com.urban_wash.view.common_methods;

import com.urban_wash.view.buisness_ui.LaundryLoginPage;
import com.urban_wash.view.user_ui.AiChatbotPage;
import com.urban_wash.view.user_ui.LogoutPage;
import com.urban_wash.view.user_ui.SubscriptionPage;
import com.urban_wash.view.user_ui.SubscriptionSuccessPage;
import com.urban_wash.view.user_ui.UserProfilePage;
import com.urban_wash.view.user_ui.ordertracking;
import com.urban_wash.view.user_ui.shoplist;
import com.urban_wash.view.user_ui.userdashboard;
import com.urban_wash.view.user_ui.userp1;
import com.urban_wash.view.user_ui.userp2;

// --- ANIMATION IMPORTS ADDED/UPDATED ---
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
// --- END OF IMPORTS ---

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * This is the abstract base class for all dashboard screens.
 * It now extends Application to serve as the base for launchable UI classes.
 */
public abstract class baseDashBoard extends Application {

    protected BorderPane root;
    private final HashMap<String, HBox> navItemsMap = new HashMap<>();
    private String currentSelected = "";
    public Stage primaryStage;

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    /**
     * This method is inherited from Application and sets up the primary stage.
     * Subclasses will inherit this behavior automatically.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new BorderPane();
        Header header = new Header();
        // STYLE MODIFIED: Make header transparent to show gradient
        header.setStyle("-fx-background-color: transparent;");

        Node leftSidebar = createLeftSidebar();
        Node centerContent = createCenterContent();

        root.setTop(header);
        root.setLeft(leftSidebar);
        root.setCenter(centerContent);

        // --- STYLE MODIFIED: Apply linear gradient background ---
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #2563EB, #8B5CF6);");
        
        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        
        primaryStage.setTitle("UrbanWash Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        
        // --- ANIMATION ADDED: Play intro animations when stage is shown ---
        primaryStage.setOnShown(e -> playIntroAnimations(header, leftSidebar, centerContent));
        primaryStage.show();
    }

    protected VBox createLeftSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(260); // Slightly wider for better padding
        // --- STYLE MODIFIED: "Glassmorphism" effect for sidebar ---
        sidebar.setStyle(
          
            "-fx-border-width: 0 1 0 0;"
        );
        sidebar.setPadding(new Insets(15, 10, 15, 10)); // Adjusted padding

        VBox navItems = new VBox(10);
        navItems.setPadding(new Insets(15));

        // --- ICONS MODIFIED: Switched to white icons for visibility ---
        createNavButton(navItems, "Dashboard", "https://img.icons8.com/material-sharp/50/ffffff/dashboard.png");
        createNavButton(navItems, "ShopList", "https://img.icons8.com/ios-filled/50/ffffff/purchase-order.png");
        createNavButton(navItems, "Order Tracking", "https://img.icons8.com/ios-filled/50/ffffff/delivery.png");
        createNavButton(navItems, "Subscription", "https://img.icons8.com/ios-filled/50/ffffff/service.png");
        createNavButton(navItems, "Profile", "https://img.icons8.com/ios-filled/50/ffffff/user-male-circle.png");
        createNavButton(navItems, "AI Assistant", "https://img.icons8.com/ios-filled/50/ffffff/bot.png");

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        VBox bottomNav = new VBox(10);
        bottomNav.setPadding(new Insets(15));
        createNavButton(bottomNav, "Logout", "https://img.icons8.com/ios-filled/50/ffffff/logout-rounded-left.png");
        
        sidebar.getChildren().addAll(navItems, spacer, bottomNav);
        highlightNavItem("Dashboard", false);
        return sidebar;
    }

    protected abstract Node createCenterContent();

    public void createNavButton(VBox container, String title, String iconUrl) {
        HBox navItem = new HBox(15);
        navItem.setAlignment(Pos.CENTER_LEFT);
        navItem.setPadding(new Insets(10, 10, 10, 15));

        // --- STYLES MODIFIED: Adapted for the new theme ---
        String normalStyle = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(255, 255, 255, 0.15); -fx-background-radius: 8; -fx-cursor: hand;";

        navItem.setStyle(normalStyle);
        ImageView icon = new ImageView(new Image(iconUrl, 20, 20, true, true)); // Icon size increased slightly
        Label label = new Label(title);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        label.setTextFill(Color.web("#E5E7EB")); // Light text color for dark background

        navItem.getChildren().addAll(icon, label);

        // --- ANIMATION: Scale transition on hover (already present, styles updated) ---
        navItem.setOnMouseEntered(e -> {
            if (!title.equals(currentSelected)) {
                navItem.setStyle(hoverStyle);
            }
        });
        navItem.setOnMouseExited(e -> {
            if (!title.equals(currentSelected)) {
                navItem.setStyle(normalStyle);
            }
        });

        navItem.setOnMouseClicked(e -> highlightNavItem(title, true));
        navItemsMap.put(title, navItem);
        container.getChildren().add(navItem);
    }

    public void highlightNavItem(String title, boolean navigate) {
        if (title.equals(currentSelected)) return;

        // Reset all other navigation items
        navItemsMap.forEach((key, box) -> {
            box.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;");
            Label label = (Label) box.getChildren().get(1);
            label.setTextFill(Color.web("#E5E7EB")); // Reset to default light text
        });

        HBox selectedBox = navItemsMap.get(title);
        if (selectedBox != null) {
            // --- STYLE MODIFIED: Highlight style for selected item ---
            selectedBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
            Label label = (Label) selectedBox.getChildren().get(1);
            label.setTextFill(Color.web("#2563EB")); // Use primary blue from landing page for text
            currentSelected = title;

            if (navigate) {
                navigateTo(title);
            }
        }
    }

    protected void navigateTo(String pageName) {
        Node pageContent = null;
        baseDashBoard newPageInstance = null;

        switch (pageName.toLowerCase()) {
            case "dashboard": newPageInstance = new userdashboard(); break;
            case "order tracking": newPageInstance = new ordertracking(); break;
            case "shoplist": newPageInstance = new shoplist(); break;
            case "userp1": newPageInstance = new userp1(); break;
            case "subscription": newPageInstance = new SubscriptionPage(); break;
            case "profile": newPageInstance = new UserProfilePage(); break;
            case "userp2": newPageInstance = new userp2(); break;
            case "ai assistant": newPageInstance = new AiChatbotPage(); break;
            case "logout": newPageInstance = new LogoutPage(); break;
            default: pageContent = new Label("Page not found: " + pageName); break;
        }

        if (newPageInstance != null) {
            newPageInstance.setRoot(this.root);
            newPageInstance.primaryStage = this.primaryStage;
            pageContent = newPageInstance.createCenterContent();
        }

        // --- ANIMATION: Fade transition for page content (already present, logic is sound) ---
        if (pageContent != null) {
            Node currentCenter = root.getCenter();
            if (currentCenter != null) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(150), currentCenter);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                final Node newContent = pageContent; 
                fadeOut.setOnFinished(event -> {
                    newContent.setOpacity(0.0);
                    root.setCenter(newContent);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newContent);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play();
            } else {
                root.setCenter(pageContent);
            }
            highlightNavItem(pageName, false);
        }
    }

    protected Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 32)); // Increased font size
        // --- STYLE MODIFIED: White text with a drop shadow for readability ---
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.4)));
        label.setPadding(new Insets(5, 0, 20, 0)); // Adjusted padding
        return label;
    }

    // --- ANIMATION METHOD ADDED: From LandingPage, to slide in elements ---
    private ParallelTransition createSlideIn(Node node, Duration delay, double fromX, double fromY) {
        node.setOpacity(0);
        node.setTranslateX(fromX);
        node.setTranslateY(fromY);

        FadeTransition ft = new FadeTransition(Duration.millis(600), node);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(600), node);
        tt.setToX(0);
        tt.setToY(0);

        ParallelTransition transition = new ParallelTransition(node, ft, tt);
        transition.setDelay(delay);
        return transition;
    }

    // --- ANIMATION METHOD ADDED: To orchestrate the intro animation ---
    private void playIntroAnimations(Node header, Node sidebar, Node centerContent) {
        createSlideIn(header, Duration.ZERO, 0, -50).play();        // Header slides from top
        createSlideIn(sidebar, Duration.millis(100), -50, 0).play(); // Sidebar slides from left
        createSlideIn(centerContent, Duration.millis(200), 0, 30).play(); // Content fades/slides from bottom
    }
}