package com.urban_wash.view.buisness_ui;



import com.urban_wash.view.common_methods.baseBuisness;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class accessdenied extends baseBuisness {

    public void start(Stage stage) {
        // Cross mark icon
        Label crossIcon = new Label("\u274C");
        crossIcon.setFont(Font.font("System", 58));
        crossIcon.setTextFill(Color.web("#DD283D"));
        crossIcon.setPadding(new Insets(0, 0, 10, 0));

        // Bold black heading
        Label title = new Label("Account Access Denied");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#111111"));
        title.setPadding(new Insets(0, 0, 16, 0));

        // First paragraph (plain, black, not bold)
        Label para1 = new Label(
            "We regret to inform you that your application for a Smart Laundry Manager shopkeeper account has been rejected."
        );
        para1.setFont(Font.font("System", 14));
        para1.setTextFill(Color.web("#222222"));
        para1.setWrapText(true);
        para1.setMaxWidth(320);
        para1.setPadding(new Insets(0, 0, 10, 0));

        // Second paragraph (plain, black, not bold; extra space before)
        Label para2 = new Label(
            "This decision is final based on our review process. If you believe this is an error or require further clarification, please contact our support team."
        );
        para2.setFont(Font.font("System", 14));
        para2.setTextFill(Color.web("#222222"));
        para2.setWrapText(true);
        para2.setMaxWidth(320);
        para2.setPadding(new Insets(0, 0, 18, 0));

        // Centered blue button
        Button contactBtn = new Button("Contact Support");
        contactBtn.setPrefWidth(180);
        contactBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        contactBtn.setStyle(
            "-fx-background-color: #2176FF;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 7;"
        );
        contactBtn.setOnAction(e -> {
            // Add your contact logic here
        });

        // Card layout: white rounded rectangle with drop shadow
        VBox card = new VBox(
            crossIcon,
            title,
            para1,
            para2,
            contactBtn
        );
        card.setAlignment(Pos.CENTER);
        card.setSpacing(0);
        card.setPadding(new Insets(36, 38, 36, 38));
        card.setBackground(new Background(
            new BackgroundFill(Color.WHITE, new CornerRadii(16), Insets.EMPTY)
        ));
        card.setMaxWidth(500);
        card.setEffect(new DropShadow(12, Color.rgb(80, 80, 80, 0.13)));

        // Centered card on soft gray background
        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: #FAFAFA;");
        root.setPrefSize(1200, 600);

        Scene scene = new Scene(root);
        stage.setTitle("Account Access Denied");
        stage.setScene(scene);
        stage.show();
    }

      public Node createCenterContent(){
        VBox vb=new VBox();
        return vb;
    }
}