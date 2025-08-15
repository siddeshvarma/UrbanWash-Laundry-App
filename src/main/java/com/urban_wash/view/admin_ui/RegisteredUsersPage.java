package com.urban_wash.view.admin_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.view.common_methods.baseBuisness;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class RegisteredUsersPage extends baseBuisness {

    // The FirestoreService will handle all backend communication.
    private final FirestoreService firestoreService = new FirestoreService();

    // --- SVG Paths for Icons ---
    private static final String USERS_ICON_SVG = "M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z";
    private static final String CHECK_ICON_SVG = "M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z";
    private static final String TRIAL_ICON_SVG = "M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z";
    @Override
    protected Node createCenterContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25, 30, 25, 30));
        content.setStyle("-fx-background-color: #F9FAFB;");

        Label title = new Label("User Management Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #111827;");

        HBox statsBox = new HBox(20,
            createStatCard("Total Users", "...", "#6366F1", USERS_ICON_SVG),
            createStatCard("Active Subscriptions", "...", "#10B981", CHECK_ICON_SVG),
            createStatCard("On Trial", "...", "#F59E0B", TRIAL_ICON_SVG)
        );

        VBox tableContainer = new VBox(15);
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20;");
        Label tableTitle = new Label("All Registered Users");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        TableView<User> table = new TableView<>();
        configureUserTable(table);
        loadUserData(table, statsBox);
        
        tableContainer.getChildren().addAll(tableTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(title, statsBox, tableContainer);
        return content;
    }
    
    /**
     * Fetches data from Firestore using the service and updates the table.
     */
    private void loadUserData(TableView<User> table, HBox statsBox) {
        table.setPlaceholder(new ProgressIndicator());

        new Thread(() -> {
            try {
                // Call the service to get users
                List<User> usersList = firestoreService.fetchUsers();

                Platform.runLater(() -> {
                    table.setItems(FXCollections.observableArrayList(usersList));
                    updateStatCards(statsBox, usersList);
                    if (usersList.isEmpty()) {
                        table.setPlaceholder(new Label("No users found in the database."));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> table.setPlaceholder(new Label("Error: Could not load user data.")));
            }
        }).start();
    }
    
    private void updateStatCards(HBox statsBox, List<User> users) {
        long activeCount = users.stream().filter(u -> "Active".equalsIgnoreCase(u.subscriptionStatus)).count();
        long trialCount = users.stream().filter(u -> "Trial".equalsIgnoreCase(u.subscriptionStatus)).count();
        
        ((Label)((VBox)((HBox)statsBox.getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).setText(String.valueOf(users.size()));
        ((Label)((VBox)((HBox)statsBox.getChildren().get(1)).getChildren().get(1)).getChildren().get(1)).setText(String.valueOf(activeCount));
        ((Label)((VBox)((HBox)statsBox.getChildren().get(2)).getChildren().get(1)).getChildren().get(1)).setText(String.valueOf(trialCount));
    }

    private void configureUserTable(TableView<User> table) {
        TableColumn<User, User> userCol = new TableColumn<>("User");
        userCol.setPrefWidth(250);
        userCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
        userCol.setCellFactory(column -> new UserCell());

        TableColumn<User, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().location));
        locationCol.setPrefWidth(150);

        TableColumn<User, String> joinedCol = new TableColumn<>("Date Joined");
        joinedCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dateJoined));
        joinedCol.setPrefWidth(120);

        TableColumn<User, String> statusCol = new TableColumn<>("Subscription Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().subscriptionStatus));
        statusCol.setPrefWidth(150);
        statusCol.setCellFactory(column -> new StatusCell());

        table.getColumns().addAll(List.of(userCol, locationCol, joinedCol, statusCol));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setStyle("-fx-selection-bar: #E5E7EB; -fx-selection-bar-non-focused: #E5E7EB;");

        table.widthProperty().addListener((obs, oldVal, newVal) -> {
            Pane header = (Pane) table.lookup(".column-header-background");
            if (header != null) {
                header.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-width: 0 0 1 0;");
            }
        });

        table.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setMinHeight(60);
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) row.setStyle("-fx-background-color: #F3F4F6;");
            });
            row.setOnMouseExited(event -> {
                if (!row.isEmpty()) row.setStyle("");
            });
            return row;
        });
    }

    private Node createStatCard(String title, String value, String color, String svg) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        Node icon = createIcon(svg, color, 24);
        VBox textContainer = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        titleLabel.setStyle("-fx-text-fill: #6B7280;");
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setStyle("-fx-text-fill: #111827;");
        textContainer.getChildren().addAll(titleLabel, valueLabel);
        card.getChildren().addAll(icon, textContainer);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

 
    private static class UserCell extends TableCell<User, User> {
        private final HBox box = new HBox(12);
        private final ImageView avatar = new ImageView();
        private final Label nameLabel = new Label();
        private final Label emailLabel = new Label();
        UserCell() {
            avatar.setFitHeight(40);
            avatar.setFitWidth(40);
            avatar.setClip(new Circle(20, 20, 20));
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            emailLabel.setTextFill(Color.GRAY);
            VBox nameBox = new VBox(2, nameLabel, emailLabel);
            box.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().addAll(avatar, nameBox);
        }
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            if (empty || user == null) {
                setGraphic(null);
            } else {
                try {
                    avatar.setImage(new Image(getClass().getResourceAsStream(user.avatarUrl)));
                } catch (Exception e) {
                    avatar.setImage(null);
                    avatar.setStyle("-fx-background-color: #D1D5DB;");
                }
                nameLabel.setText(user.name);
                emailLabel.setText(user.email);
                setGraphic(box);
            }
        }
    }
    
    private static class StatusCell extends TableCell<User, String> {
        private final Label statusLabel = new Label();
        StatusCell() {
            statusLabel.setPadding(new Insets(5, 12, 5, 12));
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            statusLabel.setStyle("-fx-background-radius: 12;");
        }
        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
                setGraphic(null);
            } else {
                statusLabel.setText(status);
                String style = switch (status) {
                    case "Active" -> "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;";
                    case "Inactive" -> "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;";
                    case "Trial" -> "-fx-background-color: #FEF3C7; -fx-text-fill: #92400E;";
                    default -> "-fx-background-color: #F3F4F6; -fx-text-fill: #4B5563;";
                };
                statusLabel.setStyle(style);
                setGraphic(statusLabel);
                setAlignment(Pos.CENTER);
            }
        }
    }

    private static Node createIcon(String svgPath, String color, int size) {
        SVGPath path = new SVGPath();
        path.setContent(svgPath);
        path.setFill(Color.web(color));
        StackPane iconPane = new StackPane(path);
        iconPane.setPrefSize(size, size);
        return iconPane;
    }

    // The User data model for this page
    public static class User {
        String name, email, location, dateJoined, subscriptionStatus, avatarUrl;
        public User(String name, String email, String location, String dateJoined, String subscriptionStatus, String avatarUrl) {
            this.name = name;
            this.email = email;
            this.location = location;
            this.dateJoined = dateJoined;
            this.subscriptionStatus = subscriptionStatus;
            this.avatarUrl = avatarUrl;
        }
    }
}