package com.urban_wash.view.buisness_ui;

import com.urban_wash.view.common_methods.baseBuisness;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * This is the new RevenueAnalyticsPage class that extends your baseBuisness framework.
 * It provides the revenue analytics UI as the main content.
 */
public class RevenueAnalyticsPage extends baseBuisness {

    /**
     * This is the main entry point to run just the Revenue Analytics page.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method overrides the parent's abstract method to provide the main UI.
     * All the logic for creating the revenue analytics view is here.
     */
    @Override
    protected Node createCenterContent() {
        VBox revenueAnalytics = new VBox(25); // Increased spacing
        revenueAnalytics.setPadding(new Insets(30));
        revenueAnalytics.setAlignment(Pos.TOP_LEFT);

        // Title + Dropdown
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setSpacing(20);

        Label title = new Label("Revenue & Earnings Analytics");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Bolder and larger
        title.setTextFill(Color.web("#1f1f1f"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ComboBox<String> dropdown = new ComboBox<>();
        dropdown.getItems().addAll("Last 7 Days", "Last 30 Days", "This Month");
        dropdown.setValue("Last 30 Days");
        dropdown.setStyle("-fx-font-size: 12px; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 6;");

        topRow.getChildren().addAll(title, spacer, dropdown);

        // Stat cards row
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        statsRow.getChildren().addAll(
                createAnalyticsCard("Total Revenue", "$15,240", ""),
                createAnalyticsCard("Average Order Value", "$48.75", ""),
                createAnalyticsCard("Completed Orders", "312", ""),
                createAnalyticsCard("New Customers", "18", "This month")
        );

        // Add title and stats to VBox
        revenueAnalytics.getChildren().addAll(topRow, statsRow);
        
        // You can add charts or other components here later
        // For example:
        // revenueAnalytics.getChildren().add(new Label("Chart would go here..."));

        return revenueAnalytics;
    }

 

    /**
     * Helper method to create a styled analytics card.
     */
    private VBox createAnalyticsCard(String heading, String value, String subtext) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setSpacing(8);
        card.setPrefWidth(220); // Adjusted width
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        card.setAlignment(Pos.TOP_LEFT);

        Label headingLabel = new Label(heading);
        headingLabel.setFont(Font.font("Arial", 14));
        headingLabel.setTextFill(Color.GRAY);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Bolder value
        valueLabel.setTextFill(Color.web("#1f1f1f"));

        card.getChildren().addAll(headingLabel, valueLabel);
        
        if (!subtext.isEmpty()) {
            Label subtextLabel = new Label(subtext);
            subtextLabel.setFont(Font.font("Arial", 12));
            subtextLabel.setTextFill(Color.GRAY);
            card.getChildren().add(subtextLabel);
        }

        return card;
    }
}
