package com.urban_wash.view.buisness_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Model.Business;
import com.urban_wash.view.common_methods.Footer;
import com.urban_wash.view.common_methods.Header_new;
import com.urban_wash.view.common_methods.baseBuisness;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class ReviewRegistration extends baseBuisness {

    // UI labels that will be updated with data from Firestore
    private Label fullNameValue;
    private Label shopNameValue;
    private Label shopAddressValue;
    private Label mobileNumberValue;
    private Label emailValue;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.setSpacing(20);
        root.setStyle("-fx-background-color: rgb(218, 222, 239);");
        root.setAlignment(Pos.TOP_CENTER);

        root.getChildren().add(new Header_new(stage).getHeader());

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(70));
        container.setMaxWidth(500);
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e5e7eb; " +
            "-fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 4);"
        );

        Label title = new Label("Review Your Registration");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label subtitle = new Label("Please review the details below before finalizing your laundry shop registration. Ensure all information is accurate to expedite your onboarding process.");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.GRAY);
        subtitle.setWrapText(true);

        container.getChildren().addAll(title, subtitle);

        // Initialize labels with "Loading..." text
        fullNameValue = new Label("Loading...");
        shopNameValue = new Label("Loading...");
        shopAddressValue = new Label("Loading...");
        mobileNumberValue = new Label("Loading...");
        emailValue = new Label("Loading...");

        // Fields section
        container.getChildren().addAll(
            createRow("Full Name", fullNameValue),
            createRow("Shop Name", shopNameValue),
            createRow("Shop Address", shopAddressValue),
            createRow("Mobile Number", mobileNumberValue),
            createRow("Email Address", emailValue)
        );

        // Uploads section (static for now)
        container.getChildren().addAll(
            createRow("KYC Documents", "Uploaded", true),
            createRow("Business Documents", "Uploaded", true)
        );

        Button registerBtn = new Button("⚡ Register Now");
        registerBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        registerBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 8;");
        
        // Navigate to the processing screen on button click
        registerBtn.setOnAction(e -> {
            ProcessingScreen processingScreen = new ProcessingScreen();
            try {
                processingScreen.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        HBox btnBox = new HBox(registerBtn);
        btnBox.setAlignment(Pos.CENTER);
        container.getChildren().add(btnBox);

        VBox scrollContent = new VBox(30);
        scrollContent.setAlignment(Pos.TOP_CENTER);
        scrollContent.setPadding(new Insets(10));
        scrollContent.getChildren().addAll(container, new Footer().getFooter());

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.getChildren().add(scrollPane);

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        stage.setScene(scene);
        stage.setTitle("Review Your Registration");
        stage.show();
        
        // Start the data fetching process
        fetchData();
    }

    /**
     * Fetches the latest business registration data from Firestore in a background thread.
     */
    private void fetchData() {
        new Thread(() -> {
            FirestoreService service = new FirestoreService();
            
            // --- FIX ---
            // The method is called on the 'service' instance, not the class itself.
            Business latestBusiness = service.fetchLatestBusiness();

            // Update the UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                if (latestBusiness != null) {
                    // Update UI labels with the fetched data
                    fullNameValue.setText(latestBusiness.getOwner());
                    shopNameValue.setText(latestBusiness.getShopName());
                    shopAddressValue.setText(latestBusiness.getAddress());
                    mobileNumberValue.setText(latestBusiness.getPhone());
                    emailValue.setText(latestBusiness.getEmail());
                } else {
                    // Handle the case where data could not be fetched
                    fullNameValue.setText("Could not fetch data.");
                    shopNameValue.setText("N/A");
                    shopAddressValue.setText("N/A");
                    mobileNumberValue.setText("N/A");
                    emailValue.setText("N/A");
                }
            });
        }).start();
    }

    /**
     * Helper method to create a styled row with a label and a value.
     * @param labelText The text for the static label.
     * @param valueLabel The Label object whose text will be updated.
     * @return A styled HBox containing the row.
     */
    private HBox createRow(String labelText, Label valueLabel) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setTextFill(Color.GRAY);

        valueLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        valueLabel.setTextFill(Color.web("#111827"));

        HBox row = new HBox(10, label, valueLabel);
        row.setPadding(new Insets(5, 0, 5, 0));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
    
    /**
     * Overloaded helper method for creating rows with static text values.
     * @param labelText The text for the static label.
     * @param valueText The text for the value.
     * @param linkStyle Whether to style the value as a link.
     * @return A styled HBox containing the row.
     */
    private HBox createRow(String labelText, String valueText, boolean linkStyle) {
        Label value = new Label(valueText);
        if (linkStyle) {
            value.setTextFill(Color.web("#2563eb"));
        }
        return createRow(labelText, value);
    }

    public Node createCenterContent(){
        VBox vb = new VBox();
        // This method seems to be a placeholder from a base class.
        // You can implement it if needed or remove it if unused.
        return vb;
    }
}
