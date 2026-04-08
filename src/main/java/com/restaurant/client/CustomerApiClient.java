/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.restaurant.client.MenuDataModels.Category;
import com.restaurant.client.MenuDataModels.Product;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class CustomerApiClient {

    private final String baseUrl;
    private final Gson gson = new Gson();
    
    public String placeOrder(MenuDataModels.OrderPayload order) throws IOException {
        String json = gson.toJson(order);
        return httpPostJson("/api/orders", json);
    }
    
    

    
    public CustomerApiClient() {
        this.baseUrl = "http://localhost:8080"; 
    }

    
    public List<Category> getCategories() throws IOException {
        String json = httpGet("/api/categories");
        Type type = new TypeToken<List<Category>>() {}.getType();
        return gson.fromJson(json, type);
    }

    
    public List<Product> getProducts() throws IOException {
        String json = httpGet("/api/products");
        Type type = new TypeToken<List<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    
    private String httpGet(String path) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet req = new HttpGet(baseUrl + path);
            return client.execute(req, response -> {
                int status = response.getCode();
                String body = EntityUtils.toString(response.getEntity());
                if (status < 200 || status >= 300) {
                    throw new IOException("Server error " + status + ": " + body);
                }
               
                return body;
            });
        }
        
    }
    
    private String httpPostJson(String path, String json) throws IOException {
        org.apache.hc.client5.http.classic.methods.HttpPost req = new org.apache.hc.client5.http.classic.methods.HttpPost(baseUrl + path);
        req.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(json, org.apache.hc.core5.http.ContentType.APPLICATION_JSON));
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            return client.execute(req, response -> {
                int status = response.getCode();
                String body = EntityUtils.toString(response.getEntity());
                if (status < 200 || status >= 300) {
                    throw new IOException("Server error " + status + ": " + body);
                }
                return body;
            });
        }
    }
}