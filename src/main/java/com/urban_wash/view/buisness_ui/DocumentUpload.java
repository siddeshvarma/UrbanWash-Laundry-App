package com.urban_wash.view.buisness_ui;

import java.io.File;

// Import the ReviewRegistration class
import com.urban_wash.view.common_methods.Header_new;
import com.urban_wash.view.common_methods.baseBuisness;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DocumentUpload extends baseBuisness {
    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.getChildren().add(new Header_new(stage).getHeader());

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: white;");

        // Title and subheading
        Label heading = new Label("Upload Your Documents For Verification");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setTextFill(Color.web("#222"));

        Label subheading = new Label(
                "To complete your UrbanWash registration, please upload the\n" +
                "necessary documents. This helps us verify your business and ensure a\n" +
                "smooth onboarding process."
        );
        subheading.setFont(Font.font("Arial", 14));
        subheading.setTextFill(Color.GRAY);
        subheading.setWrapText(true);
        subheading.setAlignment(Pos.CENTER);
        subheading.setTextAlignment(TextAlignment.CENTER);

        content.getChildren().addAll(heading, subheading);

        // Section title
        Label sectionTitle = new Label("Required Documents");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label sectionSub = new Label("Please upload the following documents. Ensure they are clear and valid.");
        sectionSub.setFont(Font.font("Arial", 13));
        sectionSub.setTextFill(Color.GRAY);

        VBox docHeader = new VBox(5, sectionTitle, sectionSub);

        // Document rows
        VBox documents = new VBox(15);
        documents.getChildren().addAll(
            createDocumentRow("Aadhaar Card", "Upload a clear scan of your Aadhaar card (front and back).", stage),
            createDocumentRow("PAN Card", "Provide a high-resolution image of your PAN card.", stage),
            createDocumentRow("Shop Registration Certificate", "Submit your official shop registration document.", stage),
            createDocumentRow("Shop Lease Agreement", "Upload a copy of your current shop lease agreement.", stage),
            createDocumentRow("Bank Statement", "Attach a recent bank statement for verification.", stage),
            createDocumentRow("Electricity Bill", "Latest electricity bill for address proof.", stage),
            createDocumentRow("GST Certificate (Optional)", "If applicable, upload your GST registration certificate.", stage)
        );

        // Footer button
        Button continueBtn = new Button("Continue to Review ➤");
        continueBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8;");
        
        // --- START OF CHANGE ---
        // Add the action to navigate to the ReviewRegistration page
        continueBtn.setOnAction(e -> {
            ReviewRegistration reviewPage = new ReviewRegistration();
            reviewPage.start(stage);
        });
        // --- END OF CHANGE ---

        HBox footer = new HBox(continueBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(20, 0, 0, 0));

        // Main white container with padding and border
        VBox container = new VBox(25);
        container.setPadding(new Insets(30, 80, 50, 80));
        container.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #cbd5e1; " +
                "-fx-border-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);"
        );
        container.getChildren().addAll(content, docHeader, documents, footer);

        // Center the container
        VBox outerRoot = new VBox(container);
        outerRoot.setStyle("-fx-background-color: rgb(197, 207, 245);");
        outerRoot.setPadding(new Insets(40));
        outerRoot.setAlignment(Pos.TOP_CENTER);

        // Make scrollable
        ScrollPane scrollPane = new ScrollPane(outerRoot);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: rgb(197, 207, 245);");
        scrollPane.setPadding(new Insets(10));

        root.getChildren().add(scrollPane);

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

        // 2. Create the Scene using the screen's width and height.
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        stage.setTitle("Document Verification");
        stage.setScene(scene);
        stage.setMaximized(true);

        stage.show();
    }

    private HBox createDocumentRow(String docTitleText, String docDescription, Stage stage) {
        VBox textBox = new VBox(4);
        Label docTitle = new Label(docTitleText);
        docTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label docDesc = new Label(docDescription);
        docDesc.setFont(Font.font("Arial", 12));
        docDesc.setTextFill(Color.GRAY);
        docDesc.setWrapText(true);

        Label status = new Label("● Pending Upload");
        status.setFont(Font.font("Arial", 12));
        status.setTextFill(Color.web("#9ca3af"));

        textBox.getChildren().addAll(docTitle, docDesc, status);

        Button actionBtn = new Button("Upload");
        actionBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        actionBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 14;");

        actionBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Upload " + docTitleText);
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upload Successful");
                alert.setHeaderText(null);
                alert.setContentText("Successfully uploaded: " + file.getName());
                alert.showAndWait();
            }
        });

        HBox.setHgrow(textBox, Priority.ALWAYS);
        HBox row = new HBox(10, textBox, actionBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #e5e7eb; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;"
        );

        return row;
    }

    public Node createCenterContent() {
        VBox vb = new VBox();
        return vb;
    }
}