package com.urban_wash.view.user_ui;

import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class SubscriptionPage extends baseDashBoard {

    // --- 🎨 UI COLOR SCHEME (Consistent with Dashboard) ---
    private final String COLOR_PRIMARY_ACCENT = "#6366F1";
    private final String COLOR_TEXT_PRIMARY = "#FFFFFF";
    private final String COLOR_TEXT_SECONDARY = "#D1D5DB";
    private final String COLOR_SURFACE_TRANSPARENT = "rgba(255, 255, 255, 0.1)";
    private final String COLOR_BORDER_SUBTLE = "rgba(255, 255, 255, 0.2)";

    public SubscriptionPage() {
        // This constructor is intentionally left blank.
        // Sidebar highlighting is now handled within createLeftSidebar.
    }

    /**
     * Overrides the base method to create the main content for the subscription selection page.
     */
    @Override
    public Node createCenterContent() {
        VBox subscriptionContainer = new VBox(40);
        subscriptionContainer.setAlignment(Pos.TOP_CENTER);
        subscriptionContainer.setPadding(new Insets(50));
        subscriptionContainer.setStyle("-fx-background-color: transparent;");

        SubscriptionManager manager = SubscriptionManager.getInstance();
        String currentPlan = manager.getActivePlan();

        // --- Dynamically build the UI based on the current plan ---
        if (manager.hasActivePlan() && "Elite Plan".equals(currentPlan)) {
            subscriptionContainer.getChildren().add(createEliteConfirmation());
        } else {
            Label title = new Label();
            Label subtitle = new Label();
            VBox headerBox = new VBox(10, title, subtitle);
            headerBox.setAlignment(Pos.CENTER);

            HBox plansBox = new HBox(30);
            plansBox.setAlignment(Pos.CENTER);

            if (manager.hasActivePlan() && "Premium Plan".equals(currentPlan)) {
                title.setText("You are a Premium Plan Member");
                subtitle.setText("Upgrade to the Elite plan for the ultimate laundry experience.");
                VBox elitePlan = createPlanBox("Elite", "For ultimate convenience.", "₹899", new String[]{"✓ Unlimited Pickups", "✓ 12hr Express Delivery", "✓ Premium Stain Care"}, false, "Upgrade");
                plansBox.getChildren().add(elitePlan);
            } else if (manager.hasActivePlan() && "Basic Plan".equals(currentPlan)) {
                title.setText("You are a Basic Plan Member");
                subtitle.setText("Upgrade your plan to enjoy more benefits and faster service.");
                VBox premiumPlan = createPlanBox("Premium", "Best value for families.", "₹499", new String[]{"✓ 5 Pickups/month", "✓ 24hr Delivery", "✓ Basic Stain Treatment"}, true, "Upgrade");
                VBox elitePlan = createPlanBox("Elite", "For ultimate convenience.", "₹899", new String[]{"✓ Unlimited Pickups", "✓ 12hr Express Delivery", "✓ Premium Stain Care"}, false, "Upgrade");
                plansBox.getChildren().addAll(premiumPlan, elitePlan);
            } else {
                title.setText("Flexible Plans for Everyone");
                subtitle.setText("Choose the perfect plan that fits your lifestyle. No hidden fees, cancel anytime.");
                VBox basicPlan = createPlanBox("Basic", "For the essentials.", "₹299", new String[]{"✓ 2 Pickups/month", "✓ 48hr Delivery", "✓ Standard Wash"}, false, "Choose Plan");
                VBox premiumPlan = createPlanBox("Premium", "Best value for families.", "₹499", new String[]{"✓ 5 Pickups/month", "✓ 24hr Delivery", "✓ Basic Stain Treatment"}, true, "Choose Plan");
                VBox elitePlan = createPlanBox("Elite", "For ultimate convenience.", "₹899", new String[]{"✓ Unlimited Pickups", "✓ 12hr Express Delivery", "✓ Premium Stain Care"}, false, "Choose Plan");
                plansBox.getChildren().addAll(basicPlan, premiumPlan, elitePlan);
            }

            title.setFont(Font.font("System", FontWeight.BOLD, 32));
            title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
            subtitle.setFont(Font.font("System", 16));
            subtitle.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

            subscriptionContainer.getChildren().addAll(headerBox, plansBox);
        }

        return subscriptionContainer;
    }

    /**
     * Overrides the sidebar creation to provide a consistent navigation experience,
     * themed to match the application's dark style.
     * @return A VBox containing the themed sidebar.
     */
    @Override
    protected VBox createLeftSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: transparent;"); // Themed for dark background
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

        VBox userInfo = new VBox(2);
        Label name = new Label("Siddesh Varma");
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        name.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        Label email = new Label("siddesh@example.com");
        email.setFont(Font.font("System", 12));
        email.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        userInfo.getChildren().addAll(name, email);
        userBox.getChildren().addAll(avatar, userInfo);
        
        VBox bottomNav = new VBox(10);
        bottomNav.setPadding(new Insets(0, 15, 15, 15)); // Adjusted padding
        createNavButton(bottomNav, "Logout", "https://img.icons8.com/ios-filled/50/ffffff/logout-rounded-left.png");

        sidebar.getChildren().addAll(navItems, spacer, userBox, bottomNav);
        
        // Highlights the correct nav item when the page loads
        Platform.runLater(() -> highlightNavItem("Subscription", false));

        return sidebar;
    }


