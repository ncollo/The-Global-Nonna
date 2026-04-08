# The Global Nonna - Restaurant Management Ecosystem

A full-stack restaurant management system featuring a real-time Kitchen Display System (KDS), a dynamic Customer Ordering Tablet, and a Staff Menu Management interface.

## Architecture
The ecosystem consists of three main components:
1. **Spring Boot Backend (The Brain):** A RESTful API built with Spring Boot, utilizing an H2 in-memory database. It handles inventory management and real-time order processing.
2. **Customer Ordering System (JavaFX):** A touch-friendly, dynamic tablet UI where customers can browse categories, view dishes, and place orders.
3. **Staff & Kitchen Display (JavaFX):** * **Menu Management:** Allows staff to dynamically create categories, upload dish images, and set prices.
    * **Kitchen Display (KDS):** A real-time dashboard that receives orders instantly.

## How to Run the Application
This system requires the backend to be running *before* the JavaFX clients are launched.

### 1. Start the Backend Server
1. Run the Spring Boot application (`NonnaApplication.java`).
2. The server will start on `http://localhost:8080`.
3. *Note: The database wipes clean on restart. You must populate it using the Staff App before the Customer App will show food.*

### 2. Populate the Menu (Staff App)
1. Run `MenuManagementApp.java`. 
2. Connect to `http://localhost:8080`.
3. Add new categories (e.g., "African") and add products (e.g., "Pepper Soup") with images.

### 3. Start the Customer Tablet & Kitchen Display
1. Run `WelcomeApp.java` to open the customer view. Select a table to browse the menu you just created.
2. Run `KitchenDisplayApp.java` to open the kitchen view.
3. Add items to your cart on the Customer App, click "Place Order", and watch the order instantly appear on the Kitchen Display!
