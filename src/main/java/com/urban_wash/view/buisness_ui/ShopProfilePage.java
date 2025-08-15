package com.urban_wash.view.buisness_ui;

import com.urban_wash.view.common_methods.baseBuisness;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * This is the new ShopProfilePage class that extends your baseBuisness framework.
 * It provides the shop profile management UI as the main content.
 */
public class ShopProfilePage extends baseBuisness {

    @Override
    protected Node createCenterContent() {
        HBox mainContent = new HBox(30);
        mainContent.setPadding(new Insets(30));

        // -------- Left: Shop Profile Form --------
        VBox leftCard = new VBox(20);
        leftCard.setPadding(new Insets(25));
        leftCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        HBox.setHgrow(leftCard, Priority.ALWAYS); // Allow left card to grow

        Label profileTitle = new Label("Shop Profile");
        profileTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Label profileSubtitle = new Label("Manage your shop’s public information and contact details.");
        profileSubtitle.setFont(Font.font("Arial", 12));
        profileSubtitle.setTextFill(Color.GRAY);

        // Form fields
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        TextField firstName = new TextField("Nehaa");
        TextField lastName = new TextField("Killedarpatil");
        TextField shopName = new TextField("Sparkle Clean Laundry");
        TextField email = new TextField("nehaa.kp23@sparkleclean.com");
        TextField phone = new TextField("+91 8767265910");
        TextField address = new TextField("123 Main Street, Anytown, USA");
        TextArea description = new TextArea("Sparkle Clean Laundry offers premium laundry services including wash & fold, dry cleaning, and specialized fabric care. We pride ourselves on eco-friendly practices and timely delivery.");
        description.setWrapText(true);
        description.setPrefRowCount(4);

        form.add(new Label("First Name"), 0, 0);
        form.add(firstName, 1, 0);
        form.add(new Label("Last Name"), 2, 0);
        form.add(lastName, 3, 0);
        form.add(new Label("Shop Name"), 0, 1);
        form.add(shopName, 1, 1, 3, 1);
        form.add(new Label("Email Address"), 0, 2);
        form.add(email, 1, 2, 3, 1);
        form.add(new Label("Phone Number"), 0, 3);
        form.add(phone, 1, 3, 3, 1);
        form.add(new Label("Shop Address"), 0, 4);
        form.add(address, 1, 4, 3, 1);
        form.add(new Label("Shop Description"), 0, 5);
        form.add(description, 1, 5, 3, 1);

        // Save button
        Button saveBtn = new Button("💾 Save Changes");
        saveBtn.setStyle("-fx-background-color: #6366F1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        saveBtn.setPrefWidth(140);

        VBox.setMargin(saveBtn, new Insets(10, 0, 0, 0));

        leftCard.getChildren().addAll(profileTitle, profileSubtitle, form, saveBtn);

        // -------- Right: Operational Settings --------
        VBox rightCard = new VBox(20);
        rightCard.setPadding(new Insets(25));
        rightCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        rightCard.setPrefWidth(350); // Give it a preferred width

        Label opSettings = new Label("Operational Settings");
        opSettings.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label opDesc = new Label("Configure your shop’s hours, service coverage, and notifications.");
        opDesc.setFont(Font.font("Arial", 12));
        opDesc.setTextFill(Color.GRAY);
        opDesc.setWrapText(true);

        Label hours = new Label("⏰ Shop Hours");
        hours.setFont(Font.font("Arial", 14));
        TextField shopHoursField = new TextField();
        shopHoursField.setPromptText("e.g., 9 AM - 9 PM");

        Label pincode = new Label("📍 Service Area Pincode");
        pincode.setFont(Font.font("Arial", 14));
        TextField pincodeField = new TextField();
        pincodeField.setPromptText("e.g., 411041");

        Button opSaveBtn = new Button("✅ Save Settings");
        opSaveBtn.setStyle("-fx-background-color: #6366F1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        opSaveBtn.setPrefWidth(160);
        opSaveBtn.setOnAction(e -> {
            String shopHours = shopHoursField.getText();
            String servicePincode = pincodeField.getText();
            System.out.println("Shop Hours: " + shopHours);
            System.out.println("Service Pincode: " + servicePincode);
            // TODO: Save to Firebase or DB logic here
        });

        rightCard.getChildren().addAll(opSettings, opDesc, new Separator(), hours, shopHoursField, new Separator(), pincode, pincodeField, new Separator(), opSaveBtn);

        mainContent.getChildren().addAll(leftCard, rightCard);

        return mainContent;
    }

  
}
