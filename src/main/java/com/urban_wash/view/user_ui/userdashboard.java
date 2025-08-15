package com.urban_wash.view.user_ui;

import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class userdashboard extends baseDashBoard {

    private final String primaryBlue = "#2563EB";
    private final String glowEffectColor = "rgba(139, 92, 246, 0.7)";
    // The wrapping Timeline is no longer needed.

    private record ServiceInfo(String title, String iconUrl) {}

    @Override
    public Node createCenterContent() {
        VBox content = new VBox(40);
        content.setPadding(new Insets(30, 0, 40, 0)); 
        content.setStyle("-fx-background-color: transparent;");

        Node welcomeSection = createWelcomeSection();
        Node searchBar = createSearchBar();
        Node servicesCarousel = createServicesCarousel();
        Node quickActionsSection = createQuickActionsSection();

        VBox paddedContent = new VBox(40);
        paddedContent.setPadding(new Insets(0, 50, 0, 50));
        paddedContent.getChildren().addAll(welcomeSection, searchBar);

        content.getChildren().addAll(
                paddedContent,
                servicesCarousel,
                quickActionsSection
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
                "-fx-background: transparent; -fx-background-color: transparent; " +
                ".viewport { -fx-background-color: transparent; } " +
                ".scroll-bar:vertical .track { -fx-background-color: transparent; }" +
                ".scroll-bar:vertical .thumb { -fx-background-color: rgba(255, 255, 255, 0.3); -fx-background-radius: 10; }"
        );

        playIntroAnimations(welcomeSection, searchBar, servicesCarousel, quickActionsSection);
        
        // *FIX:* The animation is now started within createServicesCarousel, so this block is removed.

        return content;
    }

    private Node createServicesCarousel() {
        List<ServiceInfo> services = List.of(
            new ServiceInfo("Quick Delivery", "https://img.icons8.com/ios-filled/50/ffffff/delivery.png"),
            new ServiceInfo("Subscriptions", "https://img.icons8.com/ios-filled/100/ffffff/service-bell.png"),
            new ServiceInfo("Dry Cleaning", "https://img.icons8.com/ios-filled/100/ffffff/hanger.png"),
            new ServiceInfo("Ironing", "https://img.icons8.com/ios-filled/100/ffffff/ironing.png"),
            new ServiceInfo("Wash & Fold", "https://img.icons8.com/ios-filled/100/ffffff/washing-machine.png")
        );

        final double cardWidth = 200;
        final double cardHeight = 180;
        final double spacing = 40;

        Group cardTrain = new Group();
        List<Node> allCards = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            for (ServiceInfo service : services) {
                Node card = createServiceCard(service, cardWidth, cardHeight);
                allCards.add(card);
            }
        }
        
        for (int i = 0; i < allCards.size(); i++) {
            Node card = allCards.get(i);
            card.setTranslateX(i * (cardWidth + spacing));
            cardTrain.getChildren().add(card);
        }

        double trainWidth = services.size() * (cardWidth + spacing);

        // *FIX:* Simplified animation logic. The TranslateTransition handles the infinite loop by itself.
        TranslateTransition transition = new TranslateTransition(Duration.seconds(25), cardTrain);
        transition.setFromX(0);
        transition.setToX(-trainWidth);
        transition.setInterpolator(Interpolator.LINEAR);
        transition.setCycleCount(Animation.INDEFINITE);
        
        // *FIX:* Start the animation directly.
        transition.play();
        
        Pane viewport = new Pane(cardTrain);
        viewport.setPrefHeight(cardHeight + 40);

        Rectangle clip = new Rectangle();
        viewport.setClip(clip);
        viewport.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            clip.setWidth(newBounds.getWidth());
            clip.setHeight(newBounds.getHeight());
        });
        
        viewport.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                addDynamicTransforms(allCards, cardTrain, newVal.doubleValue());
            }
        });

        return viewport;
    }
    
    private void addDynamicTransforms(List<Node> cards, Group train, double viewportWidth) {
        double center = viewportWidth / 2;
        
        train.translateXProperty().addListener((obs, oldVal, newVal) -> {
            for (Node card : cards) {
                double cardCenter = card.getTranslateX() + newVal.doubleValue() + card.getBoundsInParent().getWidth() / 2;
                double distanceFromCenter = cardCenter - center;
                double normalizedDistance = Math.max(-1.0, Math.min(1.0, distanceFromCenter / (center * 0.8)));

                double scale = 1.0 - 0.25 * Math.abs(normalizedDistance);
                double rotation = -45 * normalizedDistance;

                card.setScaleX(scale);
                card.setScaleY(scale);
                card.setRotate(rotation);
            }
        });
    }

    private Node createServiceCard(ServiceInfo info, double width, double height) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(width, height);
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;"
        );
        
        ImageView icon = new ImageView(new Image(info.iconUrl()));
        icon.setFitWidth(60);
        icon.setFitHeight(60);
        icon.setEffect(new Glow(0.5));

        Label titleLabel = new Label(info.title());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(icon, titleLabel);
        card.setRotationAxis(new Point3D(0, 1, 0));
        
        return card;
    }

    // --- UNCHANGED METHODS BELOW ---

    private VBox createWelcomeSection() {
        VBox welcomeBox = new VBox(5);
        Label welcomeTitle = new Label("Welcome back!");
        welcomeTitle.setFont(Font.font("System", FontWeight.BOLD, 36));
        welcomeTitle.setTextFill(Color.WHITE);
        welcomeTitle.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.5)));
        Label welcomeSubtitle = new Label("Ready to get your laundry done? Find your perfect laundry partner below.");
        welcomeSubtitle.setFont(Font.font("System", 18));
        welcomeSubtitle.setTextFill(Color.web("#E5E7EB"));
        welcomeBox.getChildren().addAll(welcomeTitle, welcomeSubtitle);
        return welcomeBox;
    }

    private HBox createSearchBar() {
        HBox searchContainer = new HBox();
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.15); -fx-background-radius: 12; -fx-border-color: rgba(255, 255, 255, 0.3); -fx-border-radius: 12; -fx-border-width: 1;");
        TextField searchField = new TextField();
        searchField.setPromptText("Enter an address, area, or pincode...");
        searchField.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-prompt-text-fill: #D1D5DB; -fx-font-size: 15px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.setPadding(new Insets(15, 20, 15, 20));
        Button findButton = new Button("Find Shops");
        findButton.setFont(Font.font("System", FontWeight.BOLD, 15));
        findButton.setStyle(
            "-fx-background-color: white; -fx-text-fill: " + primaryBlue + "; -fx-background-radius: 0 12 12 0;");
        findButton.setCursor(Cursor.HAND);
        findButton.setPadding(new Insets(15, 25, 15, 25));
        addHoverAnimation(findButton);

        // --- 🔴 START OF CHANGE ---
        // Modified the button's action to pass the search query.
        findButton.setOnAction(e -> {
            String searchQuery = searchField.getText().trim();
            // Create a new shoplist instance with the search query
            shoplist shopListPage = new shoplist(searchQuery);

            // Get the content Node from the new page
            Node shopListContent = shopListPage.createCenterContent();
            
            // Assuming the base dashboard's root is a BorderPane, we get it from the scene and set its center.
            // This is a common pattern for replacing views in JavaFX.
            BorderPane rootPane = (BorderPane) findButton.getScene().getRoot();
            rootPane.setCenter(shopListContent);

            // Manually highlight the nav item to keep the UI consistent, as the original navigateTo would have done.
            highlightNavItem("ShopList", false);
        });
        // --- 🔴 END OF CHANGE ---

        searchContainer.getChildren().addAll(searchField, findButton);
        return searchContainer;
    }

