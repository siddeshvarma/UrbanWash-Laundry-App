package com.urban_wash.view.common_methods;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import com.urban_wash.view.buisness_ui.DocumentUpload;
import com.urban_wash.view.buisness_ui.Home;
import com.urban_wash.view.buisness_ui.ProcessingScreen;
import com.urban_wash.view.buisness_ui.ReviewRegistration;
import com.urban_wash.view.common_methods.LandingPage;


public class Header_new {
    private final List<Hyperlink> navLinks = new ArrayList<>();
    private final Stage stage;
    private static String currentPage = "Register"; // shared page tracker

    public Header_new(Stage stage) {
        this.stage = stage;
    }

    public HBox getHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 40, 15, 40));
        header.setSpacing(25);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        // --- START OF CHANGES ---

        // Create an empty ImageView first
        ImageView logo = new ImageView();
        try {
            // Load the image from the project's resources folder
            Image logoImage = new Image(getClass().getResourceAsStream("/logo2.png"));
            logo.setImage(logoImage);
        } catch (Exception e) {
            System.err.println("Error: Could not load logo from resources (/logo2.png).");
        }
        
        logo.setFitWidth(50);
        logo.setFitHeight(50);
        logo.setPreserveRatio(true);

        Label brand = new Label("UrbanWash");
        brand.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        brand.setTextFill(Color.web("#1f2937"));

        HBox logoBox = new HBox(10, logo, brand);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setCursor(Cursor.HAND);

        logoBox.setOnMouseClicked(e -> {
            try {
                new LandingPage().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // --- END OF CHANGES ---

        // Navigation Links
        Hyperlink registerLink = createNavLink("Register");
        Hyperlink docUploadLink = createNavLink("Document Upload");
        Hyperlink reviewLink = createNavLink("Review");
        Hyperlink successLink = createNavLink("Success");

        // Actions
        registerLink.setOnAction(e -> {
            currentPage = "Register";
            setActiveLink(registerLink);
            try {
                new Home().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        docUploadLink.setOnAction(e -> {
            currentPage = "Document Upload";
            setActiveLink(docUploadLink);
            try {
                new DocumentUpload().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        reviewLink.setOnAction(e -> {
            currentPage = "Review";
            setActiveLink(reviewLink);
            try {
                new ReviewRegistration().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        successLink.setOnAction(e -> {
            currentPage = "Success";
            setActiveLink(successLink);
            try {
                new ProcessingScreen().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox nav = new HBox(15, registerLink, docUploadLink, reviewLink, successLink);
        nav.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logoBox, spacer, nav);

        // Apply highlight based on currentPage
        switch (currentPage) {
            case "Document Upload":
                setActiveLink(docUploadLink);
                break;
            case "Review":
                setActiveLink(reviewLink);
                break;
            case "Success":
                setActiveLink(successLink);
                break;
            default:
                setActiveLink(registerLink);
                break;
        }

        return header;
    }

    private Hyperlink createNavLink(String text) {
        Hyperlink link = new Hyperlink(text);
        link.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px; -fx-underline: false; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        navLinks.add(link);
        return link;
    }

    private void setActiveLink(Hyperlink activeLink) {
        for (Hyperlink link : navLinks) {
            if (link == activeLink) {
                link.setStyle("-fx-text-fill: #4f46e5; -fx-font-weight: bold; -fx-font-size: 14px; -fx-underline: false; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
            } else {
                link.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px; -fx-underline: false; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
            }
        }
    }
}