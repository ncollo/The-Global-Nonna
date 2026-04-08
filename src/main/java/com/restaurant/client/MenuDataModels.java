/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.client;

import java.util.ArrayList;
import java.util.List;

public class MenuDataModels {

    public static class Category {
        public Integer id;
        public String name;
        
        public Category(int id) { this.id = id; }
        public Category(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    public static class SpecOption {
        public String name;
        public double extraPrice;
        
        public SpecOption(String name, double extraPrice) {
            this.name = name;
            this.extraPrice = extraPrice;
        }
    }

    public static class SpecGroup {
        public String name;
        public boolean required;
        public List<SpecOption> options = new ArrayList<>();
        
        public SpecGroup(String name, boolean required) {
            this.name = name;
            this.required = required;
        }
    }

    public static class Product {
        public Long id;
        public String name;
        public double basePrice;
        public String courseType; 
        public boolean isAvailable;
        public Category category;
        public List<SpecGroup> specGroups = new ArrayList<>();
        public String imagePath; // The backend sets this automatically
    }
    
    public static class CartItem {
        public Product product;
        public int quantity;
        public List<String> selectedOptions; // e.g., ["Medium", "Beef"]
        public String specialRequests;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
            this.selectedOptions = new ArrayList<>();
            this.specialRequests = "";
        }

        public double getTotalPrice() {
            
            return product.basePrice * quantity;
        }
    }

    public static class Cart {
        public List<CartItem> items = new ArrayList<>();

        public double getSubtotal() {
            return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        }

        public double getServiceCharge() {
            return getSubtotal() * 0.10; 
        }

        public double getTotal() {
            return getSubtotal() + getServiceCharge();
        }
    }
    
    public static class OrderItemPayload {
        public Product product;
        public int quantity;
        public List<String> selectedOptions;
        public String specialRequests;
        
        public OrderItemPayload(CartItem cartItem) {
            
            this.product = new Product();
            this.product.id = cartItem.product.id;
            
            this.quantity = cartItem.quantity;
            this.selectedOptions = cartItem.selectedOptions != null ? cartItem.selectedOptions : new ArrayList<>();
            this.specialRequests = cartItem.specialRequests != null ? cartItem.specialRequests : "";
        }
    }

    public static class OrderPayload {
        public String tableNumber;
        public List<OrderItemPayload> items = new ArrayList<>();
    }
}