private VBox createQuickActionsSection() {
        VBox quickActionsBox = new VBox(20);
        quickActionsBox.setPadding(new Insets(0, 50, 0, 50));
        Label title = createSectionTitle("Quick Actions");
        HBox cardsContainer = new HBox(30);
        cardsContainer.setAlignment(Pos.CENTER);

        // --- UPDATED: Using icon URLs instead of emojis ---
        cardsContainer.getChildren().addAll(
            createActionCard("https://img.icons8.com/ios-filled/100/ffffff/delivery.png", "Track My Order", "Check the live status of your delivery."),
            createActionCard("https://img.icons8.com/ios-filled/100/ffffff/two-tickets.png", "Manage Subscriptions", "View, upgrade, or downgrade your plan."),
            createActionCard("https://img.icons8.com/ios-filled/100/ffffff/shopping-cart.png", "Place New Order", "Browse shops and schedule a pickup.")
        );
        quickActionsBox.getChildren().addAll(title, cardsContainer);
        return quickActionsBox;
    }

private Node createActionCard(String iconUrl, String title, String description) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setPrefSize(280, 200);
        card.setAlignment(Pos.TOP_LEFT);
        String baseStyle = "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 12; -fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-width: 1; -fx-border-radius: 12;";
        card.setStyle(baseStyle);
        card.setCursor(Cursor.HAND);
        DropShadow hoverShadow = new DropShadow(20, Color.web(glowEffectColor));
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(1.05);
        st.setToY(1.05);
        card.setOnMouseEntered(e -> {
            card.setEffect(hoverShadow);
            st.playFromStart();
        });
        card.setOnMouseExited(e -> {
            card.setEffect(null);
            st.stop();
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });

        // --- UPDATED: Using ImageView for the icon ---
        ImageView icon = new ImageView(new Image(iconUrl));
        icon.setFitWidth(40);
        icon.setFitHeight(40);
        addFloatingAnimation(icon); // Keep the cool floating animation

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", 14));
        descLabel.setTextFill(Color.web("#E5E7EB"));
        descLabel.setWrapText(true);
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Text actionLink = new Text("Go to " + title + " →");
        actionLink.setFont(Font.font("System", FontWeight.BOLD, 15));
        actionLink.setFill(Color.WHITE);
        actionLink.setUnderline(true);

        // Add the new ImageView 'icon' instead of the old Label
        card.getChildren().addAll(icon, titleLabel, descLabel, spacer, actionLink);
        
        card.setOnMouseClicked(e -> {
            switch (title) {
                case "Track My Order": navigateTo("Order Tracking"); break;
                case "Manage Subscriptions": navigateTo("Subscription"); break;
                case "Place New Order": navigateTo("ShopList"); break;
            }
        });
        return card;
    }
    
    @Override
    protected Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.4)));
        return title;
    }
    
    private void playIntroAnimations(Node... nodes) {
        long delay = 0;
        for (Node node : nodes) {
            animateNode(node, delay, 0, 20);
            delay += 100;
        }
    }

    private void animateNode(Node node, long delay, double fromX, double fromY) {
        node.setOpacity(0);
        node.setTranslateX(fromX);
        node.setTranslateY(fromY);
        FadeTransition ft = new FadeTransition(Duration.millis(600), node);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(600), node);
        tt.setToX(0);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.setDelay(Duration.millis(delay));
        pt.play();
    }
    
    private void addHoverAnimation(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
        st.setToX(1.05);
        st.setToY(1.05);
        node.setOnMouseEntered(e -> st.playFromStart());
        node.setOnMouseExited(e -> {
            st.stop();
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }

    private void addFloatingAnimation(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), node);
        tt.setByY(-8);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setAutoReverse(true);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }
}