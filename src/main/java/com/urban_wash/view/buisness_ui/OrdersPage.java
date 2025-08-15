package com.urban_wash.view.buisness_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.Model.Business;
import com.urban_wash.Model.Order;
import com.urban_wash.view.common_methods.baseBuisness;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class OrdersPage extends baseBuisness {

    private final ObservableList<Order> orderList = FXCollections.observableArrayList();
    private FilteredList<Order> filteredData;
    private TableView<Order> orderTable;
    private ToggleGroup filterGroup;
    private final FirestoreService firestoreService = new FirestoreService();

    private final ObservableList<String> orderStatuses = FXCollections.observableArrayList(
            "Picked Up", "Washing", "On Delivery", "Delivered", "Cancelled"
    );

    @Override
    public Node createCenterContent() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        // Style removed to let the parent's dark gradient show through
        // root.setStyle("-fx-background-color: #f9f9f9;");

        // Use the styled title from the parent class
        Label heading = createSectionTitle("Manage Orders");

        HBox filterButtons = createFilterButtons();

        Button newOrderBtn = new Button("+ New Order");
        // --- STYLE UPDATED for dark theme ---
        newOrderBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        newOrderBtn.setOnMouseEntered(e -> newOrderBtn.setStyle("-fx-background-color: #4338CA; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        newOrderBtn.setOnMouseExited(e -> newOrderBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        newOrderBtn.setOnAction(e -> showOrderForm(null));

        HBox topControls = new HBox(30, heading, new HBox());
        HBox.setHgrow(topControls.getChildren().get(1), Priority.ALWAYS); // Spacer
        topControls.getChildren().add(newOrderBtn);
        topControls.setAlignment(Pos.CENTER_LEFT);

        setupTable();
        loadOrders();

        // --- ANIMATION ---
        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), root);
        ft.setToValue(1.0);
        ft.play();

        root.getChildren().addAll(topControls, filterButtons, orderTable);
        return root;
    }

    private void setupTable() {
        orderTable = new TableView<>();
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(orderTable, Priority.ALWAYS);

        // --- STYLE UPDATED for dark theme "glass" effect ---
        orderTable.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-text-fill: white;"
        );

        // Define columns
        TableColumn<Order, String> col1 = new TableColumn<>("Order ID");
        col1.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        col1.setPrefWidth(100);

        TableColumn<Order, String> col3 = new TableColumn<>("Customer Name");
        col3.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        col3.setPrefWidth(150);

        TableColumn<Order, String> col4 = new TableColumn<>("Order Date");
        col4.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        col4.setPrefWidth(120);

        TableColumn<Order, String> col5 = new TableColumn<>("Status");
        col5.setCellValueFactory(new PropertyValueFactory<>("status"));
        col5.setPrefWidth(100);

        TableColumn<Order, String> col7 = new TableColumn<>("Amount");
        col7.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        col7.setPrefWidth(100);

        TableColumn<Order, Void> col8 = new TableColumn<>("Actions");
        col8.setCellFactory(tc -> new TableCell<>() {
            final HBox actionBox = new HBox(8);
            final Button editBtn = new Button("Edit");
            final Button deleteBtn = new Button("Delete");

            {
                // --- STYLE UPDATED for action buttons ---
                editBtn.setStyle("-fx-background-color: rgba(79, 70, 229, 0.8); -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 6; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: rgba(220, 53, 69, 0.8); -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 6; -fx-cursor: hand;");
                actionBox.getChildren().addAll(editBtn, deleteBtn);
                actionBox.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> showOrderForm(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDeleteOrder(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });
        col8.setPrefWidth(150);

        orderTable.getColumns().addAll(col1, col3, col4, col5, col7, col8);
        filteredData = new FilteredList<>(orderList, p -> true);
        orderTable.setItems(filteredData);
    }

    private HBox createFilterButtons() {
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);
        buttonsBox.setPadding(new Insets(0, 0, 20, 0));

        filterGroup = new ToggleGroup();
        ToggleButton allBtn = createFilterButton("All");
        allBtn.setSelected(true);
        buttonsBox.getChildren().add(allBtn);

        for (String status : orderStatuses) {
            buttonsBox.getChildren().add(createFilterButton(status));
        }

        filterGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                applyFilter(((ToggleButton) newToggle).getText());
            } else {
                if (oldToggle != null) oldToggle.setSelected(true);
            }
        });

        return buttonsBox;
    }

    private ToggleButton createFilterButton(String text) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(filterGroup);
        btn.setPadding(new Insets(8, 18, 8, 18));
        btn.getStyleClass().add("filter-button"); // For potential CSS styling

        // --- NEW FEATURE: Dynamic styling for hover and selection ---
        final String normalStyle = "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;";
        final String hoverStyle = "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;";
        final String selectedStyle = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #4F46E5, #A855F7); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;";

        // Listener to change style based on selection
        btn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                btn.setStyle(selectedStyle);
            } else {
                // When deselected, check if mouse is still over it to apply hover style
                btn.setStyle(btn.isHover() ? hoverStyle : normalStyle);
            }
        });

        // Listener to apply hover style only when not selected
        btn.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
            if (!btn.isSelected()) {
                btn.setStyle(isHovered ? hoverStyle : normalStyle);
            }
        });

        // Set initial style
        btn.setStyle(normalStyle);

        return btn;
    }

    private void loadOrders() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        orderTable.setPlaceholder(progressIndicator);

        Business currentBusiness = SessionManager.getInstance().getSelectedBusiness();
        if (currentBusiness == null || currentBusiness.getDocumentId() == null) {
            Label placeholderLabel = new Label("Could not identify business. Please log in again.");
            placeholderLabel.setTextFill(Color.WHITE);
            orderTable.setPlaceholder(placeholderLabel);
            return;
        }
        String businessId = currentBusiness.getDocumentId();

        new Thread(() -> {
            List<Order> fetchedOrders = firestoreService.fetchOrdersForBusiness(businessId);
            Platform.runLater(() -> {
                orderList.setAll(fetchedOrders);
                if (fetchedOrders.isEmpty()) {
                    Label placeholderLabel = new Label("No orders found. Click '+ New Order' to add one.");
                    placeholderLabel.setTextFill(Color.WHITE);
                    orderTable.setPlaceholder(placeholderLabel);
                }
            });
        }).start();
    }

    private void applyFilter(String status) {
        filteredData.setPredicate(order -> {
            if (status == null || status.equalsIgnoreCase("All")) return true;
            return status.equalsIgnoreCase(order.getStatus());
        });
    }

    private void showOrderForm(Order order) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT); // For custom shaped dialog
        dialog.setTitle(order == null ? "New Order" : "Edit Order");

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        // --- STYLE UPDATED for dark theme dialog ---
        form.setStyle("-fx-background-color: #1F2937; -fx-background-radius: 12; -fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-width: 1; -fx-border-radius: 12;");

        // Form fields
        TextField customerNameField = new TextField();
        DatePicker orderDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> statusBox = new ComboBox<>(orderStatuses);
        TextField amountField = new TextField();

        if (order != null) { // If editing, populate fields
            customerNameField.setText(order.getCustomerName());
            if (order.getOrderDate() != null) {
                try {
                    orderDatePicker.setValue(LocalDate.parse(order.getOrderDate(), DateTimeFormatter.ISO_LOCAL_DATE));
                } catch (DateTimeParseException ignored) {}
            }
            statusBox.setValue(order.getStatus());
            amountField.setText(String.valueOf(order.getTotalAmount()));
        } else {
            statusBox.setValue("Picked Up"); // Default for new order
        }
        
        // Style form fields for dark theme
        styleFormField(customerNameField, "Customer Name");
        styleFormField(orderDatePicker, "Order Date");
        styleFormField(statusBox, "Status");
        styleFormField(amountField, "Amount (e.g., 450.50)");


        Button saveBtn = new Button(order == null ? "Add Order" : "Save Changes");
        saveBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            handleSaveOrder(order, customerNameField.getText(), orderDatePicker.getValue(), statusBox.getValue(), amountField.getText(), dialog);
        });
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttonBar = new HBox(10, cancelBtn, saveBtn);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        form.getChildren().addAll(
                new Label("Customer Name:"), customerNameField,
                new Label("Order Date:"), orderDatePicker,
                new Label("Status:"), statusBox,
                new Label("Amount:"), amountField,
                new Region(), // Spacer
                buttonBar
        );
        // Make labels white
        form.getChildren().filtered(node -> node instanceof Label).forEach(node -> ((Label) node).setTextFill(Color.WHITE));

        Scene scene = new Scene(form, 400, 420);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.show();
    }

    private void handleSaveOrder(Order order, String customerName, LocalDate orderDate, String status, String amount, Stage dialog) {
        Business currentBusiness = SessionManager.getInstance().getSelectedBusiness();
        if (currentBusiness == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot process order. No business session found.");
            return;
        }

        Order orderToSave = (order == null) ? new Order() : order;
        orderToSave.setBusinessId(currentBusiness.getDocumentId());
        orderToSave.setCustomerName(customerName);
        if (orderDate != null) orderToSave.setOrderDate(orderDate.toString());
        orderToSave.setStatus(status);

        try {
            orderToSave.setTotalAmount(Double.parseDouble(amount));
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the amount.");
            return;
        }

        new Thread(() -> {
            String result = (order == null) ? firestoreService.placeOrder(orderToSave) : firestoreService.updateOrder(orderToSave);
            Platform.runLater(() -> {
                if (result.startsWith("Error:")) {
                    showAlert(Alert.AlertType.ERROR, "Operation Failed", result);
                } else {
                    dialog.close();
                    loadOrders(); // Refresh the table data
                }
            });
        }).start();
    }
    
    // Helper method to style form controls for dark theme
    private void styleFormField(Control control, String prompt) {
        String baseStyle = "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;";
        String focusedStyle = "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-border-color: #4F46E5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px; -fx-border-width: 1.5;";
        
        control.setStyle(baseStyle);
        if (control instanceof TextInputControl) {
            ((TextInputControl) control).setPromptText(prompt);
        } else if (control instanceof ComboBoxBase) {
            ((ComboBoxBase) control).setPromptText(prompt);
        }

        control.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            control.setStyle(isFocused ? focusedStyle : baseStyle);
        });
    }

    private void handleDeleteOrder(Order order) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Order");
        alert.setHeaderText("Are you sure you want to delete order " + order.getDocumentId() + "?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                String deleteResult = firestoreService.deleteOrder(order.getBusinessId(), order.getDocumentId());
                Platform.runLater(() -> {
                    if (deleteResult.startsWith("Error:")) {
                        showAlert(Alert.AlertType.ERROR, "Deletion Failed", deleteResult);
                    } else {
                        orderList.remove(order);
                    }
                });
            }).start();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}