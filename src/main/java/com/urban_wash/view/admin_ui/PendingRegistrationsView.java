package com.urban_wash.view.admin_ui;

import com.urban_wash.Controller.FirebaseAuthController;
import com.urban_wash.view.common_methods.LandingPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PendingRegistrationsView extends Application {

    private static final String PROJECT_ID = "urbanwash-90d04";
    private static final String API_KEY = "AIzaSyAeeOZbVSV0uP9qMkgueO0xZ1Mky6xAPcQ";

    private BorderPane root;
    private Button businessButton, userButton, logoutButton;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #F4F6F8;");
        HBox header = createHeader(primaryStage);
        root.setTop(header);
        Node contentView = createBusinessContentView();
        root.setCenter(contentView);
        updateButtonStyles("business");
        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setTitle("UrbanWash Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHeader(Stage stage) {
        HBox headerLayout = new HBox();
        headerLayout.setPadding(new Insets(15, 40, 15, 40));
        headerLayout.setAlignment(Pos.CENTER_LEFT);
        headerLayout.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; " +
            "-fx-border-width: 0 0 1 0; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 10, 0, 0, 3);"
        );
        Label title = new Label("UrbanWash Admin");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#111827"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        businessButton = createNavButton("Business");
        userButton = createNavButton("User");
        logoutButton = createNavButton("Logout");
        businessButton.setOnAction(e -> {
            root.setCenter(createBusinessContentView());
            updateButtonStyles("business");
        });
        userButton.setOnAction(e -> {
            RegisteredUsersPage usersPage = new RegisteredUsersPage();
            Node usersView = usersPage.createCenterContent();
            root.setCenter(usersView);
            updateButtonStyles("user");
        });
        styleLogoutButton();
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText(null);
            styleAlertDialog(alert);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        new LandingPage().start(stage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });
        HBox navButtons = new HBox(15, businessButton, userButton, logoutButton);
        navButtons.setAlignment(Pos.CENTER);
        headerLayout.getChildren().addAll(title, spacer, navButtons);
        return headerLayout;
    }

    private Node createBusinessContentView() {
        VBox pageLayout = new VBox(20);
        pageLayout.setPadding(new Insets(40));
        Label title = new Label("Pending Store Registrations");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1F2937"));
        HBox titleContainer = new HBox(title);
        titleContainer.setPadding(new Insets(0, 0, 5, 0));
        VBox contentBox = new VBox();
        contentBox.setPadding(new Insets(30));
        contentBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 20, 0, 0, 5);");
        VBox.setVgrow(contentBox, Priority.ALWAYS);
        TableView<StoreRegistration> table = createRegistrationsTable();
        fetchBusinessData(table);
        contentBox.getChildren().add(table);
        pageLayout.getChildren().addAll(titleContainer, contentBox);
        return pageLayout;
    }

    private void fetchBusinessData(TableView<StoreRegistration> table) {
        ProgressIndicator pi = new ProgressIndicator();
        table.setPlaceholder(pi);
        new Thread(() -> {
            try {
                String endpoint = String.format(
                    "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business?key=%s",
                    PROJECT_ID, API_KEY
                );
                String jsonResponse = performGetRequest(endpoint);
                if (jsonResponse != null && !jsonResponse.startsWith("Error:")) {
                    ObservableList<StoreRegistration> registrations = parseBusinessData(jsonResponse);
                    Platform.runLater(() -> {
                        table.setItems(registrations);
                        if (registrations.isEmpty()) {
                            table.setPlaceholder(new Label("No registrations found."));
                        }
                    });
                } else {
                    Platform.runLater(() -> table.setPlaceholder(new Label("Failed to load data.")));
                    System.err.println("Failed to fetch data: " + jsonResponse);
                }
            } catch (Exception e) {
                Platform.runLater(() -> table.setPlaceholder(new Label("An error occurred.")));
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * ✅ --- CORRECTED METHOD ---
     * This method now safely parses the createTime field to prevent crashes.
     */
    private ObservableList<StoreRegistration> parseBusinessData(String json) {
        List<StoreRegistration> registrationList = new ArrayList<>();
        if (json == null || !json.contains("documents")) {
            return FXCollections.observableArrayList(registrationList);
        }
        String[] documents = json.split("\"name\": \"");
        for (int i = 1; i < documents.length; i++) {
            String docStr = documents[i];
            try {
                int pathEndIndex = docStr.indexOf("\"");
                if (pathEndIndex == -1) continue;
                String documentPath = docStr.substring(0, pathEndIndex);
                String shopName = getValue(docStr, "shopName", "stringValue");
                String owner = getValue(docStr, "owner", "stringValue");
                String email = getValue(docStr, "email", "stringValue");
                String location = getValue(docStr, "address", "stringValue");
                String password = getValue(docStr, "password", "stringValue");

                // --- FIX ---
                // Safely parse the createTime, providing a default if it's missing or invalid.
                String createTimeString = getValue(docStr, "createTime", "integerValue");
                long createTimeMillis = System.currentTimeMillis(); // Default to now
                if (!createTimeString.equals("N/A")) {
                    try {
                        createTimeMillis = Long.parseLong(createTimeString);
                    } catch (NumberFormatException e) {
                        System.err.println("Could not parse createTime value: " + createTimeString + ". Using default.");
                    }
                }
                
                Date date = new Date(createTimeMillis);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String registrationDate = sdf.format(date);
                String status = getValue(docStr, "status", "stringValue");
                if (status.equals("N/A")) {
                    status = "Pending Review";
                }
                StoreRegistration registration = new StoreRegistration(shopName, owner, registrationDate, email, location, status);
                registration.setDocumentId(documentPath);
                registration.setPassword(password);
                registrationList.add(registration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FXCollections.observableArrayList(registrationList);
    }
    
    // --- The rest of the file remains unchanged ---

    private String performGetRequest(String endpointUrl) throws Exception {
        URL url = new URL(endpointUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        InputStream inputStream = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        return (responseCode >= 200 && responseCode < 300) ? response.toString() : "Error: " + response.toString();
    }
    
    private String performDeleteRequest(String documentPath) throws Exception {
        String endpointUrl = String.format("https://firestore.googleapis.com/v1/%s?key=%s", documentPath, API_KEY);
        URL url = new URL(endpointUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        return readResponseMessage(conn);
    }

    private String performPatchRequest(String documentPath, String payload, String... fieldPaths) throws Exception {
        StringBuilder maskBuilder = new StringBuilder();
        for (String field : fieldPaths) {
            maskBuilder.append("&updateMask.fieldPaths=").append(field);
        }
        String endpointUrl = String.format("https://firestore.googleapis.com/v1/%s?key=%s%s",
            documentPath, API_KEY, maskBuilder.toString());
        URL url = new URL(endpointUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }
        return readResponseMessage(conn);
    }

    private String readResponseMessage(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            return "Success";
        }
        InputStream inputStream = conn.getErrorStream();
        if (inputStream == null) {
            return "Error: " + conn.getResponseMessage();
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        return "Error: " + response.toString();
    }
    
    private String getValue(String docStr, String key, String type) {
        String searchKey = "\"" + key + "\": {\"" + type + "\": \"";
        int startIndex = docStr.indexOf(searchKey);
        if (startIndex == -1) return "N/A";
        startIndex += searchKey.length();
        int endIndex = docStr.indexOf("\"", startIndex);
        return docStr.substring(startIndex, endIndex);
    }

    private TableView<StoreRegistration> createRegistrationsTable() {
        TableView<StoreRegistration> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle(getModernTableCSS());
        TableColumn<StoreRegistration, String> shopNameCol = new TableColumn<>("Shop Name");
        shopNameCol.setCellValueFactory(new PropertyValueFactory<>("shopName"));
        TableColumn<StoreRegistration, String> ownerCol = new TableColumn<>("Owner");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        TableColumn<StoreRegistration, String> regDateCol = new TableColumn<>("Registration Date");
        regDateCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        TableColumn<StoreRegistration, String> contactCol = new TableColumn<>("Contact Email");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        TableColumn<StoreRegistration, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<StoreRegistration, String> statusCol = createStatusColumn();
        TableColumn<StoreRegistration, Void> actionCol = createActionColumn(tableView);
        tableView.getColumns().addAll(shopNameCol, ownerCol, regDateCol, contactCol, locationCol, statusCol, actionCol);
        return tableView;
    }
    
    private TableColumn<StoreRegistration, String> createStatusColumn() {
        TableColumn<StoreRegistration, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(status);
                    statusLabel.setMinWidth(120);
                    statusLabel.setAlignment(Pos.CENTER);
                    statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 0; -fx-background-radius: 20;");
                    String color = switch (status.toLowerCase()) {
                        case "pending review" -> "#F59E0B";
                        case "approved" -> "#10B981";
                        case "rejected" -> "#EF4444";
                        default -> "#6B7280";
                    };
                    statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: " + color + ";");
                    setGraphic(statusLabel);
                }
            }
        });
        return statusCol;
    }

    private TableColumn<StoreRegistration, Void> createActionColumn(TableView<StoreRegistration> tableView) {
        TableColumn<StoreRegistration, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button detailsBtn = new Button("Details");
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox buttonsBox = new HBox(10, detailsBtn, approveBtn, rejectBtn);
            {
                buttonsBox.setAlignment(Pos.CENTER);
                styleActionButton(detailsBtn, "#6B7280", "#4B5563");
                styleActionButton(approveBtn, "#10B981", "#059669");
                styleActionButton(rejectBtn, "#EF4444", "#DC2626");
                detailsBtn.setOnAction(event -> {
                    StoreRegistration reg = tableView.getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registration Details");
                    alert.setHeaderText("Details for: " + reg.getShopName());
                    String content = String.format("Owner: %s\nContact Email: %s\nLocation: %s\nRegistration Date: %s\nCurrent Status: %s", reg.getOwner(), reg.getContactEmail(), reg.getLocation(), reg.getRegistrationDate(), reg.getStatus());
                    alert.setContentText(content);
                    styleAlertDialog(alert);
                    alert.showAndWait();
                });
                approveBtn.setOnAction(event -> {
                    StoreRegistration selectedReg = getTableView().getItems().get(getIndex());
                    if (selectedReg.getPassword().equals("N/A")) {
                        showAlert(Alert.AlertType.ERROR, "Approval Failed", "Password not found for this user.");
                        return;
                    }
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Approve Registration");
                    confirmAlert.setHeaderText("Create Auth Account & Link to Business for " + selectedReg.getContactEmail() + "?");
                    confirmAlert.setContentText("This will create a login account and save the user's ID to the business profile.");
                    styleAlertDialog(confirmAlert);
                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            new Thread(() -> {
                                FirebaseAuthController authController = new FirebaseAuthController();
                                String authResult = authController.signUpAndGetUid(selectedReg.getContactEmail(), selectedReg.getPassword());
                                if (authResult != null && !authResult.startsWith("Error:")) {
                                    String uid = authResult;
                                    String payload = String.format(
                                        "{\"fields\": {\"status\": {\"stringValue\": \"Approved\"}, \"ownerUid\": {\"stringValue\": \"%s\"}}}",
                                        uid
                                    );
                                    try {
                                        String firestoreResult = performPatchRequest(selectedReg.getDocumentId(), payload, "status", "ownerUid");
                                        Platform.runLater(() -> {
                                            if (firestoreResult.equals("Success")) {
                                                selectedReg.setStatus("Approved");
                                                getTableView().refresh();
                                                showAlert(Alert.AlertType.INFORMATION, "Success", "Business approved and user account created.");
                                            } else {
                                                showAlert(Alert.AlertType.ERROR, "Firestore Update Failed", firestoreResult);
                                            }
                                        });
                                    } catch (Exception e) {
                                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()));
                                    }
                                } else {
                                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Authentication Failed", authResult));
                                }
                            }).start();
                        }
                    });
                });
                rejectBtn.setOnAction(event -> {
                    StoreRegistration selectedReg = getTableView().getItems().get(getIndex());
                    if (selectedReg.getDocumentId() == null || selectedReg.getDocumentId().isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete: Document ID is missing.");
                        return;
                    }
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
                        "Are you sure you want to delete the registration for '" + selectedReg.getShopName() + "'?", 
                        ButtonType.YES, ButtonType.NO);
                    confirmAlert.setTitle("Delete Confirmation");
                    confirmAlert.setHeaderText("This action cannot be undone.");
                    styleAlertDialog(confirmAlert);
                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            new Thread(() -> {
                                try {
                                    String result = performDeleteRequest(selectedReg.getDocumentId());
                                    Platform.runLater(() -> {
                                        if (result.equals("Success")) {
                                            getTableView().getItems().remove(selectedReg);
                                            getTableView().refresh();
                                        } else {
                                            showAlert(Alert.AlertType.ERROR, "Delete Failed", result);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });
        return actionCol;
    }
    
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        button.setPadding(new Insets(8, 18, 8, 18));
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #374151; -fx-background-radius: 8;");
        return button;
    }

    private void styleLogoutButton() {
        String baseStyle = "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 14; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18;";
        String hoverStyle = "-fx-background-color: #FECACA; -fx-text-fill: #B91C1C; -fx-font-size: 14; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18;";
        logoutButton.setStyle(baseStyle);
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(hoverStyle));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(baseStyle));
    }

    private void styleActionButton(Button btn, String baseColor, String hoverColor) {
        String normalStyle = String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 6 16;", baseColor);
        String hoverStyle = String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 6 16;", hoverColor);
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
    }
    
    private void updateButtonStyles(String active) {
        String activeStyle = "-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 8 18;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #374151; -fx-font-weight: normal; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 8 18;";
        businessButton.setStyle(active.equals("business") ? activeStyle : inactiveStyle);
        userButton.setStyle(active.equals("user") ? activeStyle : inactiveStyle);
    }

    private void styleAlertDialog(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: System;");
        dialogPane.getButtonTypes().stream().map(dialogPane::lookupButton).forEach(btn -> {
            if (((Button) btn).isDefaultButton() || ((Labeled) btn).getText().equals("Yes") || ((Labeled) btn).getText().equals("OK")) {
                btn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold;");
            } else {
                btn.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #1F2937; -fx-font-weight: bold;");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleAlertDialog(alert);
        alert.showAndWait();
    }

    private String getModernTableCSS() {
        return """
            .table-view .column-header-background { -fx-background-color: #F9FAFB; }
            .table-view .column-header, .table-view .filler { -fx-background-color: transparent; -fx-border-color: #E5E7EB; -fx-border-width: 0 1px 1px 0; -fx-padding: 12; }
            .table-view .column-header .label { -fx-text-fill: #374151; -fx-font-weight: bold; -fx-font-size: 13px; }
            .table-view .cell { -fx-border-color: transparent #E5E7EB transparent transparent; -fx-padding: 20px 16px; }
            .table-view .table-row-cell:hover { -fx-background-color: #F9FAFB; }
            .table-view .table-row-cell:selected { -fx-background-color: #F3F4F6; }
            .table-view .table-row-cell:selected .text { -fx-fill: #111827; }
            .table-view .filler, .table-view .column-header.filler { -fx-background-color: white; -fx-border-color: transparent; }
        """;
    }

    public static class StoreRegistration {
        private String documentId;
        private String shopName, owner, registrationDate, contactEmail, location, status, password;
        public StoreRegistration(String s, String o, String rd, String ce, String l, String st) {
            this.shopName = s; this.owner = o; this.registrationDate = rd; this.contactEmail = ce; this.location = l; this.status = st;
        }
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getShopName() { return shopName; }
        public String getOwner() { return owner; }
        public String getRegistrationDate() { return registrationDate; }
        public String getContactEmail() { return contactEmail; }
        public String getLocation() { return location; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
