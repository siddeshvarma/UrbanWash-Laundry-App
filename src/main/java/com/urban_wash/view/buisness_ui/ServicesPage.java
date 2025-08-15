package com.urban_wash.view.buisness_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.Model.Business;
import com.urban_wash.Model.Service;
import com.urban_wash.view.common_methods.baseBuisness;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServicesPage extends baseBuisness {

    private GridPane servicesGrid;
    private final FirestoreService firestoreService = new FirestoreService();
    private Business currentBusiness;
    private List<Service> serviceList = new ArrayList<>();

    @Override
    public Node createCenterContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 12; -fx-padding: 25;");
        
        VBox textSection = new VBox(5);
        textSection.setBackground(Background.EMPTY);
        Label headingTop = new Label("Manage Your Services");
        headingTop.setFont(Font.font("System", FontWeight.BOLD, 24));
        headingTop.setTextFill(Color.WHITE);
        
        Label description = new Label("Effortlessly view, add, edit, and remove your laundry services.");
        description.setWrapText(true);
        description.setFont(Font.font("System", 14));
        description.setTextFill(Color.web("#E5E7EB"));
        textSection.getChildren().addAll(headingTop, description);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = new Button("➕ Add Service");
        addButton.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 20; -fx-cursor: hand;");
        addButton.setOnAction(e -> openServiceDialog(null));
        headerBox.getChildren().addAll(textSection, spacer, addButton);

        Label servicesHeading = createSectionTitle("Your Current Services");

        servicesGrid = new GridPane();
        servicesGrid.setHgap(30);
        servicesGrid.setVgap(30);
        
        content.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), content);
        ft.setToValue(1.0);
        ft.play();

        content.getChildren().addAll(headerBox, servicesHeading, servicesGrid);
        loadServicesFromFirestore();
        return content;
    }

    private void loadServicesFromFirestore() {
        servicesGrid.getChildren().clear();
        servicesGrid.add(new ProgressIndicator(), 0, 0);

        String uid = SessionManager.getInstance().getCurrentUserUid();
        if (uid == null || uid.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "Could not find user. Please log in again.");
            return;
        }

        new Thread(() -> {
            this.currentBusiness = firestoreService.fetchBusinessByOwnerUid(uid);
            if (this.currentBusiness != null) {
                this.serviceList = this.currentBusiness.getServices() != null ? this.currentBusiness.getServices() : new ArrayList<>();
                Platform.runLater(this::refreshGrid);
            } else {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.WARNING, "No Business Found", "No business profile found for your account.");
                    servicesGrid.getChildren().clear();
                });
            }
        }).start();
    }
    
    private void saveServicesToFirestore() {
        if (currentBusiness == null || currentBusiness.getDocumentId() == null) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Cannot save services. No business profile loaded.");
            return;
        }
        new Thread(() -> {
            String result = firestoreService.updateBusinessServices(currentBusiness.getDocumentId(), this.serviceList);
            Platform.runLater(() -> {
                if (result.startsWith("Error:")) {
                    showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not save services. " + result);
                }
            });
        }).start();
    }
    
    private VBox createServiceCard(Service service) {
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); -fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-radius: 12; -fx-background-radius: 12;");

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(service.getImageUrl(), 250, 150, false, true));
        } catch (Exception e) {
            imageView.setImage(new Image("https://via.placeholder.com/250x150.png?text=No+Image", 250, 150, false, true));
        }
        imageView.setSmooth(true);
        Rectangle clip = new Rectangle(250, 150);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        imageView.setClip(clip);

        Label titleLabel = new Label(service.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);

        Label statusLabel = new Label(service.getStatus());
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setPadding(new Insets(3, 9, 3, 9));
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusLabel.setStyle(service.getStatus().equalsIgnoreCase("Active") ? "-fx-background-color: #10B981; -fx-background-radius: 12;" : "-fx-background-color: #EF4444; -fx-background-radius: 12;");

        HBox titleAndStatus = new HBox(10, titleLabel, statusLabel);
        titleAndStatus.setAlignment(Pos.CENTER_LEFT);
        
        // --- FUNCTIONALITY UPDATED: Format the double price into a currency String ---
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        Label priceLabel = new Label(currencyFormat.format(service.getPrice()));
        priceLabel.setTextFill(Color.web("#C4B5FD"));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        
        Label unitLabel = new Label(service.getUnit());
        unitLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        unitLabel.setTextFill(Color.web("#E5E7EB"));
        HBox priceBox = new HBox(5, priceLabel, unitLabel);
        priceBox.setAlignment(Pos.BOTTOM_LEFT);

        Label descLabel = new Label(service.getDescription());
        descLabel.setWrapText(true);
        descLabel.setTextFill(Color.web("#E5E7EB"));
        descLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        descLabel.setMaxHeight(60);

        Region cardSpacer = new Region();
        VBox.setVgrow(cardSpacer, Priority.ALWAYS);

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        Button editBtn = new Button("Edit");
        Button delBtn = new Button("Delete");
        
        editBtn.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;");
        delBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.3); -fx-text-fill: #FCA5A5; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;");
        
        editBtn.setOnAction(e -> openServiceDialog(service));
        delBtn.setOnAction(e -> {
            serviceList.remove(service);
            refreshGrid();
            saveServicesToFirestore();
        });
        
        actionBox.getChildren().addAll(editBtn, delBtn);
        card.getChildren().addAll(imageView, titleAndStatus, priceBox, descLabel, cardSpacer, actionBox);
        return card;
    }

    private void openServiceDialog(Service serviceToEdit) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle(serviceToEdit == null ? "Add New Service" : "Edit Service");

        VBox dialogVBox = new VBox(15);
        dialogVBox.setPadding(new Insets(25));
        dialogVBox.setStyle("-fx-background-color: #1F2937; -fx-background-radius: 12; -fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-width: 1; -fx-border-radius: 12;");

        TextField titleField = new TextField();
        TextField imageField = new TextField();
        TextField priceField = new TextField();
        TextField unitField = new TextField();
        TextField descField = new TextField();
        ChoiceBox<String> statusChoice = new ChoiceBox<>();

        if (serviceToEdit != null) {
            titleField.setText(serviceToEdit.getTitle());
            // --- FUNCTIONALITY UPDATED: Convert double price to String for editing ---
            priceField.setText(String.valueOf(serviceToEdit.getPrice()));
            unitField.setText(serviceToEdit.getUnit());
            descField.setText(serviceToEdit.getDescription());
            imageField.setText(serviceToEdit.getImageUrl());
            statusChoice.setValue(serviceToEdit.getStatus());
        } else {
            statusChoice.setValue("Active");
        }
        
        styleFormField(titleField, "e.g., Dry Cleaning");
        styleFormField(imageField, "Enter a valid image URL");
        styleFormField(priceField, "e.g., 150.00");
        styleFormField(unitField, "e.g., per piece");
        styleFormField(descField, "A short service description.");
        styleFormField(statusChoice, null);
        statusChoice.getItems().addAll("Active", "Inactive");
        
        Button submitBtn = new Button(serviceToEdit == null ? "Add Service" : "Update Service");
        submitBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20; -fx-cursor: hand;");
        submitBtn.setOnAction(e -> {
            // --- FUNCTIONALITY UPDATED: Parse price String to double and handle errors ---
            double price;
            try {
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid number for the price (e.g., 150.00).");
                return;
            }

            Service service = (serviceToEdit == null) ? new Service() : serviceToEdit;
            service.setImageUrl(imageField.getText());
            service.setTitle(titleField.getText());
            service.setPrice(price); // Set the parsed double value
            service.setUnit(unitField.getText());
            service.setDescription(descField.getText());
            service.setStatus(statusChoice.getValue());
            
            if (serviceToEdit == null) serviceList.add(service);
            
            refreshGrid();
            saveServicesToFirestore();
            dialog.close();
        });

        dialogVBox.getChildren().addAll(
            createFormRow("Service Title:", titleField),
            createFormRow("Image URL:", imageField),
            createFormRow("Price:", priceField),
            createFormRow("Unit:", unitField),
            createFormRow("Description:", descField),
            createFormRow("Status:", statusChoice),
            submitBtn
        );
        
        Scene scene = new Scene(dialogVBox);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void refreshGrid() {
        servicesGrid.getChildren().clear();
        if (serviceList == null || serviceList.isEmpty()) {
            Label placeholder = new Label("No services found. Click 'Add Service' to get started.");
            placeholder.setTextFill(Color.web("#E5E7EB"));
            servicesGrid.add(placeholder, 0, 0, 4, 1);
            return;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            int col = i % 3;
            int row = i / 3;
            VBox card = createServiceCard(serviceList.get(i));
            
            card.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(400), card);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(i * 70));
            ft.play();
            
            servicesGrid.add(card, col, row);
        }
    }

    private Node createFormRow(String labelText, Node formField) {
        Label label = new Label(labelText);
        label.setTextFill(Color.web("#D1D5DB"));
        return new VBox(5, label, formField);
    }
    
    private void styleFormField(Control control, String prompt) {
        String baseStyle = "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px; -fx-padding: 8;";
        String focusedStyle = "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-border-color: #4F46E5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px; -fx-padding: 8; -fx-border-width: 1.5;";
        
        control.setStyle(baseStyle);
        if (control instanceof TextInputControl && prompt != null) {
            ((TextInputControl) control).setPromptText(prompt);
        }

        control.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            control.setStyle(isFocused ? focusedStyle : baseStyle);
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}