package com.urban_wash.view.user_ui;

import com.urban_wash.Controller.FirestoreService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.Model.User;
import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfilePage extends baseDashBoard {

    // --- 🎨 UI COLOR SCHEME ---
    private final String COLOR_PRIMARY_ACCENT = "#818cf8";
    private final String COLOR_TEXT_PRIMARY = "#FFFFFF";
    private final String COLOR_TEXT_SECONDARY = "#E5E7EB";
    private final String COLOR_HEADLINE = "#E0E7FF";
    private final String COLOR_SURFACE_TRANSPARENT = "rgba(0, 0, 0, 0.2)";
    private final String COLOR_BORDER_SUBTLE = "rgba(255, 255, 255, 0.2)";
    private final String COLOR_DESTRUCTIVE_RED = "#f87171";
    private final String COLOR_SUCCESS_GREEN = "#4ade80";

    private final FirestoreService firestoreService = new FirestoreService();
    private final Map<String, List<String>> countryStatesMap = new HashMap<>();
    private TextField firstNameField, lastNameField, cityField, zipField, addressField, phoneField;
    private ComboBox<String> countryCombo, stateCombo;
    private Label nameHeaderLabel;

    public UserProfilePage() {
        initializeCountryData();
        Platform.runLater(() -> highlightNavItem("Profile", false));
    }

    private void initializeCountryData() {
        countryStatesMap.put("India", List.of("Maharashtra", "Rajasthan", "Karnataka", "Delhi", "Tamil Nadu"));
        countryStatesMap.put("United States", List.of("California", "Texas", "New York", "Florida", "Illinois"));
        countryStatesMap.put("Germany", List.of("Bavaria", "Berlin", "Hesse", "Saxony", "Hamburg"));
    }

    @Override
    public Node createCenterContent() {
        HBox mainBody = new HBox(30);
        mainBody.setPadding(new Insets(20)); // Reduced main padding slightly
        mainBody.setStyle("-fx-background-color: transparent;");

        VBox formContainer = createProfileForm();
        HBox.setHgrow(formContainer, Priority.ALWAYS);

        VBox rightSidebar = createRightSidebar();

        mainBody.getChildren().addAll(formContainer, rightSidebar);
        
        loadUserData();
        
        playIntroAnimations(formContainer, rightSidebar);

        return mainBody;
    }

    private VBox createProfileForm() {
        // --- THIS IS THE KEY CHANGE ---
        // Reduced vertical spacing and padding to make the form more compact
        VBox form = new VBox(15); // Was 25
        form.setPadding(new Insets(20)); // Was 30
        
        form.setStyle(
            "-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + ";" +
            "-fx-border-color: " + COLOR_BORDER_SUBTLE + ";" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );

        Label title = new Label("Personal Information");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(COLOR_HEADLINE));
        Label subtitle = new Label("Update your photo and personal details here.");
        subtitle.setFont(Font.font("System", 15));
        subtitle.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        VBox headerBox = new VBox(5, title, subtitle);

        ImageView profileImageView = new ImageView(new Image("https://i.pravatar.cc/150?u=a042581f4e29026704d"));
        profileImageView.setFitWidth(80);
        profileImageView.setFitHeight(80);
        Circle clip = new Circle(40, 40, 40);
        profileImageView.setClip(clip);

        nameHeaderLabel = new Label("Loading...");
        String planNameText = SubscriptionManager.getInstance().hasActivePlan() 
            ? SubscriptionManager.getInstance().getActivePlan() + " Member" 
            : "No Active Plan";
        VBox nameAndRole = new VBox(5, nameHeaderLabel, new Label(planNameText));
        nameHeaderLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + COLOR_TEXT_PRIMARY + ";");
        nameAndRole.getChildren().get(1).setStyle("-fx-font-size: 13px; -fx-text-fill: " + COLOR_TEXT_SECONDARY + ";");

        Button uploadButton = new Button("Change");
        styleSecondaryButton(uploadButton);
        Button removeButton = new Button("Remove");
        styleDestructiveButton(removeButton);
        HBox photoControls = new HBox(10, uploadButton, removeButton);
        photoControls.setAlignment(Pos.CENTER_LEFT);
        HBox profilePhotoBox = new HBox(20, profileImageView, nameAndRole, photoControls);
        profilePhotoBox.setAlignment(Pos.CENTER_LEFT);

        firstNameField = new TextField();
        lastNameField = new TextField();
        HBox nameRow = createFormRow(createLabeledControl("First Name", firstNameField), createLabeledControl("Last Name", lastNameField));

        phoneField = new TextField();
        VBox phoneBox = createLabeledControl("Phone Number", phoneField);

        countryCombo = new ComboBox<>();
        countryCombo.setItems(FXCollections.observableArrayList(countryStatesMap.keySet()));
        VBox countryBox = createLabeledControl("Country / Region", countryCombo);

        stateCombo = new ComboBox<>();
        VBox stateBox = createLabeledControl("State / Province", stateCombo);
        countryCombo.setOnAction(e -> updateStates());
        
        cityField = new TextField();
        zipField = new TextField();
        HBox cityZipRow = createFormRow(createLabeledControl("City", cityField), createLabeledControl("Zip Code", zipField));

        addressField = new TextField();
        VBox addressBox = createLabeledControl("Address", addressField);

        Button saveButton = new Button("Save Changes");
        stylePrimaryButton(saveButton);
        saveButton.setOnAction(e -> saveUserData());
        Button cancelButton = new Button("Cancel");
        styleSecondaryButton(cancelButton);
        HBox buttonBox = new HBox(10, cancelButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        form.getChildren().addAll(headerBox, createStyledSeparator(), profilePhotoBox, createStyledSeparator(), nameRow, phoneBox, addressBox, countryBox, stateBox, cityZipRow, createStyledSeparator(), buttonBox);

        return form;
    }

    // --- Data handling methods (unchanged) ---
    private void loadUserData() {
        String uid = SessionManager.getInstance().getCurrentUserUid();
        if (uid == null || uid.isEmpty()) { return; }
        new Thread(() -> {
            User user = firestoreService.getUserProfile(uid);
            Platform.runLater(() -> {
                if (user != null) populateForm(user);
            });
        }).start();
    }

    private void populateForm(User user) {
        if (user.getName() != null) {
            nameHeaderLabel.setText(user.getName());
            String[] nameParts = user.getName().split(" ", 2);
            firstNameField.setText(nameParts.length > 0 ? nameParts[0] : "");
            lastNameField.setText(nameParts.length > 1 ? nameParts[1] : "");
        }
        phoneField.setText(user.getPhone());
        addressField.setText(user.getAddress());
        cityField.setText(user.getCity());
        zipField.setText(user.getZipCode());
        if (user.getCountry() != null && !user.getCountry().isEmpty()) {
            countryCombo.setValue(user.getCountry());
            updateStates();
            stateCombo.setValue(user.getState());
        }
    }

    private void saveUserData() {
        String uid = SessionManager.getInstance().getCurrentUserUid();
        if (uid == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user logged in.");
            return;
        }
        User updatedUser = new User();
        updatedUser.setUid(uid);
        updatedUser.setName(firstNameField.getText().trim() + " " + lastNameField.getText().trim());
        updatedUser.setPhone(phoneField.getText().trim());
        updatedUser.setAddress(addressField.getText().trim());
        updatedUser.setCity(cityField.getText().trim());
        updatedUser.setZipCode(zipField.getText().trim());
        updatedUser.setCountry(countryCombo.getValue());
        updatedUser.setState(stateCombo.getValue());
        new Thread(() -> {
            String result = firestoreService.updateUserProfile(updatedUser);
            Platform.runLater(() -> {
                if (!result.startsWith("Error:")) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
                    root.setLeft(createLeftSidebar());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update Failed", result);
                }
            });
        }).start();
    }

    private void updateStates() {
        String selectedCountry = countryCombo.getValue();
        if (selectedCountry != null) {
            List<String> states = countryStatesMap.get(selectedCountry);
            stateCombo.setItems(FXCollections.observableArrayList(states));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Right Sidebar Creation (Themed) ---
    private VBox createRightSidebar() {
        VBox sidebar = new VBox(25);
        sidebar.setPrefWidth(320);
        sidebar.setMinWidth(320);
        VBox subCard = createSubscriptionCard();
        VBox helpCard = createHelpCard();
        VBox deleteSection = createDeleteCard();
        sidebar.getChildren().addAll(subCard, helpCard, deleteSection);
        return sidebar;
    }

    private VBox createCardBase() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-radius: 12; -fx-border-width: 1;");
        return card;
    }

    private VBox createSubscriptionCard() {
        VBox subCard = createCardBase();
        SubscriptionManager manager = SubscriptionManager.getInstance();
        if (manager.hasActivePlan()) {
            Label subTitle = new Label("YOUR ACTIVE PLAN");
            subTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
            subTitle.setTextFill(Color.web(COLOR_PRIMARY_ACCENT));
            Label plan = new Label("💎 " + manager.getActivePlan());
            plan.setFont(Font.font("System", FontWeight.BOLD, 18));
            plan.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
            Label valid = new Label("Valid until: Dec 22, 2025");
            valid.setFont(Font.font("System", 13));
            valid.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
            ProgressBar subProgress = new ProgressBar(0.7);
            subProgress.setMaxWidth(Double.MAX_VALUE);
            subProgress.setStyle("-fx-accent: " + COLOR_PRIMARY_ACCENT + ";");
            Button manageButton = new Button("Manage Plan");
            stylePrimaryButton(manageButton);
            manageButton.setMaxWidth(Double.MAX_VALUE);
            manageButton.setOnAction(e -> navigateTo("Subscription"));
            subCard.getChildren().addAll(subTitle, plan, valid, subProgress, manageButton);
        } else {
            Label subTitle = new Label("NO ACTIVE PLAN");
            subTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
            subTitle.setTextFill(Color.web(COLOR_PRIMARY_ACCENT));
            Label infoText = new Label("Choose a plan to enjoy our premium benefits!");
            infoText.setWrapText(true);
            infoText.setFont(Font.font("System", 14));
            infoText.setTextFill(Color.web(COLOR_TEXT_PRIMARY));
            Button choosePlanButton = new Button("Choose a Plan");
            stylePrimaryButton(choosePlanButton);
            choosePlanButton.setMaxWidth(Double.MAX_VALUE);
            choosePlanButton.setOnAction(e -> navigateTo("Subscription"));
            subCard.getChildren().addAll(subTitle, infoText, choosePlanButton);
        }
        return subCard;
    }
    
    private VBox createHelpCard() {
        VBox helpCard = createCardBase();
        Label helpTitle = new Label("💡 Need Help?");
        helpTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        helpTitle.setTextFill(Color.web(COLOR_SUCCESS_GREEN));
        Label helpText = new Label("Our support team is here for you. Visit our help center or contact us directly.");
        helpText.setWrapText(true);
        helpText.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        Button contactButton = new Button("Contact Support");
        contactButton.setMaxWidth(Double.MAX_VALUE);
        contactButton.setStyle("-fx-background-color: " + COLOR_SUCCESS_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        helpCard.getChildren().addAll(helpTitle, helpText, contactButton);
        return helpCard;
    }

    private VBox createDeleteCard() {
        VBox deleteSection = createCardBase();
        Label deleteTitle = new Label("Delete Account");
        deleteTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        deleteTitle.setTextFill(Color.web(COLOR_DESTRUCTIVE_RED));
        Label deleteWarning = new Label("Once you delete your account, there is no going back. Please be certain.");
        deleteWarning.setWrapText(true);
        deleteWarning.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        Button deleteButton = new Button("Delete My Account");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        styleDestructiveButton(deleteButton);
        deleteSection.getChildren().addAll(deleteTitle, deleteWarning, deleteButton);
        return deleteSection;
    }

    private VBox createLabeledControl(String labelText, Node control) {
        Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        if (control instanceof ComboBox) {
            styleComboBox((ComboBox<String>) control);
        }
        if (control instanceof TextInputControl) {
            styleTextField((TextInputControl) control);
        }
        VBox box = new VBox(8, label, control);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }
    
    private HBox createFormRow(Node... children) {
        HBox row = new HBox(20);
        row.getChildren().addAll(children);
        return row;
    }
    
    private Separator createStyledSeparator() {
        Separator separator = new Separator();
        separator.setStyle("-fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-width: 1 0 0 0;");
        return separator;
    }
    
    private void styleTextField(TextInputControl field) {
        field.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + COLOR_TEXT_PRIMARY + ";" +
            "-fx-font-size: 15px;" +
            "-fx-border-color: transparent transparent " + COLOR_BORDER_SUBTLE + " transparent;" +
            "-fx-border-width: 2;" +
            "-fx-padding: 5 0;"
        );
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            String borderColor = newVal ? COLOR_PRIMARY_ACCENT : COLOR_BORDER_SUBTLE;
            field.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + COLOR_TEXT_PRIMARY + ";" +
                "-fx-font-size: 15px;" +
                "-fx-border-color: transparent transparent " + borderColor + " transparent;" +
                "-fx-border-width: 2;" +
                "-fx-padding: 5 0;"
            );
        });
    }

    private void styleComboBox(ComboBox<String> combo) {
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-font-size: 15px;" +
            "-fx-border-color: transparent transparent " + COLOR_BORDER_SUBTLE + " transparent;" +
            "-fx-border-width: 2;"
        );
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #2e1a5a;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #2e1a5a; -fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-padding: 8 12;");
                    setOnMouseEntered(e -> setStyle("-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: " + COLOR_TEXT_PRIMARY + "; -fx-padding: 8 12;"));
                    setOnMouseExited(e -> setStyle("-fx-background-color: #2e1a5a; -fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-padding: 8 12;"));
                }
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_PRIMARY + ";");
                }
            }
        });
    }

    private void stylePrimaryButton(Button btn) {
        String baseStyle = "-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;";
        String hoverStyle = "-fx-background-color: #6d28d9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
    }
    
    private void styleSecondaryButton(Button btn) {
        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-width: 1.5; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-text-fill: " + COLOR_TEXT_PRIMARY + "; -fx-border-color: " + COLOR_TEXT_SECONDARY + "; -fx-border-width: 1.5; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
    }

    private void styleDestructiveButton(Button btn) {
        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: " + COLOR_DESTRUCTIVE_RED + "; -fx-border-color: " + COLOR_DESTRUCTIVE_RED + "; -fx-border-width: 1.5; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(248, 113, 113, 0.1); -fx-text-fill: " + COLOR_DESTRUCTIVE_RED + "; -fx-border-color: " + COLOR_DESTRUCTIVE_RED + "; -fx-border-width: 1.5; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
    }

    private void playIntroAnimations(Node form, Node sidebar) {
        animateNode(form, 0, -50, 0);
        animateNode(sidebar, 100, 50, 0);
        if (form instanceof VBox) {
            VBox formBox = (VBox) form;
            for (int i = 0; i < formBox.getChildren().size(); i++) {
                animateNode(formBox.getChildren().get(i), 200 + (i * 50), 0, 20);
            }
        }
    }

    private void animateNode(Node node, long delay, double fromX, double fromY) {
        node.setOpacity(0);
        node.setTranslateX(fromX);
        node.setTranslateY(fromY);
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setToValue(1.0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
        tt.setToX(0);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition pt = new ParallelTransition(node, ft, tt);
        pt.setDelay(Duration.millis(delay));
        pt.play();
    }
}