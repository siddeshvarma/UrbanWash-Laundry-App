
package com.urban_wash.view.common_methods;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.Desktop;
import java.net.URI;

public class Footer extends VBox {
    public static VBox getFooter() {
        VBox footer = new VBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(30, 20, 30, 20));
        footer.setStyle("-fx-background-color:#F9FAFB;");

        Label brand = new Label("UrbanWash");
        brand.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        brand.setTextFill(Color.web("#1f2937"));

        Label tagline = new Label("Stay updated with the latest offers");
        tagline.setFont(Font.font("Arial", 14));
        tagline.setTextFill(Color.GRAY);

        HBox emailBox = new HBox(10);
        emailBox.setAlignment(Pos.CENTER);
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(200);
        Button subscribe = new Button("Subscribe");
        subscribe.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 14; -fx-background-radius: 6;");
        emailBox.getChildren().addAll(emailField, subscribe);

        // Social media icons
        HBox socialIcons = new HBox(15);
        socialIcons.setAlignment(Pos.CENTER);

        ImageView insta = new ImageView(new Image("https://cdn-icons-png.flaticon.com/24/2111/2111463.png"));
        ImageView whatsapp = new ImageView(new Image("https://cdn-icons-png.flaticon.com/24/733/733585.png"));
        ImageView facebook = new ImageView(new Image("https://cdn-icons-png.flaticon.com/24/733/733547.png"));

        insta.setFitWidth(24); insta.setFitHeight(24);
        whatsapp.setFitWidth(24); whatsapp.setFitHeight(24);
        facebook.setFitWidth(24); facebook.setFitHeight(24);

        // Wrap in hyperlinks (open URLs in browser)
        Hyperlink instaLink = new Hyperlink("", insta);
        Hyperlink whatsappLink = new Hyperlink("", whatsapp);
        Hyperlink facebookLink = new Hyperlink("", facebook);

        instaLink.setOnAction(e -> openInBrowser("https://instagram.com/urbanwash"));
        whatsappLink.setOnAction(e -> openInBrowser("https://wa.me/1234567890"));
        facebookLink.setOnAction(e -> openInBrowser("https://facebook.com/urbanwash"));

        socialIcons.getChildren().addAll(instaLink, whatsappLink, facebookLink);

        Label copyright = new Label("© 2025 UrbanWash.");
        copyright.setFont(Font.font("Arial", 12));
        copyright.setTextFill(Color.GRAY);

        footer.getChildren().addAll(brand, tagline, emailBox, socialIcons, copyright);
        return footer;
    }

    private static void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
