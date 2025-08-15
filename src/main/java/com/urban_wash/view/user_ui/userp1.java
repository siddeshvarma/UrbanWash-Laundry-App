package com.urban_wash.view.user_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.Model.Business;
import com.urban_wash.Model.Order;
import com.urban_wash.Model.Service;
import com.urban_wash.Model.User;
import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class userp1 extends baseDashBoard {

    // --- UI COLOR SCHEME ---
    private final String COLOR_PRIMARY_ACCENT = "#818cf8";
    private final String COLOR_TEXT_PRIMARY = "#FFFFFF";
    private final String COLOR_TEXT_SECONDARY = "#E5E7EB";
    private final String COLOR_SURFACE_TRANSPARENT = "rgba(0, 0, 0, 0.2)";
    private final String COLOR_BORDER_SUBTLE = "rgba(255, 255, 255, 0.2)";

    private Label subtotalValueLabel, deliveryValueLabel, totalValueLabel;
    private VBox itemsBox;
    private Button confirmButton;
    private ToggleGroup deliveryToggleGroup, paymentToggleGroup;
    private GridPane addressGrid;

    private final FirestoreService firestoreService = new FirestoreService();
    private final Map<Service, Integer> orderQuantities = new HashMap<>();
    private double deliveryFee = 0.0;
    private double currentSubtotal = 0.0;
    private DeliveryOption selectedDeliveryOption;

    private record DeliveryOption(String title, String description, double price) {}

    @Override
    public Node createCenterContent() {
        HBox mainContent = new HBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: transparent;");

        Node detailsPanel = createDetailsPanel();
        Node summaryPanel = createOrderSummaryPanel();

        // The details panel should be scrollable
        ScrollPane scrollPane = new ScrollPane(detailsPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        HBox.setHgrow(scrollPane, Priority.ALWAYS); // Allow details panel to grow

        // The summary panel should have a fixed preferred width
        VBox summaryVBox = (VBox) summaryPanel;
        summaryVBox.setMinWidth(400);

        playIntroAnimations(scrollPane, summaryPanel);

        mainContent.getChildren().addAll(scrollPane, summaryVBox);

        return mainContent;
    }

    private Node createDetailsPanel() {
        VBox detailsContainer = new VBox(30);
        detailsContainer.setPadding(new Insets(30));

        confirmButton = new Button("Confirm and Pay");
        confirmButton.setMaxWidth(Double.MAX_VALUE);
        confirmButton.setPrefHeight(45);
        confirmButton.setFont(Font.font("System", FontWeight.BOLD, 16));
        confirmButton.setStyle("-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8;");
        confirmButton.setDisable(true);
        confirmButton.setOnAction(e -> placeOrderAction());
        
        SVGPath lockIcon = new SVGPath();
        lockIcon.setContent("M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z");
        lockIcon.setFill(Color.WHITE);
        confirmButton.setGraphic(lockIcon);

        detailsContainer.getChildren().addAll(
            createYourDetailsSection(),
            createDeliveryAddressSection(),
            createDeliveryModeSection(),
            createPaymentMethodSection(),
            confirmButton
        );
        return detailsContainer;
    }

    private Node createSectionHeader(String title, String iconPath) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitHeight(24);
        icon.setFitWidth(24);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        header.getChildren().addAll(icon, titleLabel);
        return header;
    }

    private Node createYourDetailsSection() {
        VBox container = new VBox(10);
        Node header = createSectionHeader("Your Details", "https://img.icons8.com/ios-filled/50/ffffff/user-male-circle.png");
        Label emailLabel = new Label("Loading...");
        emailLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        emailLabel.setPadding(new Insets(0, 0, 0, 34));
        
        String uid = SessionManager.getInstance().getCurrentUserUid();
        if (uid != null) {
            new Thread(() -> {
                User user = firestoreService.getUserProfile(uid);
                Platform.runLater(() -> {
                    if (user != null && user.getEmail() != null) emailLabel.setText(user.getEmail());
                    else emailLabel.setText("Could not load email.");
                });
            }).start();
        }
        container.getChildren().addAll(header, emailLabel);
        return container;
    }

    private Node createDeliveryAddressSection() {
        Node header = createSectionHeader("Delivery Address", "https://img.icons8.com/ios-filled/50/ffffff/address.png");
        addressGrid = createAddressForm();
        return new VBox(15, header, addressGrid);
    }

    private GridPane createAddressForm() {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(15);
        
        TextField firstNameField = new TextField(); firstNameField.setId("firstName");
        TextField lastNameField = new TextField(); lastNameField.setId("lastName");
        TextField address1Field = new TextField(); address1Field.setId("address1");
        TextField address2Field = new TextField(); address2Field.setId("address2");
        TextField cityField = new TextField(); cityField.setId("city");
        TextField stateField = new TextField(); stateField.setId("state");
        TextField pincodeField = new TextField(); pincodeField.setId("pincode");
        TextField phoneField = new TextField(); phoneField.setId("phone");
        ComboBox<String> countryBox = new ComboBox<>(); countryBox.setId("country");
        
        List.of(firstNameField, lastNameField, address1Field, address2Field, cityField, stateField, pincodeField, phoneField).forEach(this::styleTextField);
        styleComboBox(countryBox);

        countryBox.getItems().addAll("India", "United States", "United Kingdom");
        countryBox.setMaxWidth(Double.MAX_VALUE);

        grid.add(createThemedLabel("First Name"), 0, 0); grid.add(firstNameField, 0, 1);
        grid.add(createThemedLabel("Last Name"), 1, 0); grid.add(lastNameField, 1, 1);
        grid.add(createThemedLabel("Country / Region"), 0, 2, 2, 1); grid.add(countryBox, 0, 3, 2, 1);
        grid.add(createThemedLabel("Address Line 1"), 0, 4, 2, 1); grid.add(address1Field, 0, 5, 2, 1);
        grid.add(createThemedLabel("Address Line 2 (Optional)"), 0, 6, 2, 1); grid.add(address2Field, 0, 7, 2, 1);
        grid.add(createThemedLabel("City"), 0, 8); grid.add(cityField, 0, 9);
        grid.add(createThemedLabel("State"), 1, 8); grid.add(stateField, 1, 9);
        grid.add(createThemedLabel("Pincode"), 2, 8); grid.add(pincodeField, 2, 9);
        grid.add(createThemedLabel("Phone Number"), 0, 10, 2, 1); grid.add(phoneField, 0, 11, 2, 1);

        String uid = SessionManager.getInstance().getCurrentUserUid();
        if (uid != null) {
            new Thread(() -> {
                User user = firestoreService.getUserProfile(uid);
                Platform.runLater(() -> {
                    if (user != null) {
                        firstNameField.setText(user.getFirstName());
                        lastNameField.setText(user.getLastName());
                        countryBox.setValue(user.getCountry());
                        address1Field.setText(user.getAddress());
                        cityField.setText(user.getCity());
                        stateField.setText(user.getState());
                        pincodeField.setText(user.getZipCode());
                        phoneField.setText(user.getPhone());
                    }
                });
            }).start();
        }
        return grid;
    }
    
    private Label createThemedLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        return label;
    }

    private String getAddressFieldText(String id) {
        Node node = addressGrid.lookup("#" + id);
        return (node instanceof TextField) ? ((TextField) node).getText() : "";
    }

    private String getAddressComboBoxValue(String id) {
        Node node = addressGrid.lookup("#" + id);
        return (node instanceof ComboBox) ? ((ComboBox<String>) node).getValue() : "";
    }

    private Node createDeliveryModeSection() {
        Node header = createSectionHeader("Select Delivery Mode", "https://img.icons8.com/ios-filled/50/ffffff/delivery-time.png");
        deliveryToggleGroup = new ToggleGroup();
        VBox optionsBox = new VBox(15);
        
        DeliveryOption rapid = new DeliveryOption("Rapid Delivery", "Delivered within 5 hours", 200.00);
        DeliveryOption express = new DeliveryOption("Express Delivery", "Delivered within 24 hours", 100.00);
        DeliveryOption regular = new DeliveryOption("Regular Delivery", "Delivered within 2 days", 50.00);

        optionsBox.getChildren().addAll(
            createDeliveryModeOption(rapid, deliveryToggleGroup),
            createDeliveryModeOption(express, deliveryToggleGroup),
            createDeliveryModeOption(regular, deliveryToggleGroup)
        );
        
        deliveryToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updateToggleStyles(deliveryToggleGroup);
            if (newToggle != null) {
                this.selectedDeliveryOption = (DeliveryOption) newToggle.getUserData();
                this.deliveryFee = this.selectedDeliveryOption.price();
            } else {
                this.selectedDeliveryOption = null;
                this.deliveryFee = 0.0;
            }
            updateTotals();
            checkButtonState();
        });
        
        return new VBox(15, header, optionsBox);
    }

    private Node createPaymentMethodSection() {
        Node header = createSectionHeader("Payment Method", "https://img.icons8.com/ios-filled/50/ffffff/bank-card-back-side.png");
        Label subtitle = new Label("All transactions are secure and encrypted.");
        subtitle.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        subtitle.setPadding(new Insets(0, 0, 0, 34));
        
        paymentToggleGroup = new ToggleGroup();
        ToggleButton onlinePay = createPaymentOption("Pay Online", "Use Razorpay/Stripe for secure payment.");
        onlinePay.setUserData("Pay Online");
        ToggleButton cod = createPaymentOption("Cash on Delivery", "Pay with cash when your items are returned.");
        cod.setUserData("Cash on Delivery");
        
        onlinePay.setToggleGroup(paymentToggleGroup);
        cod.setToggleGroup(paymentToggleGroup);
        
        paymentToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            updateToggleStyles(paymentToggleGroup);
            checkButtonState();
        });
        
        return new VBox(10, header, subtitle, new VBox(10, onlinePay, cod));
    }

    private Node createOrderSummaryPanel() {
        VBox summaryContainer = new VBox(20);
        summaryContainer.setPadding(new Insets(30));
        summaryContainer.setStyle("-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-background-radius: 16; -fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-radius: 16; -fx-border-width: 1;");
        
        Label title = new Label("Order Summary");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        itemsBox = new VBox(15);
        
        VBox totalsBox = new VBox(10);
        totalsBox.setPadding(new Insets(15, 0, 15, 0));
        totalsBox.setStyle("-fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-width: 1 0 1 0;");
        subtotalValueLabel = new Label();
        deliveryValueLabel = new Label();
        totalValueLabel = new Label();
        totalsBox.getChildren().addAll(createTotalRow("Subtotal", subtotalValueLabel, false), createTotalRow("Delivery", deliveryValueLabel, false));
        
        Node grandTotalRow = createTotalRow("Total", totalValueLabel, true);
        
        summaryContainer.getChildren().addAll(title, itemsBox, totalsBox, grandTotalRow);
        
        loadServicesAndPopulateSummary();
        return summaryContainer;
    }

    private void loadServicesAndPopulateSummary() {
        itemsBox.getChildren().clear();
        Business selectedBusiness = SessionManager.getInstance().getSelectedBusiness();
        if (selectedBusiness == null) {
            itemsBox.getChildren().add(createThemedLabel("No shop selected."));
            return;
        }

        List<Service> services = selectedBusiness.getServices();
        if (services == null) {
            itemsBox.getChildren().add(createThemedLabel("This shop has no services available."));
            return;
        }

        List<Service> activeServices = services.stream()
            .filter(s -> s != null && "Active".equalsIgnoreCase(s.getStatus()))
            .collect(Collectors.toList());

        if (activeServices.isEmpty()) {
            itemsBox.getChildren().add(createThemedLabel("This shop has no active services."));
        } else {
            for (Service service : activeServices) {
                itemsBox.getChildren().add(createSummaryItem(service));
            }
        }
        updateTotals();
    }

    private Node createSummaryItem(Service service) {
        HBox itemRow = new HBox(10);
        itemRow.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        
        try {
            Image image = getImageFromBase64(service.getImageUrl());
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color: " + COLOR_BORDER_SUBTLE + "; -fx-background-radius: 4;");
        }
        
        Label nameLabel = createThemedLabel(service.getTitle() + " (" + service.getUnit() + ")");
        Label priceLabel = new Label("₹0.00");
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        priceLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        
        Button minusBtn = new Button("-");
        Label qtyLabel = createThemedLabel("0");
        Button plusBtn = new Button("+");
        
        String stepperStyle = "-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-font-weight: bold; -fx-text-fill: white;";
        minusBtn.setStyle(stepperStyle);
        plusBtn.setStyle(stepperStyle);
        
        HBox stepper = new HBox(5, minusBtn, qtyLabel, plusBtn);
        stepper.setAlignment(Pos.CENTER);

        minusBtn.setOnAction(e -> updateItemQuantity(service, qtyLabel, priceLabel, -1));
        plusBtn.setOnAction(e -> updateItemQuantity(service, qtyLabel, priceLabel, 1));

        VBox detailsVBox = new VBox(5, nameLabel, stepper);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        itemRow.getChildren().addAll(imageView, detailsVBox, spacer, priceLabel);
        return itemRow;
    }

    private void updateItemQuantity(Service service, Label qtyLabel, Label priceLabel, int change) {
        int currentQty = orderQuantities.getOrDefault(service, 0);
        int newQty = Math.max(0, currentQty + change);
        qtyLabel.setText(String.valueOf(newQty));
        
        double unitPrice = service.getPrice();
        
        priceLabel.setText(String.format("₹%.2f", unitPrice * newQty));
        
        if (newQty > 0) {
            orderQuantities.put(service, newQty);
        } else {
            orderQuantities.remove(service);
        }
        
        updateTotals();
        checkButtonState();
    }

    private void updateTotals() {
        this.currentSubtotal = orderQuantities.entrySet().stream()
            .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
            .sum();

        double total = this.currentSubtotal + this.deliveryFee;
        subtotalValueLabel.setText(String.format("₹%.2f", this.currentSubtotal));
        deliveryValueLabel.setText(String.format("₹%.2f", this.deliveryFee));
        totalValueLabel.setText(String.format("₹%.2f", total));
    }

    private void checkButtonState() {
        boolean itemsSelected = !orderQuantities.isEmpty();
        boolean deliverySelected = deliveryToggleGroup.getSelectedToggle() != null;
        boolean paymentSelected = paymentToggleGroup.getSelectedToggle() != null;
        confirmButton.setDisable(!itemsSelected || !deliverySelected || !paymentSelected);
    }
    
    private void placeOrderAction() {
        String userId = SessionManager.getInstance().getCurrentUserUid();
        Business business = SessionManager.getInstance().getSelectedBusiness();
        
        if (userId == null || business == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not place order. Session data is missing.");
            return;
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setBusinessId(business.getDocumentId());
        
        String firstName = getAddressFieldText("firstName");
        String lastName = getAddressFieldText("lastName");
        order.setCustomerName(firstName + " " + lastName);

        order.setOrderDate(Instant.now().toString());
        order.setStatus("Picked Up"); 
        
        order.setDeliveryMethod(selectedDeliveryOption.title());
        order.setDeliveryFee(selectedDeliveryOption.price());
        order.setPaymentMethod((String) paymentToggleGroup.getSelectedToggle().getUserData());
        
        double finalTotal = this.currentSubtotal + this.deliveryFee;
        order.setTotalAmount(finalTotal);
        
        SessionManager.getInstance().setFinalOrderTotal(finalTotal);

        Map<String, String> address = new HashMap<>();
        address.put("firstName", firstName);
        address.put("lastName", lastName);
        address.put("country", getAddressComboBoxValue("country"));
        address.put("address1", getAddressFieldText("address1"));
        address.put("address2", getAddressFieldText("address2"));
        address.put("city", getAddressFieldText("city"));
        address.put("state", getAddressFieldText("state"));
        address.put("pincode", getAddressFieldText("pincode"));
        address.put("phone", getAddressFieldText("phone"));
        order.setDeliveryAddress(address);
        
        List<Map<String, Object>> serviceList = new ArrayList<>();
        orderQuantities.forEach((service, quantity) -> {
            Map<String, Object> serviceMap = new HashMap<>();
            serviceMap.put("title", service.getTitle());
            serviceMap.put("quantity", quantity);
            serviceMap.put("price", service.getPrice());
            serviceList.add(serviceMap);
        });
        order.setOrderedServices(serviceList);

        confirmButton.setDisable(true);
        confirmButton.setText("Placing Order...");
        new Thread(() -> {
            String result = firestoreService.placeOrder(order);
            Platform.runLater(() -> {
                if (result.contains("Error:")) {
                    showAlert(Alert.AlertType.ERROR, "Order Failed", "There was an error placing your order: " + result);
                    confirmButton.setDisable(false);
                    confirmButton.setText("Confirm and Pay");
                } else {
                    // --- 🔴 FIXED: Manually navigate to the userp2 page ---
                    userp2 userP2Page = new userp2();
                    Node userP2Content = userP2Page.createCenterContent();
                    
                    // Get the main BorderPane from the current scene and switch its center content.
                    BorderPane rootPane = (BorderPane) confirmButton.getScene().getRoot();
                    rootPane.setCenter(userP2Content);
                }
            });
        }).start();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private ToggleButton createDeliveryModeOption(DeliveryOption option, ToggleGroup group) {
        ToggleButton toggleButton = createPaymentOption(option.title(), option.description());
        ((BorderPane)toggleButton.getGraphic()).setRight(new Label(String.format("₹%.2f", option.price())) {{ 
            setFont(Font.font("System", FontWeight.BOLD, 14));
            setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        }});
        toggleButton.setUserData(option);
        toggleButton.setToggleGroup(group);
        return toggleButton;
    }

    private ToggleButton createPaymentOption(String title, String description) {
        BorderPane contentPane = new BorderPane();
        contentPane.setPadding(new Insets(15));
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        Label descriptionLabel = new Label(description);
        descriptionLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        VBox textVBox = new VBox(5, titleLabel, descriptionLabel);
        contentPane.setLeft(textVBox);
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setGraphic(contentPane);
        toggleButton.setMaxWidth(Double.MAX_VALUE);
        toggleButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        return toggleButton;
    }

    private void updateToggleStyles(ToggleGroup group) {
        for (Toggle toggle : group.getToggles()) {
            Node graphic = ((ToggleButton) toggle).getGraphic();
            if (toggle.isSelected()) {
                graphic.setStyle("-fx-border-color: " + COLOR_PRIMARY_ACCENT + "; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-color: " + "rgba(129, 140, 248, 0.2)" + "; -fx-background-radius: 8;");
            } else {
                graphic.setStyle("-fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: transparent; -fx-background-radius: 8;");
            }
        }
    }

    private Node createTotalRow(String label, Label valueLabel, boolean isBold) {
        HBox row = new HBox();
        Label labelText = new Label(label);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        if (isBold) {
            labelText.setFont(Font.font("System", FontWeight.BOLD, 16));
            valueLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            labelText.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
            valueLabel.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
        } else {
            labelText.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            valueLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        }
        row.getChildren().addAll(labelText, spacer, valueLabel);
        return row;
    }
    
    private Image getImageFromBase64(String dataUri) {
        if (dataUri == null || !dataUri.contains(",")) {
            // Return a placeholder or null if the URI is invalid
            return null;
        }
        String base64Data = dataUri.substring(dataUri.indexOf(',') + 1);
        byte[] imageData = Base64.getDecoder().decode(base64Data);
        return new Image(new ByteArrayInputStream(imageData));
    }

    private void playIntroAnimations(Node... nodes) {
        long delay = 0;
        for (Node node : nodes) {
           animateNode(node, delay, 0, 30);
           delay += 100;
        }
    }

    private void animateNode(Node node, long delay, double fromX, double fromY) {
        node.setOpacity(0);
        node.setTranslateX(fromX);
        node.setTranslateY(fromY);
        FadeTransition ft = new FadeTransition(Duration.millis(600), node);
        ft.setToValue(1.0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(600), node);
        tt.setToX(0);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition pt = new ParallelTransition(node, ft, tt);
        pt.setDelay(Duration.millis(delay));
        pt.play();
    }
    
    private void styleTextField(TextInputControl field) {
        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_PRIMARY + "; -fx-font-size: 14px; -fx-border-color: transparent transparent " + COLOR_BORDER_SUBTLE + " transparent; -fx-border-width: 2;";
        String focusedStyle = "-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_PRIMARY + "; -fx-font-size: 14px; -fx-border-color: transparent transparent " + COLOR_PRIMARY_ACCENT + " transparent; -fx-border-width: 2;";
        field.setStyle(baseStyle);
        field.focusedProperty().addListener((obs, ov, nv) -> field.setStyle(nv ? focusedStyle : baseStyle));
    }
    
    private void styleComboBox(ComboBox<String> combo) {
        combo.setStyle("-fx-background-color: transparent; -fx-border-color: transparent transparent " + COLOR_BORDER_SUBTLE + " transparent; -fx-border-width: 2;");
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_PRIMARY + "; -fx-font-size: 14px;");
            }
        });
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-background-color: #2e1a5a; -fx-text-fill: " + COLOR_TEXT_SECONDARY + ";");
                setOnMouseEntered(e -> setStyle("-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: " + COLOR_TEXT_PRIMARY + ";"));
                setOnMouseExited(e -> setStyle("-fx-background-color: #2e1a5a; -fx-text-fill: " + COLOR_TEXT_SECONDARY + ";"));
            }
        });
    }
}