/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.client;

import com.restaurant.client.MenuDataModels.Category;
import com.restaurant.client.MenuDataModels.Product;
import com.restaurant.client.MenuDataModels.Cart;
import com.restaurant.client.MenuDataModels.CartItem;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuScreen {

    private final CustomerApiClient apiClient = new CustomerApiClient();
    private List<Product> allProducts = new ArrayList<>();
    
    
    private final Cart cart = new Cart();
    private int currentTableNumber;
    
    private Category selectedCategory = null;
    private String selectedCourse = null;
    
    
    private HBox continentTabBox;
    private HBox categoryPillBox;
    private FlowPane dishGrid;
    private Label sectionTitleLabel;
    private Button topCartButton;
    
    
    private VBox cartDrawer;
    private VBox cartItemsContainer;
    private Label subtotalLabel;
    private Label serviceChargeLabel;
    private Label totalLabel;
    private boolean isCartOpen = false;

    public Scene createScene(Stage stage, int tableNumber) {
        this.currentTableNumber = tableNumber;
        
        
        StackPane root = new StackPane();
        root.getStyleClass().add("menu-background-pane");

        BorderPane layoutPane = new BorderPane();

        
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(25, 40, 25, 40));
        topBar.getStyleClass().add("menu-top-bar");

        VBox brandBox = new VBox(2);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        Label brandName = new Label("The Global Nonna");
        brandName.getStyleClass().add("top-bar-brand-name");
        String tableText = AppSettings.isEnglish ? "Table " : "Tavolo ";
        Label tableNumberLabel = new Label(tableText + tableNumber);
        tableNumberLabel.getStyleClass().add("top-bar-table-label");
        brandBox.getChildren().addAll(brandName, tableNumberLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField searchBar = new TextField();
        searchBar.setPromptText(AppSettings.isEnglish ? "🔍 Search all dishes..." : "🔍 Cerca tutti i piatti...");
        searchBar.getStyleClass().add("menu-search-bar");

        topCartButton = new Button("🛒 Cart (0)");
        topCartButton.getStyleClass().add("top-bar-cart-button");
        
        topCartButton.setOnAction(e -> toggleCartDrawer());

        topBar.getChildren().addAll(brandBox, spacer, searchBar, topCartButton);
        layoutPane.setTop(topBar);

        
        VBox centerContentBox = new VBox(25);
        centerContentBox.setPadding(new Insets(25, 40, 25, 40));

        continentTabBox = new HBox(30);
        continentTabBox.setAlignment(Pos.CENTER_LEFT);

        categoryPillBox = new HBox(15);
        categoryPillBox.setAlignment(Pos.CENTER_LEFT);

        sectionTitleLabel = new Label("Loading Menu...");
        sectionTitleLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold;");

        dishGrid = new FlowPane();
        dishGrid.setHgap(20);
        dishGrid.setVgap(25);

        ScrollPane dishScrollPane = new ScrollPane(dishGrid);
        dishScrollPane.getStyleClass().add("dish-grid-scrollpane");
        dishScrollPane.setFitToWidth(true);
        VBox.setVgrow(dishScrollPane, Priority.ALWAYS);

        centerContentBox.getChildren().addAll(continentTabBox, categoryPillBox, sectionTitleLabel, dishScrollPane);
        layoutPane.setCenter(centerContentBox);

        root.getChildren().add(layoutPane);

        
        buildCartDrawer(stage);
        
        
        StackPane.setAlignment(cartDrawer, Pos.CENTER_RIGHT);
        
        cartDrawer.setTranslateX(400); 
        root.getChildren().add(cartDrawer);

        
        fetchMenuData();

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(MenuScreen.class.getResource("/styles.css").toExternalForm());
        return scene;
    }

    

    private void buildCartDrawer(Stage stage) {
        cartDrawer = new VBox(20);
        cartDrawer.setPrefWidth(400);
        cartDrawer.setMaxWidth(400);
        cartDrawer.getStyleClass().add("cart-drawer");
        cartDrawer.setPadding(new Insets(25));

        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("🛍 Your Order");
        title.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 22px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("cart-close-btn");
        closeBtn.setOnAction(e -> toggleCartDrawer());
        header.getChildren().addAll(title, headerSpacer, closeBtn);

        
        HBox tableBox = new HBox(10);
        tableBox.setAlignment(Pos.CENTER_LEFT);
        Label tableLbl = new Label("Table");
        tableLbl.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px;");
        Label tableNumBadge = new Label(String.valueOf(currentTableNumber));
        tableNumBadge.getStyleClass().add("cart-table-badge");
        tableBox.getChildren().addAll(tableLbl, tableNumBadge);

        
        cartItemsContainer = new VBox(15);
        ScrollPane itemsScroll = new ScrollPane(cartItemsContainer);
        itemsScroll.setFitToWidth(true);
        itemsScroll.getStyleClass().add("dish-grid-scrollpane");
        VBox.setVgrow(itemsScroll, Priority.ALWAYS);

        
        VBox totalsBox = new VBox(10);
        totalsBox.setPadding(new Insets(20, 0, 10, 0));
        totalsBox.setStyle("-fx-border-color: #333333 transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        
        subtotalLabel = buildTotalRow("Subtotal", "$0.00", false);
        serviceChargeLabel = buildTotalRow("Service Charge (10%)", "$0.00", false);
        totalLabel = buildTotalRow("Total", "$0.00", true);
        totalsBox.getChildren().addAll(subtotalLabel.getParent(), serviceChargeLabel.getParent(), totalLabel.getParent());

        
        Button placeOrderBtn = new Button("Place Order ➔");
        placeOrderBtn.getStyleClass().add("cart-place-order-btn");
        placeOrderBtn.setMaxWidth(Double.MAX_VALUE);
        
        placeOrderBtn.setOnAction(e -> {
            if (cart.items.isEmpty()) return;
            
            placeOrderBtn.setText("Submitting...");
            placeOrderBtn.setDisable(true);
            
            
            MenuDataModels.OrderPayload payload = new MenuDataModels.OrderPayload();
            payload.tableNumber = String.valueOf(currentTableNumber);
            for (CartItem item : cart.items) {
                payload.items.add(new MenuDataModels.OrderItemPayload(item));
            }

            
            new Thread(() -> {
                try {
                    apiClient.placeOrder(payload);
                    
                    
                    Platform.runLater(() -> {
                        stage.setScene(new SuccessScreen().createScene(stage, cart, currentTableNumber));
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        placeOrderBtn.setText("Failed! Try Again.");
                        placeOrderBtn.setDisable(false);
                    });
                    ex.printStackTrace();
                }
            }).start();
        });

        cartDrawer.getChildren().addAll(header, tableBox, itemsScroll, totalsBox, placeOrderBtn);
    }

    private Label buildTotalRow(String labelText, String valueText, boolean isBold) {
        HBox row = new HBox();
        Label lbl = new Label(labelText);
        Label val = new Label(valueText);
        String style = isBold ? 
            "-fx-text-fill: #E6C27A; -fx-font-size: 18px; -fx-font-weight: bold;" : 
            "-fx-text-fill: #A0A0A0; -fx-font-size: 14px;";
        lbl.setStyle(style);
        val.setStyle(style);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(lbl, sp, val);
        return val;
    }

    private void toggleCartDrawer() {
        isCartOpen = !isCartOpen;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), cartDrawer);
        tt.setToX(isCartOpen ? 0 : 400); 
        tt.play();
    }

    private void addToCart(Product p) {
        
        boolean found = false;
        for (CartItem item : cart.items) {
            if (item.product.id.equals(p.id)) {
                item.quantity++;
                found = true;
                break;
            }
        }
        if (!found) {
            cart.items.add(new CartItem(p, 1));
        }
        
        refreshCartUI();
        if (!isCartOpen) toggleCartDrawer(); 
    }

    private void refreshCartUI() {
        
        int totalItems = cart.items.stream().mapToInt(i -> i.quantity).sum();
        topCartButton.setText("🛒 Cart (" + totalItems + ")");
        
        
        cartItemsContainer.getChildren().clear();
        String currency = AppSettings.isEnglish ? "KES " : "€";
        
        for (CartItem item : cart.items) {
            VBox itemCard = new VBox(5);
            itemCard.getStyleClass().add("cart-item-card");
            
            HBox topRow = new HBox();
            Label nameLbl = new Label(item.product.name);
            nameLbl.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px;");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Button delBtn = new Button("🗑");
            delBtn.getStyleClass().add("cart-close-btn");
            delBtn.setOnAction(e -> {
                cart.items.remove(item);
                refreshCartUI();
            });
            topRow.getChildren().addAll(nameLbl, sp, delBtn);
            
            HBox bottomRow = new HBox(15);
            bottomRow.setAlignment(Pos.CENTER_LEFT);
            Label qtyLbl = new Label("Qty: " + item.quantity);
            qtyLbl.setStyle("-fx-text-fill: #A0A0A0; -fx-background-color: #2A1A10; -fx-padding: 4 10; -fx-background-radius: 5;");
            Region sp2 = new Region();
            HBox.setHgrow(sp2, Priority.ALWAYS);
            Label priceLbl = new Label(currency + String.format("%.2f", item.getTotalPrice()));
            priceLbl.setStyle("-fx-text-fill: #E6C27A; -fx-font-weight: bold;");
            bottomRow.getChildren().addAll(qtyLbl, sp2, priceLbl);
            
            itemCard.getChildren().addAll(topRow, bottomRow);
            cartItemsContainer.getChildren().add(itemCard);
        }

        
        subtotalLabel.setText(currency + String.format("%.2f", cart.getSubtotal()));
        serviceChargeLabel.setText(currency + String.format("%.2f", cart.getServiceCharge()));
        totalLabel.setText(currency + String.format("%.2f", cart.getTotal()));
    }

    

    private void fetchMenuData() {
        new Thread(() -> {
            try {
                allProducts = apiClient.getProducts();
                Platform.runLater(this::buildFiltersAndDisplay);
            } catch (Exception e) {
                Platform.runLater(() -> sectionTitleLabel.setText("Failed to connect to backend server."));
                e.printStackTrace();
            }
        }).start();
    }

    private void buildFiltersAndDisplay() {
        if (allProducts.isEmpty()) {
            sectionTitleLabel.setText("No menu items available yet.");
            return;
        }

        List<Category> uniqueCategories = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.category != null && uniqueCategories.stream().noneMatch(c -> c.id == p.category.id)) {
                uniqueCategories.add(p.category);
            }
        }

        List<String> uniqueCourses = allProducts.stream()
                .map(p -> p.courseType)
                .distinct()
                .collect(Collectors.toList());

        if (!uniqueCategories.isEmpty()) selectedCategory = uniqueCategories.get(0);
        if (!uniqueCourses.isEmpty()) selectedCourse = uniqueCourses.get(0);

        renderContinentTabs(uniqueCategories);
        renderCoursePills(uniqueCourses);
        renderDishes();
    }

    private void renderContinentTabs(List<Category> categories) {
        continentTabBox.getChildren().clear();
        for (Category cat : categories) {
            boolean isActive = (selectedCategory != null && selectedCategory.id == cat.id);
            VBox tab = new VBox(5);
            tab.setAlignment(Pos.CENTER);
            Label label = new Label(cat.name != null ? cat.name : "Category " + cat.id);
            label.getStyleClass().add(isActive ? "continent-tab-active" : "continent-tab");
            tab.getChildren().add(label);
            tab.setCursor(javafx.scene.Cursor.HAND);
            tab.setOnMouseClicked(e -> {
                selectedCategory = cat;
                renderContinentTabs(categories); 
                renderDishes();
            });
            continentTabBox.getChildren().add(tab);
        }
    }

    private void renderCoursePills(List<String> courses) {
        categoryPillBox.getChildren().clear();
        for (String course : courses) {
            boolean isActive = course.equals(selectedCourse);
            HBox pill = new HBox(8);
            pill.setAlignment(Pos.CENTER);
            pill.getStyleClass().add(isActive ? "category-pill-active" : "category-pill");
            String formattedName = formatCourseName(course);
            Label labelText = new Label(formattedName);
            labelText.setStyle("-fx-text-fill: " + (isActive ? "#E6C27A" : "#888888") + "; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold;");
            pill.getChildren().add(labelText);
            pill.setCursor(javafx.scene.Cursor.HAND);
            pill.setOnMouseClicked(e -> {
                selectedCourse = course;
                renderCoursePills(courses);
                renderDishes();
            });
            categoryPillBox.getChildren().add(pill);
        }
    }

    private void renderDishes() {
        dishGrid.getChildren().clear();
        if (selectedCategory == null || selectedCourse == null) return;

        List<Product> visibleProducts = allProducts.stream()
                .filter(p -> p.category != null && p.category.id == selectedCategory.id)
                .filter(p -> p.courseType.equals(selectedCourse))
                .collect(Collectors.toList());

        String catName = selectedCategory.name != null ? selectedCategory.name : "Category";
        sectionTitleLabel.setText(catName + " / " + formatCourseName(selectedCourse) + " (" + visibleProducts.size() + " dishes)");

        for (Product p : visibleProducts) {
            dishGrid.getChildren().add(buildDishCard(p));
        }
    }

    private VBox buildDishCard(Product p) {
        VBox card = new VBox(15);
        card.getStyleClass().add("dish-card");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(260);
        imageView.setFitHeight(160);
        if (p.imagePath != null && !p.imagePath.isEmpty()) {
            try {
                imageView.setImage(new Image("http://localhost:8080" + p.imagePath, true));
            } catch (Exception ignored) {}
        }
        
        Label nameLabel = new Label(p.name);
        nameLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        
        String currency = AppSettings.isEnglish ? "KES " : "€";
        Label priceLabel = new Label(currency + String.format("%.2f", p.basePrice));
        priceLabel.setStyle("-fx-text-fill: #E6C27A; -fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        HBox infoBox = new HBox(nameLabel, new Region(), priceLabel);
        HBox.setHgrow(infoBox.getChildren().get(1), Priority.ALWAYS); 
        
        Button addBtn = new Button("+ Add to Order");
        addBtn.getStyleClass().add("add-to-order-btn");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        
        
        addBtn.setOnAction(e -> addToCart(p));
        
        card.getChildren().addAll(imageView, infoBox, addBtn);
        return card;
    }

    private String formatCourseName(String raw) {
        if (raw == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String word : raw.split("_")) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
