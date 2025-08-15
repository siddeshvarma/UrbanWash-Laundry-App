package com.urban_wash.view.common_methods;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Header extends HBox {

    public Header() {
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER_LEFT);
        
        // The HBox is transparent by default, showing the gradient behind it.

        // --- FIX: Corrected image loading ---
        // Load logo from the project's internal resources, not from a file path.
        // This is reliable and works even after building the project into a JAR file.
        Image logoImage = new Image(getClass().getResourceAsStream("/logo2.png"));
        
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(50);
        logoView.setFitWidth(50);
        logoView.setPreserveRatio(true);
        
        Label cmpName = new Label("URBANWASH");
        cmpName.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        cmpName.setTextFill(Color.WHITE);

        this.getChildren().addAll(logoView, cmpName);
    }
}