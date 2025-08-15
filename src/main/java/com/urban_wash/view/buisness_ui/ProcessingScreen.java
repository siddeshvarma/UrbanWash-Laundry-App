package com.urban_wash.view.buisness_ui;

import com.urban_wash.view.common_methods.Footer;
import com.urban_wash.view.common_methods.Header_new;
import com.urban_wash.view.common_methods.LandingPage;
import com.urban_wash.view.common_methods.baseBuisness;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button; // Added import for Button
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

// Assuming LandingPage is in the same package. If not, update the import.
// import com.urban_wash.view.buisness_ui.LandingPage;

public class ProcessingScreen extends baseBuisness {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: rgb(197, 207, 245);");

        // Top Header
        VBox topSection = new VBox();
        topSection.getChildren().add(new Header_new(stage).getHeader());

        // Center card
        VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(100, 0, 30, 0)); // spacing around card

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle(
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);"
        );

        // Loader animation
        Circle loader = new Circle(15, Color.TRANSPARENT);
        loader.setStroke(Color.web("#2563eb"));
        loader.setStrokeWidth(4);
        loader.setStyle("-fx-stroke-dash-array: 40;");

        RotateTransition rotate = new RotateTransition(Duration.seconds(1), loader);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.play();

        // Text
        Label processingText = new Label("Processing Registration...");
        processingText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        processingText.setTextFill(Color.web("#111827"));

        Label message = new Label("Your request is being processed. You may Visit later!!!");
        message.setFont(Font.font("Arial", 13));
        message.setTextFill(Color.GRAY);

        // --- START OF ADDED CODE ---

        // Back to Home Button
        Button backButton = new Button("🏠 Back to Home");
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        backButton.setStyle(
            "-fx-background-color: #e5e7eb; " +
            "-fx-text-fill: #1f2937; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 8;"
        );
        VBox.setMargin(backButton, new Insets(15, 0, 0, 0)); // Add some top margin

        // Set the action for the button to navigate to LandingPage
        backButton.setOnAction(e -> {
            LandingPage landingPage = new LandingPage();
            try {
                // Call the start method of LandingPage on the current stage
                landingPage.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // --- END OF ADDED CODE ---

        // Add original components and the new button to the card
        card.getChildren().addAll(loader, processingText, message, backButton);
        centerBox.getChildren().add(card);

        // Set regions in BorderPane
        root.setTop(topSection);
        root.setCenter(centerBox);
        root.setBottom(new Footer().getFooter()); // Footer pinned to bottom

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

        // 2. Create the Scene using the screen's width and height.
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        stage.setScene(scene);
        stage.setTitle("Processing");
        stage.show();
    }

      public Node createCenterContent(){
          VBox vb=new VBox();
          return vb;
      }

    
}