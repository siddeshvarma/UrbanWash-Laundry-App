package com.urban_wash.view.common_methods;

import com.google.common.base.Optional;
import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.Model.Business;
import com.urban_wash.view.buisness_ui.*;

// --- ANIMATION IMPORTS ADDED/UPDATED ---
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
// --- END OF IMPORTS ---

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;

public abstract class baseBuisness extends Application {

    private final HashMap<String, HBox> navItemsMap = new HashMap<>();
    private String currentSelected = "";
    private BorderPane root;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new BorderPane();

        Header header = new Header();
        // --- STYLE MODIFIED: Make header transparent to show gradient ---
        header.setStyle("-fx-background-color: transparent;");

        Node leftSidebar = createLeftSidebar(primaryStage);

        // Initial Page Load with Loading Indicator
        ProgressIndicator initialLoader = new ProgressIndicator();
        StackPane initialCenter = new StackPane(initialLoader);
        root.setCenter(initialCenter);

        root.setTop(header);
        root.setLeft(leftSidebar);

        // --- STYLE MODIFIED: Apply linear gradient background ---
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #2563EB, #8B5CF6);");

        // Fetch Business Data Asynchronously
        loadBusinessDataAndSetInitialPage();

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setTitle("Business Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        // --- ANIMATION ADDED: Play intro animations when stage is shown ---
        primaryStage.setOnShown(e -> playIntroAnimations(header, leftSidebar, initialCenter));
        primaryStage.show();
    }

    private void loadBusinessDataAndSetInitialPage() {
        String ownerUid = SessionManager.getInstance().getCurrentUserUid();
        if (ownerUid == null || ownerUid.isEmpty()) {
            root.setCenter(new Label("Error: No user logged in. Please restart the application."));
            return;
        }

        new Thread(() -> {
            FirestoreService firestoreService = new FirestoreService();
            Business business = firestoreService.fetchBusinessByOwnerUid(ownerUid);

            Platform.runLater(() -> {
                if (business != null) {
                    SessionManager.getInstance().setSelectedBusiness(business);
                    Node dashboardContent = new BusinessDashboard().createCenterContent();
                    // Set initial page with fade animation
                    navigateTo("Dashboard", primaryStage);
                    highlightNavItem("Dashboard");
                } else {
                    root.setCenter(new Label("Could not load business data for UID: " + ownerUid));
                }
            });
        }).start();
    }

