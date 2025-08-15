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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Home extends baseBuisness {

    private TextField fullNameField;
    private TextField shopNameField;
    private TextField shopAddressField;
    private TextField mobileNumberField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.setStyle("-fx-background-color:rgb(197, 207, 245);");

        root.getChildren().add(new Header_new(stage).getHeader());

        VBox scrollContent = new VBox();
        scrollContent.setSpacing(40);
        scrollContent.setPadding(new Insets(40, 80, 40, 80));
        scrollContent.setStyle("-fx-background-color:rgb(204, 204, 239);");

        // === Header Section (Aapka UI - Koi Badlaav Nahi) ===
        VBox headerText = new VBox();
        headerText.setAlignment(Pos.CENTER_LEFT);
        headerText.setSpacing(10);
        Label heading = new Label("Register Your\nLaundry Shop with\nUrbanWash");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setTextFill(Color.web("#222"));
        Label subheading = new Label("Streamline your business operations and connect with a growing\ncustomer base effortlessly.");
        subheading.setFont(Font.font("Arial", 13));
        subheading.setTextFill(Color.GREY);
        subheading.setWrapText(true);
        headerText.getChildren().addAll(heading, subheading);
        ImageView banner = new ImageView(new Image("https://imgs.search.brave.com/YwyTpKjBwk5NpE_a2WGlRv3_H2eKlrVXekQE3QhP_DI/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9jZG4u/dmVjdG9yc3RvY2su/Y29tL2kvcHJldmll/dy0xeC8yNi82OC9s/YXVuZHJ5LWNhcnRv/b24tYmFja2dyb3Vu/ZC12ZWN0b3ItMzg3/ODI2NjguanBn"));
        banner.setFitHeight(240);
        banner.setPreserveRatio(true);
        HBox header = new HBox(40, headerText, banner);
        header.setAlignment(Pos.CENTER);

        // === Form card (Aapka UI - Koi Badlaav Nahi) ===
        VBox formCard = new VBox(25);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 14; -fx-border-color: #ccc; -fx-border-radius: 14;");
        formCard.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Register Your Laundry Shop With UrbanWash");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        GridPane form = new GridPane();
        form.setHgap(20);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        fullNameField = new TextField();
    shopNameField = new TextField();
    shopAddressField = new TextField();
    mobileNumberField = new TextField();
    emailField = new TextField();
    passwordField = new PasswordField();
    confirmPasswordField = new PasswordField();

// Prompt text is added to guide the user
fullNameField.setPromptText("Enter your full name");
shopNameField.setPromptText("Enter your laundry shop's name");
shopAddressField.setPromptText("Enter the full shop address");
mobileNumberField.setPromptText("Enter your mobile number");
emailField.setPromptText("Enter your email address");
passwordField.setPromptText("Create a secure password");
confirmPasswordField.setPromptText("Confirm your password");

        form.add(new Label("Full Name"), 0, 0);
        form.add(fullNameField, 1, 0);
        form.add(new Label("Shop Name"), 0, 1);
        form.add(shopNameField, 1, 1);
        form.add(new Label("Shop Address"), 0, 2);
        form.add(shopAddressField, 1, 2);
        form.add(new Label("Mobile Number"), 0, 3);
        form.add(mobileNumberField, 1, 3);
        form.add(new Label("Email"), 0, 4);
        form.add(emailField, 1, 4);
        form.add(new Label("Password"), 0, 5);
        form.add(passwordField, 1, 5);
        form.add(new Label("Confirm Password"), 0, 6);
        form.add(confirmPasswordField, 1, 6);

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 8;");

        registerBtn.setOnAction(e -> handleRegistration(stage, registerBtn));

        VBox formWrapper = new VBox(20, formTitle, form, registerBtn);
        formWrapper.setAlignment(Pos.CENTER);
        formWrapper.setMaxWidth(600);
        formCard.getChildren().add(formWrapper);

        // === Why Register Section (Aapka UI - Koi Badlaav Nahi) ===
        Label whyLabel = new Label("Why Register with UrbanWash?");
        whyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        whyLabel.setAlignment(Pos.CENTER);
        HBox features = new HBox(30);
        features.setAlignment(Pos.CENTER);
        features.getChildren().addAll(
                createFeature("https://cdn-icons-png.flaticon.com/512/616/616489.png", "Boost Your Business", "Expand your reach to a wider customer base and increase your daily orders effortlessly."),
                createFeature("https://cdn-icons-png.flaticon.com/512/891/891462.png", "Seamless Management", "Access intuitive tools for order management, customer communication, and service tracking."),
                createFeature("https://cdn-icons-png.flaticon.com/512/747/747376.png", "Secure & Reliable", "Benefit from our robust platform designed with top-notch security and reliable service.")
        );

        scrollContent.getChildren().addAll(header, formCard, whyLabel, features, new Footer().getFooter());

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().add(scrollPane);

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

        // 2. Create the Scene using the screen's width and height.
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        stage.setTitle("UrbanWash Registration");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    
    private void handleRegistration(Stage stage, Button registerBtn) {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (fullNameField.getText().isEmpty() || shopNameField.getText().isEmpty() || emailField.getText().isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match. Please re-enter.");
            return;
        }

        // --- BUSINESS OBJECT ME PASSWORD PASS KAREIN ---
        Business businessData = new Business(
                shopNameField.getText(),
                shopAddressField.getText(),
                fullNameField.getText(),
                emailField.getText(),
                mobileNumberField.getText(),
                password // Password ko yahan pass karein
        );

        registerBtn.setDisable(true);
        registerBtn.setText("Registering...");

        new Thread(() -> {
            FirestoreService service = new FirestoreService();
            String result = service.registerBusiness(businessData);

            Platform.runLater(() -> {
                registerBtn.setDisable(false);
                registerBtn.setText("Register");

                // ✅ FIX: Check for a successful Firestore JSON response instead of "Success" string.
                if (result.contains("\"name\":") && result.contains("\"createTime\":")) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Business has been registered successfully!");
                    try {
                        new DocumentUpload().start(stage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Registration Failed", result);
                }
            });
        }).start();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private VBox createFeature(String iconUrl, String heading, String description) {
        ImageView icon = new ImageView(new Image(iconUrl));
        icon.setFitHeight(40);
        icon.setFitWidth(40);
        Label title = new Label(heading);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label desc = new Label(description);
        desc.setWrapText(true);
        desc.setTextFill(Color.GRAY);
        desc.setFont(Font.font("Arial", 13));
        VBox box = new VBox(10, icon, title, desc);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefWidth(260);
        box.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-background-radius: 10;");
        return box;
    }

    public Node createCenterContent(){
        VBox vb=new VBox();
        return vb;
    }
}