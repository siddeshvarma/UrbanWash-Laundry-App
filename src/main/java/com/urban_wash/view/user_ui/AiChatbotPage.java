package com.urban_wash.view.user_ui;

import com.urban_wash.Controller.FirebaseService;
import com.urban_wash.Controller.SessionManager;
import com.urban_wash.view.common_methods.GeminiApiClient;
import com.urban_wash.view.common_methods.baseDashBoard;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AiChatbotPage extends baseDashBoard {

    // --- 🎨 UI COLOR SCHEME (Consistent with Dashboard) ---
    private final String COLOR_PRIMARY_ACCENT = "#818cf8";
    private final String COLOR_PRIMARY_ACCENT_HOVER = "#6366f1"; // Added for hover effect
    private final String COLOR_TEXT_PRIMARY = "#FFFFFF";
    private final String COLOR_TEXT_SECONDARY = "#E5E7EB";
    private final String COLOR_SURFACE_TRANSPARENT = "rgba(0, 0, 0, 0.2)";
    private final String COLOR_BORDER_SUBTLE = "rgba(255, 255, 255, 0.2)";
    private final String COLOR_AI_BUBBLE = "rgba(0, 0, 0, 0.3)";

    private VBox chatHistory;
    private TextField userInput;
    private Button sendButton;
    private ScrollPane scrollPane;

    public AiChatbotPage() {
        // Highlight the 'AI Assistant' item in the sidebar when this page is created
        Platform.runLater(() -> highlightNavItem("AI Assistant", false));
    }

    @Override
    protected Node createCenterContent() {
        FirebaseService.initialize();
        
        VBox container = new VBox(20);
        container.setPadding(new Insets(25, 30, 25, 30));
        container.setStyle("-fx-background-color: transparent;");
        container.setAlignment(Pos.TOP_CENTER);
        
        VBox chatWrapper = new VBox(15);
        chatWrapper.setMaxWidth(800);
        chatWrapper.setStyle("-fx-background-color: " + COLOR_SURFACE_TRANSPARENT + "; -fx-border-color: " + COLOR_BORDER_SUBTLE + "; -fx-border-radius: 12; -fx-background-radius: 12;");
        chatWrapper.setPadding(new Insets(20));
        VBox.setVgrow(chatWrapper, Priority.ALWAYS);

        // This uses the styled title from the parent baseDashBoard class
        Label title = createSectionTitle("AI Assistant");
        title.setPadding(new Insets(0, 0, 5, 0));

        chatHistory = new VBox(10);
        chatHistory.setPadding(new Insets(10));
        scrollPane = new ScrollPane(chatHistory);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-background: transparent; "
        );
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        userInput = new TextField();
        userInput.setPromptText("Ask about orders, or find shops in a location...");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        styleTextField(userInput);
        userInput.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleSendAction(); });
        
        sendButton = new Button("Send");
        sendButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        final String sendNormalStyle = "-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 15; -fx-cursor: hand;";
        final String sendHoverStyle = "-fx-background-color: " + COLOR_PRIMARY_ACCENT_HOVER + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 15; -fx-cursor: hand;";
        sendButton.setStyle(sendNormalStyle);
        sendButton.setOnMouseEntered(e -> sendButton.setStyle(sendHoverStyle));
        sendButton.setOnMouseExited(e -> sendButton.setStyle(sendNormalStyle));
        sendButton.setOnAction(e -> handleSendAction());

        HBox inputControls = new HBox(10, userInput, sendButton);
        inputControls.setAlignment(Pos.CENTER);
        
        VBox inputArea = new VBox(5, inputControls);

        chatWrapper.getChildren().addAll(title, scrollPane, inputArea);
        container.getChildren().add(chatWrapper);
        addMessageToHistory("Hello! I'm Washie, your AI Assistant. How can I help you with your laundry today?", false);
        return container;
    }

    private void handleSendAction() {
        String prompt = userInput.getText().trim();
        if (prompt.isEmpty()) return;

        addMessageToHistory(prompt, true);
        
        // --- FIX: Use the actual logged-in user's ID from SessionManager ---
        String userId = SessionManager.getInstance().getCurrentUserUid();
        String firebaseContext = FirebaseService.getUserDataContext(userId); 

        final String[] locationKeywords = {"shop", "store", "laundry", "near", "location", "area"};
        boolean isLocationQuery = false;
        for (String keyword : locationKeywords) {
            if (prompt.toLowerCase().contains(keyword)) {
                isLocationQuery = true;
                break;
            }
        }

        if (isLocationQuery) {
            String location = extractLocation(prompt);
            if (location != null && !location.isEmpty()) {
                String shopsData = FirebaseService.getShopsByLocation(location);
                firebaseContext += shopsData; 
            }
        }
        
        final String finalFirebaseContext = firebaseContext;
        userInput.clear();

        Label thinkingLabel = new Label("Washie is thinking...");
        thinkingLabel.setTextFill(Color.web(COLOR_TEXT_SECONDARY));
        HBox thinkingBox = createMessageBox(thinkingLabel, false);
        chatHistory.getChildren().add(thinkingBox);
        
        Task<String> apiCallTask = new Task<>() {
            @Override
            protected String call() {
                return GeminiApiClient.generateContent(prompt, finalFirebaseContext, null);
            }
        };

        apiCallTask.setOnSucceeded(e -> Platform.runLater(() -> {
            chatHistory.getChildren().remove(thinkingBox);
            addMessageToHistory(apiCallTask.getValue(), false);
        }));
        new Thread(apiCallTask).start();
    }

    private String extractLocation(String prompt) {
        String[] prepositions = {"in ", "near ", "at ", "from ", "around "};
        String lowerCasePrompt = prompt.toLowerCase();
        for (String prep : prepositions) {
            if (lowerCasePrompt.contains(prep)) {
                int startIndex = lowerCasePrompt.indexOf(prep) + prep.length();
                String potentialLocation = prompt.substring(startIndex).trim();
                String location = potentialLocation.split("[,?.]")[0].trim();
                return capitalizeWords(location);
            }
        }
        return null;
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return capitalized.toString().trim();
    }
    
    private void addMessageToHistory(String message, boolean isUser) {
        if (message != null && !message.isEmpty()) {
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(550);
            HBox messageBox = createMessageBox(messageLabel, isUser);
            chatHistory.getChildren().add(messageBox);
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        }
    }

    private HBox createMessageBox(Node content, boolean isUser) {
        HBox messageContainer = new HBox(content);
        
        String userStyle = "-fx-background-color: " + COLOR_PRIMARY_ACCENT + "; -fx-padding: 12; -fx-background-radius: 15 15 5 15;";
        String aiStyle = "-fx-background-color: " + COLOR_AI_BUBBLE + "; -fx-padding: 12; -fx-background-radius: 15 15 15 5;";
        
        if (content instanceof Label) {
            ((Label) content).setTextFill(Color.web(isUser ? COLOR_TEXT_PRIMARY : COLOR_TEXT_SECONDARY));
        }

        // --- REFINEMENT: Style applied to the container (HBox) instead of the content ---
        messageContainer.setStyle(isUser ? userStyle : aiStyle);
        messageContainer.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return messageContainer;
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
}