private VBox createPlanBox(String planName, String description, String price, String[] features, boolean isPopular, String buttonText) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.TOP_LEFT);
        box.setPrefWidth(320);
        box.setMinHeight(420);

        String popularStyle = isPopular ? "-fx-border-color: " + COLOR_PRIMARY_ACCENT + "; -fx-border-width: 2;" : "-fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-width: 1;";
        box.setStyle("-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-background-radius: 16; -fx-border-radius: 16; " + popularStyle);

        Label title = new Label(planName);
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", 14));
        descLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        HBox priceBox = new HBox(5);
        priceBox.setAlignment(Pos.BASELINE_CENTER);
        Label priceLabel = new Label(price);
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        priceLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        Label periodLabel = new Label("/month");
        periodLabel.setFont(Font.font("System", 14));
        periodLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        priceBox.getChildren().addAll(priceLabel, periodLabel);

        VBox featureList = new VBox(10);
        for (String feature : features) {
            Label featureLabel = new Label(feature);
            featureLabel.setFont(Font.font("System", 15));
            featureLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
            featureList.getChildren().add(featureLabel);
        }

        Button chooseButton = new Button(buttonText);
        chooseButton.setMaxWidth(Double.MAX_VALUE);
        
        // --- THIS IS THE KEY CHANGE ---
        chooseButton.setOnAction(e -> {
            // 1. Set the active plan
            SubscriptionManager.getInstance().setActivePlan(planName + " Plan");

            // 2. Create an instance of the success page, passing the plan name
            SubscriptionSuccessPage successPage = new SubscriptionSuccessPage(planName + " Plan");
            
            // 3. Manually switch the center content in the main dashboard view
            if (root != null) {
                // Pass the main stage and root pane references to the new page
                successPage.primaryStage = this.primaryStage;
                successPage.setRoot(this.root);
                
                // Set the new content
                root.setCenter(successPage.createCenterContent());
                // Set the new sidebar (which is null for the success page to hide it)
                root.setLeft(successPage.createLeftSidebar());
            }
        });

        if (isPopular) {
            stylePrimaryButton(chooseButton);
        } else {
            styleSecondaryButton(chooseButton);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        box.getChildren().addAll(title, descLabel, new Separator(), featureList, spacer, priceBox, chooseButton);
        return box;
    }

    private VBox createEliteConfirmation() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setStyle("-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-radius: 12;");
        box.setMaxWidth(600);

        ImageView icon = new ImageView(new Image("https://img.icons8.com/fluency/96/FFFFFF/trophy.png"));
        icon.setFitHeight(60);
        icon.setFitWidth(60);

        Label title = new Label("You are an Elite Plan Member!");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        Label subtitle = new Label("You have access to all our premium features. Thank you for being a valued member.");
        subtitle.setFont(Font.font("System", 16));
        subtitle.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(TextAlignment.CENTER);

        box.getChildren().addAll(icon, title, subtitle);
        return box;
    }

    private void stylePrimaryButton(Button btn) {
        btn.setStyle("-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: " + COLOR_TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-padding: 12 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
    }

    private void styleSecondaryButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_PRIMARY + "; -fx-border-color: " + COLOR_TEXT_PRIMARY + "; -fx-border-width: 1.5; -fx-font-weight: bold; -fx-padding: 12 20; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
    }
}