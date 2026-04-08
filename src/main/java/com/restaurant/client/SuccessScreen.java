/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.client;

import com.restaurant.client.MenuDataModels.Cart;
import com.restaurant.client.MenuDataModels.CartItem;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform; 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SuccessScreen {

    private int secondsElapsed = 0;

    public Scene createScene(Stage stage, Cart cart, int tableNumber) {
        VBox mainContent = new VBox(30);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getStyleClass().add("menu-background-pane");

        
        StackPane animationContainer = new StackPane();
        
        Circle innerCircle = new Circle(40);
        innerCircle.setFill(javafx.scene.paint.Color.web("#1A1008"));
        innerCircle.setStroke(javafx.scene.paint.Color.web("#2ECC71")); 
        innerCircle.setStrokeWidth(3);
        Label checkmark = new Label("✔");
        checkmark.setStyle("-fx-text-fill: #2ECC71; -fx-font-size: 40px; -fx-font-weight: bold;");
        
        Circle rippleCircle = new Circle(40);
        rippleCircle.setFill(javafx.scene.paint.Color.TRANSPARENT);
        rippleCircle.setStroke(javafx.scene.paint.Color.web("#2ECC71"));
        rippleCircle.setStrokeWidth(2);

        animationContainer.getChildren().addAll(rippleCircle, innerCircle, checkmark);

        ScaleTransition st = new ScaleTransition(Duration.seconds(1.5), rippleCircle);
        st.setToX(1.8); st.setToY(1.8);
        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), rippleCircle);
        ft.setFromValue(0.8); ft.setToValue(0.0);
        
        ParallelTransition pulse = new ParallelTransition(st, ft);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        
        Label mainTitle = new Label("Order Placed!");
        mainTitle.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Segoe UI'; -fx-font-size: 32px; -fx-font-weight: bold;");
        Label subTitle = new Label("Your order has been sent to the kitchen");
        subTitle.setStyle("-fx-text-fill: #A0A0A0; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px;");

        
        VBox summaryCard = new VBox(20);
        summaryCard.getStyleClass().add("success-summary-card");
        summaryCard.setMaxWidth(450);

        HBox cardHeader = new HBox();
        VBox orderIdBox = new VBox(LabelBuilder("ORDER ID", 12, "#888888"), LabelBuilder("ORD-" + (int)(Math.random() * 9000 + 1000), 16, "#E6C27A"));
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox tableBox = new VBox(LabelBuilder("TABLE", 12, "#888888"), LabelBuilder(String.valueOf(tableNumber), 16, "#FFFFFF"));
        tableBox.setAlignment(Pos.CENTER_RIGHT);
        cardHeader.getChildren().addAll(orderIdBox, spacer, tableBox);
        summaryCard.getChildren().add(cardHeader);

        String currency = AppSettings.isEnglish ? "KES " : "€";
        for (CartItem item : cart.items) {
            HBox itemRow = new HBox(15);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            Label qtyBadge = new Label(String.valueOf(item.quantity));
            qtyBadge.getStyleClass().add("success-qty-badge");
            VBox itemNameBox = new VBox(LabelBuilder(item.product.name, 16, "#FFFFFF"), LabelBuilder(item.product.category.name, 12, "#888888"));
            Region rowSpacer = new Region(); HBox.setHgrow(rowSpacer, Priority.ALWAYS);
            Label priceLbl = new Label(currency + String.format("%.2f", item.getTotalPrice()));
            priceLbl.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px;");
            itemRow.getChildren().addAll(qtyBadge, itemNameBox, rowSpacer, priceLbl);
            summaryCard.getChildren().add(itemRow);
        }

        HBox totalRow = new HBox(LabelBuilder("Total (incl. service)", 16, "#FFFFFF"), new Region(), LabelBuilder(currency + String.format("%.2f", cart.getTotal()), 18, "#E6C27A"));
        HBox.setHgrow(totalRow.getChildren().get(1), Priority.ALWAYS);
        totalRow.setStyle("-fx-border-color: #333333 transparent transparent transparent; -fx-border-width: 1 0 0 0; -fx-padding: 15 0 0 0;");
        summaryCard.getChildren().add(totalRow);

        
        HBox statusCard = new HBox();
        statusCard.getStyleClass().add("success-summary-card");
        statusCard.setMaxWidth(450);
        statusCard.setAlignment(Pos.CENTER);
        
        Label statusValueLabel = LabelBuilder("Waiting to be received", 15, "#D35400");
        VBox statusBox = new VBox(5, LabelBuilder("STATUS", 11, "#888888"), statusValueLabel);
        statusBox.setAlignment(Pos.CENTER);
        Region statSpacer = new Region(); HBox.setHgrow(statSpacer, Priority.ALWAYS);
        
        Label timeLabel = LabelBuilder("00:00", 16, "#FFFFFF");
        timeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
        VBox timeBox = new VBox(5, LabelBuilder("ELAPSED", 11, "#888888"), timeLabel);
        timeBox.setAlignment(Pos.CENTER);
        
        statusCard.getChildren().addAll(statusBox, statSpacer, timeBox);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            int mins = secondsElapsed / 60;
            int secs = secondsElapsed % 60;
            timeLabel.setText(String.format("%02d:%02d", mins, secs));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        
        StackPane finalLayout = new StackPane(mainContent);
        
        Button floatingTracker = new Button("🛎️ Order Pending");
        floatingTracker.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 15 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5); -fx-cursor: hand;");
        StackPane.setAlignment(floatingTracker, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(floatingTracker, new Insets(40));

        floatingTracker.setOnAction(e -> {
            ScaleTransition clickAnim = new ScaleTransition(Duration.millis(100), floatingTracker);
            clickAnim.setByX(0.1); clickAnim.setByY(0.1);
            clickAnim.setAutoReverse(true); clickAnim.setCycleCount(2);
            clickAnim.play();
        });

        
        Timeline statusTracker = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            pollKitchenStatus(tableNumber, statusValueLabel, mainTitle, checkmark, rippleCircle, floatingTracker);
        }));
        statusTracker.setCycleCount(Animation.INDEFINITE);
        statusTracker.play();

        Button browseBtn = new Button("Continue Browsing Menu ➔");
        browseBtn.getStyleClass().add("cart-place-order-btn");
        browseBtn.setMaxWidth(450);
        browseBtn.setOnAction(e -> {
            statusTracker.stop(); 
            stage.setScene(new MenuScreen().createScene(stage, tableNumber));
        });

        mainContent.getChildren().addAll(animationContainer, mainTitle, subTitle, summaryCard, statusCard, browseBtn);
        finalLayout.getChildren().add(floatingTracker); 

        Scene scene = new Scene(finalLayout, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return scene;
    }

    
    private void pollKitchenStatus(int tableNumber, Label statusValueLabel, Label mainTitle, Label checkmark, Circle rippleCircle, Button floatingTracker) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = "http://localhost:8080/api/orders/table/" + tableNumber + "/active";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Cache-Control", "no-cache") 
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                  .thenAccept(response -> {
                      if (response.statusCode() == 200) {
                          String json = response.body();
                          System.out.println("--- DATABASE TOLD CUSTOMER ---");
                          System.out.println(json); 
                          
                          Platform.runLater(() -> {
                              try {
                                  
                                  if (json.equals("[]")) {
                                      statusValueLabel.setText("ENJOY YOUR MEAL!");
                                      statusValueLabel.setStyle("-fx-text-fill: #9B9B9B; -fx-font-size: 16px; -fx-font-weight: bold;");
                                      mainTitle.setText("Order Complete");
                                      floatingTracker.setText("✅ Enjoy your meal!");
                                      floatingTracker.setStyle("-fx-background-color: #9B9B9B; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 15 25;");
                                  }
                                  else if (json.contains("\"READY\"")) {
                                      statusValueLabel.setText("READY FOR PICKUP!");
                                      statusValueLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-size: 16px; -fx-font-weight: bold;");
                                      mainTitle.setText("Your Food is Ready!");
                                      mainTitle.setStyle("-fx-text-fill: #2ECC71; -fx-font-family: 'Segoe UI'; -fx-font-size: 36px; -fx-font-weight: bold;");
                                      checkmark.setText("🍽"); 
                                      rippleCircle.setStroke(javafx.scene.paint.Color.web("#FFD700")); 
                                      floatingTracker.setText("🛎️ FOOD READY!");
                                      floatingTracker.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #1A1008; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 15 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");
                                  } 
                                  else if (json.contains("\"PREPARING\"")) {
                                      statusValueLabel.setText("Chef is Cooking!");
                                      statusValueLabel.setStyle("-fx-text-fill: #4A90E2; -fx-font-size: 15px;");
                                      mainTitle.setText("Kitchen is Preparing...");
                                      floatingTracker.setText("🔥 Cooking...");
                                      floatingTracker.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 15 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");
                                  }
                              } catch (Exception ex) { }
                          });
                      }
                  });
        } catch (Exception e) { }
    }
    
    private Label LabelBuilder(String text, int size, String hexColor) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + hexColor + "; -fx-font-family: 'Segoe UI'; -fx-font-size: " + size + "px;");
        return l;
    }
}