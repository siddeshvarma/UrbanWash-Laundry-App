package com.urban_wash.view.user_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.Model.Business;
import com.urban_wash.view.common_methods.baseDashBoard;
// --- ANIMATION IMPORTS ADDED ---
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
// --- END IMPORTS ---
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
// --- 🔴 ADD THIS IMPORT STATEMENT 🔴 ---
import com.urban_wash.view.user_ui.userp1;


import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class shoplist extends baseDashBoard {

    // --- Added for random image selection ---
    private final Random random = new Random();
    private final List<String> laundryImagePaths = List.of(
            "/Gemini_Generated_Image_41skws41skws41sk.png",
            "/Gemini_Generated_Image_cs1zymcs1zymcs1z.png",
            "/Gemini_Generated_Image_lq6ejclq6ejclq6e.png",
            "/Gemini_Generated_Image_w3jv23w3jv23w3jv.png"
    );

    private final String COLOR_PRIMARY_ACCENT = "#6366F1";
    private final String COLOR_TEXT_PRIMARY = "#FFFFFF";
    private final String COLOR_TEXT_SECONDARY = "#D1D5DB";
    private final String COLOR_SURFACE_TRANSPARENT = "rgba(255, 255, 255, 0.1)";
    private final String COLOR_BORDER_SUBTLE = "rgba(255, 255, 255, 0.2)";
    private final String COLOR_GLOW_EFFECT = "rgba(167, 139, 250, 0.7)";
    private final String COLOR_RATING_SUCCESS = "#16A34A";

    private final FirestoreService firestoreService = new FirestoreService();
    private String searchQuery;

    public shoplist() {
        this.searchQuery = null;
        Platform.runLater(() -> highlightNavItem("ShopList", false));
    }

    public shoplist(String searchQuery) {
        this.searchQuery = searchQuery;
        Platform.runLater(() -> highlightNavItem("ShopList", false));
    }

    @Override
    public Node createCenterContent() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: transparent;");

        Label title = new Label();
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        if (searchQuery != null && !searchQuery.isEmpty()) {
            title.setText("Showing Shops in \"" + searchQuery + "\"");
        } else {
            title.setText("Find Laundry Services Near You");
        }

        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(10));
        tilePane.setHgap(30);
        tilePane.setVgap(30);
        tilePane.setAlignment(Pos.TOP_LEFT);

        loadApprovedBusinesses(tilePane);

        ScrollPane scrollPane = new ScrollPane(tilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: transparent; -fx-background-color: transparent; "
        );
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainContainer.getChildren().addAll(title, scrollPane);
        return mainContainer;
    }

    private void loadApprovedBusinesses(TilePane tilePane) {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        tilePane.getChildren().add(progressIndicator);

        new Thread(() -> {
            List<Business> allBusinesses = firestoreService.fetchApprovedBusinesses();
            List<Business> filteredBusinesses;

            if (searchQuery != null && !searchQuery.isEmpty()) {
                String lowerCaseQuery = searchQuery.toLowerCase();
                filteredBusinesses = allBusinesses.stream()
                        .filter(business -> business.getAddress().toLowerCase().contains(lowerCaseQuery) ||
                                            business.getShopName().toLowerCase().contains(lowerCaseQuery))
                        .collect(Collectors.toList());
            } else {
                filteredBusinesses = allBusinesses;
            }

            Platform.runLater(() -> {
                tilePane.getChildren().clear();
                if (filteredBusinesses.isEmpty()) {
                    String messageText = (searchQuery != null && !searchQuery.isEmpty())
                            ? "No shops found matching your search."
                            : "No approved laundry services found at the moment.";
                    Label messageLabel = new Label(messageText);
                    messageLabel.setFont(Font.font("System", 16));
                    messageLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
                    tilePane.getChildren().add(messageLabel);
                } else {
                    // UPDATED LOOP to get index for staggered animation
                    for (int i = 0; i < filteredBusinesses.size(); i++) {
                        Business business = filteredBusinesses.get(i);
                        Node card = createLaundryCard(business);
                        tilePane.getChildren().add(card);
                        // Call the new animation method for each card
                        animateCardIn(card, i);
                    }
                }
            });
        }).start();
    }
    
    private Node createLaundryCard(Business business) {
        String randomImagePath = laundryImagePaths.get(random.nextInt(laundryImagePaths.size()));
        // Note: The dummy data for rating, price, and time is kept as is.
        return createLaundryCard(
            randomImagePath,
            business,
            "4.2", "₹200 for one", "30 min"
        );
    }

    private Node createLaundryCard(String imagePath, Business business, String rating, String price, String time) {
        VBox card = new VBox(10);
        card.setPrefWidth(350);
        card.setPadding(new Insets(15));
        card.setCursor(Cursor.HAND);
        
        String baseStyle = "-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-background-radius: 16; " +
                           "-fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-width: 1; -fx-border-radius: 16;";
        card.setStyle(baseStyle);

        DropShadow hoverShadow = new DropShadow(20, Color.web(COLOR_GLOW_EFFECT));
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(1.03);
        st.setToY(1.03);

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

        ImageView imageView = new ImageView();
        try {
            String imageUrl = getClass().getResource(imagePath).toExternalForm();
            imageView.setImage(new Image(imageUrl));
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color: " + COLOR_BORDER_SUBTLE + "; -fx-background-radius: 12;");
        }
        imageView.setFitWidth(320);
        imageView.setFitHeight(180);
        Rectangle clip = new Rectangle(320, 180);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        imageView.setClip(clip);
        
        VBox details = new VBox(5);
        details.setPadding(new Insets(10, 5, 5, 5));

        BorderPane namePane = new BorderPane();
        Label nameLabel = new Label(business.getShopName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        namePane.setLeft(nameLabel);
        namePane.setRight(createRatingBox(rating));

        Label addressLabel = new Label(business.getAddress());
        addressLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));

        HBox metaBox = new HBox(10);
        Label timeLabel = new Label("• " + time);
        timeLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        Label priceLabel = new Label("• " + price);
        priceLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        metaBox.getChildren().addAll(timeLabel, priceLabel);

        Button goToShop = new Button("Go to Shop");
        goToShop.setStyle("-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: " + COLOR_TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-background-radius: 10;");
        
        // --- 🔴 START OF CHANGE 🔴 ---
        goToShop.setOnAction(e -> {
            // This part remains the same: store the selected business in the session.
            SessionManager.getInstance().setSelectedBusiness(business);
            
            // This part is new: manually navigate to the userp1 page.
            userp1 userP1Page = new userp1();
            Node userP1Content = userP1Page.createCenterContent();
            
            // Get the root pane from the button's scene and set its center.
            BorderPane rootPane = (BorderPane) goToShop.getScene().getRoot();
            rootPane.setCenter(userP1Content);
            
            // The userp1 page should ideally handle highlighting its own nav item,
            // but we can leave this out for now as the navigation is the critical part.
        });
        // --- 🔴 END OF CHANGE 🔴 ---

        HBox buttonBox = new HBox(goToShop);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        details.getChildren().addAll(namePane, addressLabel, metaBox, buttonBox);
        card.getChildren().addAll(imageView, details);
        return card;
    }

    private HBox createRatingBox(String rating) {
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER);
        ratingBox.setStyle("-fx-background-color: " + COLOR_RATING_SUCCESS + "; -fx-background-radius: 5;");
        ratingBox.setPadding(new Insets(3, 6, 3, 6));

        Label ratingLabel = new Label(rating);
        ratingLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        ratingLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));

        SVGPath star = new SVGPath();
        star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
        star.setFill(Color.web(COLOR_TEXT_PRIMARY));
        star.setScaleX(0.5);
        star.setScaleY(0.5);

        ratingBox.getChildren().addAll(ratingLabel, star);
        return ratingBox;
    }
    
    // --- NEW ANIMATION METHOD ---
    private void animateCardIn(Node card, int index) {
        // Start the card smaller and invisible
        card.setScaleX(0.7);
        card.setScaleY(0.7);
        card.setOpacity(0);

        // Create transitions for scale and fade
        ScaleTransition st = new ScaleTransition(Duration.millis(300), card);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition ft = new FadeTransition(Duration.millis(300), card);
        ft.setToValue(1.0);

        // Run them in parallel for a smooth "pop-in" effect
        ParallelTransition pt = new ParallelTransition(card, st, ft);
        // Add a delay based on the card's position to stagger the animation
        pt.setDelay(Duration.millis(index * 70));
        pt.play();
    }
}