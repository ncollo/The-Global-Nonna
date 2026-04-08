/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class WelcomeApp extends Application {
    
    private Label titleLabel;
    private Label subtitleLabel;
    private Button continueButton;
    private Label footerLabel;
    private Button langButton;
    
    private Label sloganLabel;
    
    private int selectedCard = -1;

    @Override
    public void start(Stage primaryStage) {
        
        StackPane root = new StackPane();
        root.getStyleClass().add("background-pane");

        
        BorderPane layoutPane = new BorderPane();

        
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT); 
        topBar.setPadding(new Insets(40, 50, 0, 50));
        
        
        Region logoIcon = new Region();
        logoIcon.getStyleClass().add("logo-icon-designed");

        VBox textBrandBox = new VBox(0); 
        textBrandBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label("The Global Nonna");
        nameLabel.getStyleClass().add("logo-name-designed");
        
        sloganLabel = new Label();
        sloganLabel.getStyleClass().add("logo-slogan-designed");
        
        textBrandBox.getChildren().addAll(nameLabel, sloganLabel);

        HBox brandContainer = new HBox(15);
        brandContainer.setAlignment(Pos.CENTER_LEFT);
        brandContainer.getStyleClass().add("brand-container");
        brandContainer.getChildren().addAll(logoIcon, textBrandBox);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Invisibly pushes the lang button to the right
        
        
        langButton = new Button("🇮🇹 IT");
        langButton.getStyleClass().add("lang-button-designed");
        langButton.setOnAction(e -> toggleLanguage());

        topBar.getChildren().addAll(brandContainer, spacer, langButton);
        layoutPane.setTop(topBar); 

        
        VBox glassPanel = new VBox(20);
        glassPanel.getStyleClass().add("glass-panel");
        glassPanel.setMaxSize(800, 600);
        glassPanel.setAlignment(Pos.CENTER);
        glassPanel.setPadding(new Insets(40));

        titleLabel = new Label();
        titleLabel.getStyleClass().add("title-text-designed");
        
        subtitleLabel = new Label();
        subtitleLabel.getStyleClass().add("subtitle-text-designed");

        TilePane tableGrid = new TilePane();
        tableGrid.setHgap(20);
        tableGrid.setVgap(20);
        tableGrid.setAlignment(Pos.CENTER);
        tableGrid.setPrefColumns(4);

        Button[] tableButtons = new Button[12];
        for (int i = 0; i < 12; i++) {
            final int tableNum = i + 1;
            Button btn = new Button(tableNum + "\nTABLE");
            btn.getStyleClass().add("table-button-designed");
            
            btn.setOnAction(e -> {
                for (Button b : tableButtons) b.getStyleClass().remove("table-button-selected");
                btn.getStyleClass().add("table-button-selected");
                selectedCard = tableNum;
                updateContinueButton();
            });
            tableButtons[i] = btn;
            tableGrid.getChildren().add(btn);
        }

        continueButton = new Button();
        continueButton.getStyleClass().add("continue-button-designed");
        continueButton.setDisable(true); 
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setOnAction(e -> {
            System.out.println("Proceeding to menu for Table: " + selectedCard);
            
            
            Scene menuScene = new MenuScreen().createScene(primaryStage, selectedCard);
            primaryStage.setScene(menuScene);
            primaryStage.setFullScreen(true);
        });

        footerLabel = new Label();
        footerLabel.getStyleClass().add("footer-text-designed");
        VBox.setMargin(footerLabel, new Insets(20, 0, 0, 0));

        glassPanel.getChildren().addAll(titleLabel, subtitleLabel, tableGrid, continueButton, footerLabel);
        layoutPane.setCenter(glassPanel); 

        root.getChildren().add(layoutPane);

        updateTexts();

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setTitle("The Global Nonna - Customer Display");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void toggleLanguage() {
       
        AppSettings.isEnglish = !AppSettings.isEnglish;
        updateTexts();
    }

    private void updateTexts() {
        
        if (AppSettings.isEnglish) {
            langButton.setText("🇮🇹 IT");
            sloganLabel.setText("Authentic Italian, Global Flavors.");
            titleLabel.setText("Welcome, Valued Guest");
            subtitleLabel.setText("Please select your table number to begin ordering");
            continueButton.setText("Select a Table to Continue");
            footerLabel.setText("Need assistance? Please call our staff. We're happy to help.");
        } else {
            langButton.setText("🇬🇧 EN");
            sloganLabel.setText("Vero Italiano, Sapori Globali.");
            titleLabel.setText("Benvenuto, Gentile Ospite");
            subtitleLabel.setText("Seleziona il numero del tuo tavolo per ordinare");
            continueButton.setText("Seleziona un tavolo per continuare");
            footerLabel.setText("Hai bisogno di assistenza? Chiama il nostro staff. Siamo felici di aiutarti.");
        }
        updateContinueButton();
    }

    private void updateContinueButton() {
        if (selectedCard != -1) {
            continueButton.setDisable(false);
            continueButton.setText(AppSettings.isEnglish ? "Continue to Menu ➔" : "Continua al Menu ➔");
            continueButton.getStyleClass().add("continue-button-active");
        } else {
            continueButton.setDisable(true);
            continueButton.getStyleClass().remove("continue-button-active");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}