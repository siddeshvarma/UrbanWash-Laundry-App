package com.urban_wash.view.common_methods;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AboutUs extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1E3A8A, #5B21B6);");

        HBox header = createHeader(stage);
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox content = new VBox(40);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(0, 0, 50, 0));

        content.getChildren().add(createHeroSection());
        
        Node projectSection = createProjectSection();
        content.getChildren().add(projectSection);
        playSlideInAnimation(projectSection, 0, 0, 50);

        Node core2webSection = createAlternatingAboutSection("About Core2Web", "https://imgs.search.brave.com/xjYzTdGrqFmEYWqgbnVO25PTWOzF8NBWOfp3PZvYLfA/rs:fit:0:180:1:0/g:ce/aHR0cHM6Ly9pbWFn/ZS53aW51ZGYuY29t/L3YyL2ltYWdlMS9Z/Mjl0TG1SbGRtVnNi/M0JsY2k1amIzSmxN/bmRsWWw5cFkyOXVY/ekUyTWpjNU1EZzBN/RFpmTURjMy9pY29u/LnBuZz93PTE0MCZm/YWtldXJsPTE", "Core2Web Technologies is a leading IT training institute dedicated to bridging the gap between academic knowledge and industry demands. With a focus on practical, hands-on learning, Core2Web offers comprehensive courses in cutting-edge technologies like Java Full-Stack Development, Data Structures, and competitive programming. Their mission is to empower students with the skills and confidence needed to excel in the tech industry.", true);
        content.getChildren().add(core2webSection);
        playSlideInAnimation(core2webSection, 100, -50, 0);

        Node shashiSirSection = createAlternatingAboutSection("Our Mentor, Shashi Sir", "https://images.crunchbase.com/image/upload/c_thumb,h_170,w_170,f_auto,g_face,z_0.7,b_white,q_auto:eco,dpr_2/xehwvn5gvdvc5ws3kawm", "Shashikant Bagal, affectionately known as Shashi Sir, is the visionary founder of Core2Web Technologies. With over 14 years of experience, he is a renowned corporate trainer and a passionate educator. Shashi Sir's teaching methodology simplifies complex programming concepts, making them accessible to all. His dedication has inspired thousands of developers to achieve their career goals at top tech companies.", false);
        content.getChildren().add(shashiSirSection);
        playSlideInAnimation(shashiSirSection, 100, 50, 0);
        
        Node teamSection = createTeamSection();
        content.getChildren().add(teamSection);
        playSlideInAnimation(teamSection, 100, 0, 50);

        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        stage.setTitle("About Us - UrbanWash");
        stage.setScene(scene);
        stage.show();
    }

    private Node createHeroSection() {
        StackPane heroPane = new StackPane();
        heroPane.setPrefHeight(350);

        Image bgImage = new Image("https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=1920&q=80", 1920, 350, true, true, true);
        ImageView imageView = new ImageView(bgImage);

        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(25, 29, 58, 0.6);");

        VBox textContent = new VBox(10);
        textContent.setAlignment(Pos.CENTER);
        Label title = new Label("Our Story & Mission");
        title.setFont(Font.font("System", FontWeight.BOLD, 52));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(15, Color.BLACK));

        Label subtitle = new Label("Learn about the project, the team, and the vision behind UrbanWash.");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 20));
        subtitle.setTextFill(Color.web("#E0E0E0"));

        textContent.getChildren().addAll(title, subtitle);
        heroPane.getChildren().addAll(imageView, overlay, textContent);
        return heroPane;
    }

    private HBox createHeader(Stage stage) {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 0 0 1 0;");

        Button backButton = new Button("← Back to Home");
        backButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        backButton.setCursor(Cursor.HAND);
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #E5E7EB;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-background-radius: 6;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #E5E7EB;"));
        backButton.setOnAction(e -> {
            try {
                new LandingPage().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        header.getChildren().add(backButton);
        return header;
    }

    private Node createProjectSection() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(40, 50, 40, 50));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 12; -fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-width: 1; -fx-border-radius: 12;");
        
        Label title = new Label("About UrbanWash");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);

        Label description = new Label("UrbanWash is a comprehensive laundry management application designed to connect users with local laundry services seamlessly. " + "For customers, it offers a simple way to schedule pickups, track orders, and receive freshly cleaned laundry at their doorstep. " + "For laundry shop owners, it provides a powerful dashboard to manage operations, track finances, and grow their business. " + "This project aims to modernize the laundry industry by providing a centralized, efficient, and user-friendly digital solution for everyone.");
        description.setFont(Font.font("System", 16));
        description.setTextFill(Color.web("#E5E7EB"));
        description.setWrapText(true);
        description.setTextAlignment(TextAlignment.CENTER);
        description.setMaxWidth(800);
        description.setLineSpacing(5);

        section.getChildren().addAll(title, description);
        
        HBox container = new HBox(section);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(1000);
        return container;
    }

    private Node createAlternatingAboutSection(String titleText, String imageUrl, String descriptionText, boolean imageOnLeft) {
        HBox section = new HBox(60);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(50));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 12; -fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-width: 1; -fx-border-radius: 12;");
        section.setMaxWidth(1000);

        ImageView imageView = new ImageView(new Image(imageUrl, 250, 250, true, true));
        Rectangle clip = new Rectangle(250, 250);
        clip.setArcWidth(250);
        clip.setArcHeight(250);
        imageView.setClip(clip);
        imageView.setEffect(new DropShadow(20, Color.rgb(0,0,0,0.3)));

        VBox textContent = new VBox(15);
        textContent.setAlignment(Pos.CENTER_LEFT);
        textContent.setPrefWidth(550);

        Label title = new Label(titleText);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label description = new Label(descriptionText);
        description.setFont(Font.font("System", 16));
        description.setTextFill(Color.web("#E5E7EB"));
        description.setWrapText(true);
        description.setLineSpacing(5);
        // --- FIX: Label ki max width set ki taaki text theek se wrap ho ---
        description.setMaxWidth(550);

        textContent.getChildren().addAll(title, description);

        if (imageOnLeft) {
            section.getChildren().addAll(imageView, textContent);
        } else {
            section.getChildren().addAll(textContent, imageView);
        }
        
        return section;
    }

    private Node createTeamSection() {
        VBox section = new VBox(30);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(50));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 12; -fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-width: 1; -fx-border-radius: 12;");
        section.setMaxWidth(1000);

        Label title = new Label("Meet the Team");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);

        Label teamName = new Label("</DevDynamos>");
        teamName.setFont(Font.font("Consolas", FontWeight.BOLD, 26));
        teamName.setTextFill(Color.web("#A855F7"));

        GridPane teamGrid = new GridPane();
        teamGrid.setAlignment(Pos.CENTER);
        teamGrid.setHgap(50);
        teamGrid.setVgap(40);

        Node member1 = createTeamMemberCard("Siddesh Varma");
        Node member2 = createTeamMemberCard("Abhinav Boge");
        Node member3 = createTeamMemberCard("Nehaa Killedarpatil");
        Node member4 = createTeamMemberCard("Neha Magar");
        
        teamGrid.add(member1, 0, 0);
        teamGrid.add(member2, 1, 0);
        teamGrid.add(member3, 0, 1);
        teamGrid.add(member4, 1, 1);

        playScaleInAnimation(member1, 100);
        playScaleInAnimation(member2, 200);
        playScaleInAnimation(member3, 150);
        playScaleInAnimation(member4, 250);

        section.getChildren().addAll(title, teamName, teamGrid);
        return section;
    }

    private Node createTeamMemberCard(String name) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);

        StackPane avatar = new StackPane();
        Circle circle = new Circle(60);
        circle.setFill(Color.web("#312E81"));
        circle.setStroke(Color.web("#A855F7"));
        circle.setStrokeWidth(2);

        Label initials = new Label(name.substring(0, 1));
        initials.setFont(Font.font("System", FontWeight.BOLD, 50));
        initials.setTextFill(Color.web("#C4B5FD"));
        avatar.getChildren().addAll(circle, initials);
        avatar.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.2)));

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        nameLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(avatar, nameLabel);
        return card;
    }

    private void playSlideInAnimation(Node node, int delay, double fromX, double fromY) {
        node.setOpacity(0);
        node.setTranslateX(fromX);
        node.setTranslateY(fromY);

        FadeTransition ft = new FadeTransition(Duration.millis(800), node);
        ft.setToValue(1);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(800), node);
        tt.setToX(0);
        tt.setToY(0);

        ParallelTransition pt = new ParallelTransition(node, ft, tt);
        pt.setDelay(Duration.millis(delay));
        pt.play();
    }

    private void playScaleInAnimation(Node node, int delay) {
        node.setOpacity(0);
        node.setScaleX(0.7);
        node.setScaleY(0.7);

        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setToValue(1);

        ScaleTransition st = new ScaleTransition(Duration.millis(500), node);
        st.setToX(1.0);
        st.setToY(1.0);
        
        ParallelTransition pt = new ParallelTransition(node, ft, st);
        pt.setDelay(Duration.millis(delay));
        pt.play();
    }
}