    protected VBox createLeftSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(260); // Slightly wider for better padding
        // --- STYLE MODIFIED: "Glassmorphism" effect for sidebar ---
        sidebar.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 0 1 0 0;"
        );
        sidebar.setPadding(new Insets(15, 10, 15, 10));

        VBox navItems = new VBox(10);
        navItems.setPadding(new Insets(15));

        // --- ICONS MODIFIED: Switched to white icons for visibility ---
        addNavItem(navItems, "Dashboard", "https://img.icons8.com/material-sharp/50/ffffff/dashboard.png", stage);
        addNavItem(navItems, "Orders", "https://img.icons8.com/ios-filled/50/ffffff/purchase-order.png", stage);
        addNavItem(navItems, "Delivery", "https://img.icons8.com/ios-filled/50/ffffff/delivery.png", stage);
        addNavItem(navItems, "Services", "https://img.icons8.com/ios-filled/50/ffffff/service.png", stage);

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomNav = new VBox(10);
        bottomNav.setPadding(new Insets(15));
        addNavItem(bottomNav, "Logout", "https://img.icons8.com/ios-filled/50/ffffff/logout-rounded-left.png", stage);

        sidebar.getChildren().addAll(navItems, spacer, bottomNav);
        return sidebar;
    }

    private void addNavItem(VBox container, String title, String iconUrl, Stage stage) {
        HBox navItem = new HBox(15);
        navItem.setAlignment(Pos.CENTER_LEFT);
        navItem.setPadding(new Insets(10, 10, 10, 15));

        // --- STYLES MODIFIED: Adapted for the new theme ---
        String normalStyle = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(255, 255, 255, 0.15); -fx-background-radius: 8; -fx-cursor: hand;";

        navItem.setStyle(normalStyle);
        ImageView icon = new ImageView(new Image(iconUrl, 20, 20, true, true));
        Label label = new Label(title);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        label.setTextFill(Color.web("#E5E7EB")); // Light text for dark background

        navItem.getChildren().addAll(icon, label);

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

        navItem.setOnMouseClicked(e -> {
            highlightNavItem(title);
            navigateTo(title, stage);
        });

        navItemsMap.put(title, navItem);
        container.getChildren().add(navItem);
    }

    private void navigateTo(String pageKey, Stage stage) {
        Node centerContent = null;
        try {
            switch (pageKey) {
                case "Dashboard":
                    centerContent = new BusinessDashboard().createCenterContent();
                    break;
                case "Orders":
                    centerContent = new OrdersPage().createCenterContent();
                    break;
                case "Delivery":
                    centerContent = new DeliveryPage().createCenterContent();
                    break;
                case "Services":
                    centerContent = new ServicesPage().createCenterContent();
                    break;
                case "Logout":
    // Create a confirmation dialog
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Logout");
    alert.setHeaderText("You are about to log out.");
    alert.setContentText("Are you sure you want to proceed?");

    // --- STYLING ADDED FOR DARK THEME ---
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setStyle(
        "-fx-background-color: #1F2937;" + // Dark background
        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
        "-fx-border-width: 1;"
    );

    // Style the text content
    dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
    dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #D1D5DB; -fx-font-size: 14px;");

    // Style the buttons
    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    okButton.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");

    Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
    cancelButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
    // --- END OF STYLING ---

    // Show the dialog and wait for the user's response
    java.util.Optional<ButtonType> result = alert.showAndWait();

    // If the user clicks the OK button, then log out
    if (result.isPresent() && result.get() == ButtonType.OK) {
        SessionManager.getInstance().clearSession();
        new LandingPage().start(stage);
    }
    return; // This return is inside the case but outside the if, so it executes either way // Exit to avoid setting center content
            }
            // --- ANIMATION ADDED: Fade transition for page content ---
            if (centerContent != null) {
                Node currentCenter = root.getCenter();
                if (currentCenter != null) {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(150), currentCenter);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    final Node newContent = centerContent;
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
                    root.setCenter(centerContent);
                }
            }
        } catch (Exception ex) {
            System.err.println("Navigation failed for: " + pageKey);
            ex.printStackTrace();
        }
    }

    public void highlightNavItem(String title) {
        if (currentSelected.equals(title) && !title.isEmpty()) return;
        currentSelected = title;

        navItemsMap.forEach((key, box) -> {
            // Reset style for all items
            box.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;");
            ((Label) box.getChildren().get(1)).setTextFill(Color.web("#E5E7EB"));
        });

        HBox selectedBox = navItemsMap.get(title);
        if (selectedBox != null) {
            // --- STYLE MODIFIED: Highlight style for selected item ---
            selectedBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
            ((Label) selectedBox.getChildren().get(1)).setTextFill(Color.web("#2563EB")); // Primary blue
        }
    }

    // --- ANIMATION METHOD ADDED: To slide in elements ---
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
        createSlideIn(header, Duration.ZERO, 0, -50).play();      // Header slides from top
        createSlideIn(sidebar, Duration.millis(100), -50, 0).play(); // Sidebar slides from left
        createSlideIn(centerContent, Duration.millis(200), 0, 30).play(); // Content fades/slides from bottom
    }

    protected abstract Node createCenterContent();

    // Helper methods for content pages
    protected Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 32));
        // --- STYLE MODIFIED: White text with a drop shadow for readability ---
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.4)));
        label.setPadding(new Insets(5, 0, 20, 0));
        return label;
    }

    protected VBox createStatCard(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#4B5563")); // Dark gray text
        titleLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web("#111827")); // Nearly black text
        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(200);

        VBox card = new VBox(5, titleLabel, valueLabel);
        setupCardStyle(card);
        card.setPrefWidth(220);
        return card;
    }

    protected VBox createStatCard(String title, String value, String change, boolean isPositive) {
        VBox card = createStatCard(title, value);
        Label changeLabel = new Label(change);
        changeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        String color = isPositive ? "#10B981" : "#EF4444"; // Green or Red
        changeLabel.setStyle("-fx-text-fill: " + color + ";");
        card.getChildren().add(changeLabel);
        return card;
    }

    private void setupCardStyle(Pane card) {
        // This style contrasts well with the new dark background
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 1, 4);");
        card.setPrefWidth(250);
    }
}