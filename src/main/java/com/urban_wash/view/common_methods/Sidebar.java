package com.urban_wash.view.common_methods;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;

public class Sidebar extends VBox {
    private final HashMap<String, HBox> navItemsMap = new HashMap<>();
    private String currentSelected = "";

    public Sidebar(String defaultSelectedItem) {
        this.setPrefWidth(220);
        this.setStyle("-fx-background-color: white;-fx-border-radius: 10");

        VBox navItems = new VBox(10);
        navItems.setPadding(new Insets(15));

        // Add nav items with icons from URLs
        addNavItem(navItems, "Dashboard", "https://img.icons8.com/ios-filled/50/000000/dashboard.png");
        addNavItem(navItems, "Orders", "https://img.icons8.com/ios-filled/50/000000/purchase-order.png");
        addNavItem(navItems, "Delivery", "https://img.icons8.com/ios-filled/50/000000/delivery.png");
        addNavItem(navItems, "Services", "https://img.icons8.com/ios-filled/50/000000/service.png");
        addNavItem(navItems, "Analytics", "https://img.icons8.com/ios-filled/50/000000/combo-chart.png");
        addNavItem(navItems, "Profile", "https://img.icons8.com/ios-filled/50/000000/user.png");

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(10, 10, 20, 20));
        userBox.setStyle("-fx-border-color: #eaeaea; -fx-border-width: 1 0 0 0;");

        ImageView avatar = new ImageView(new Image("https://img.icons8.com/ios-filled/50/000000/user-male-circle.png"));
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);

        VBox userInfo = new VBox(2);
        Label name = new Label("Siddesh Varma");
        name.setFont(Font.font(14));
        Label email = new Label("abc@gmail.com");
        email.setFont(Font.font(11));
        email.setTextFill(Color.GRAY);

        userInfo.getChildren().addAll(name, email);
        userBox.getChildren().addAll(avatar, userInfo);

        this.getChildren().addAll(navItems, spacer, userBox);

        // Set default selected
        if (navItemsMap.containsKey(defaultSelectedItem)) {
            highlightNavItem(defaultSelectedItem);
        }
    }

    private void addNavItem(VBox container, String title, String iconUrl) {
        HBox navItem = new HBox(10);
        navItem.setAlignment(Pos.CENTER_LEFT);
        navItem.setPadding(new Insets(8, 20, 8, 0));
        navItem.setPrefWidth(180);

        ImageView icon = new ImageView(new Image(iconUrl));
        icon.setFitWidth(18);
        icon.setFitHeight(18);

        Label label = new Label(title);
        label.setFont(Font.font("Arial", 13));
        label.setTextFill(Color.GRAY);

        navItem.getChildren().addAll(icon, label);

        navItem.setOnMouseClicked(e -> highlightNavItem(title));
        navItemsMap.put(title, navItem);
        container.getChildren().add(navItem);
    }

    private void highlightNavItem(String title) {
        // Reset all to default
        navItemsMap.forEach((key, box) -> {
            box.setStyle("");
            Label label = (Label) box.getChildren().get(1);
            label.setTextFill(Color.GRAY);
        });

        // Highlight selected
        HBox selectedBox = navItemsMap.get(title);
        if (selectedBox != null) {
            selectedBox.setStyle("-fx-background-color: rgba(94, 122, 232, 1); -fx-background-radius: 6;");
            Label label = (Label) selectedBox.getChildren().get(1);
            label.setTextFill(Color.WHITE);
            currentSelected = title;
        }
    }
}
