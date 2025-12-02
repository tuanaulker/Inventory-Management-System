package inventory;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import inventory.CompositePattern.*;
import inventory.FactoryPattern.*;
import inventory.ObserverPattern.*;
import inventory.CommandPattern.*;

public class SimpleWebServer {

    private static ProductCategory rootCategory;
    private static InventoryManager manager;
    private static List<String> logs = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Initialize System
        setupInventory();

        // Create Server
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Define Contexts
        server.createContext("/", new DashboardHandler());
        server.createContext("/api/inventory", new InventoryApiHandler());
        server.createContext("/style.css", new StyleHandler());
        server.createContext("/action", new ActionHandler());

        server.setExecutor(null); // creates a default executor
        System.out.println("Server started on http://localhost:" + port);
        server.start();
    }

    private static void setupInventory() {
        manager = new InventoryManager() {
            @Override
            public void update(Product prd) {
                super.update(prd);
                logs.add("Update: " + prd.getName() + " is now " + prd.getState().getClass().getSimpleName());
            }
        };

        // Try to load from database
        rootCategory = Database.load();

        if (rootCategory == null) {
            System.out.println("No database found. Creating default inventory...");
            IProductFactory factory = new ElectronicProductFactory();

            Product laptop = factory.createProduct("Laptop", 1200, 10, 5);
            Product smartphone = factory.createProduct("Smartphone", 800, 20, 8);
            Product headphones = factory.createProduct("Headphones", 150, 2, 5);

            rootCategory = new ProductCategory("Electronics");
            ProductCategory computers = new ProductCategory("Computers");
            ProductCategory audio = new ProductCategory("Audio");

            rootCategory.add(computers);
            rootCategory.add(audio);
            
            computers.add(laptop);
            computers.add(smartphone);
            audio.add(headphones);
            
            // Save initial state
            Database.save(rootCategory);
        } else {
            System.out.println("Loaded inventory from database.");
        }

        // Register observers for all products in the tree
        registerObserversRecursively(rootCategory);
    }

    private static void registerObserversRecursively(ProductComponent component) {
        if (component instanceof ProductCategory) {
            for (ProductComponent child : ((ProductCategory) component).getChildren()) {
                registerObserversRecursively(child);
            }
        } else if (component instanceof Product) {
            ((Product) component).registerObs(manager);
        }
    }

    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            File file = new File("src/web/index.html");
            if (!file.exists()) {
                String response = "404 - Dashboard not found (checked src/web/index.html)";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            String htmlTemplate = new String(Files.readAllBytes(file.toPath()));
            t.sendResponseHeaders(200, htmlTemplate.length());
            OutputStream os = t.getResponseBody();
            os.write(htmlTemplate.getBytes());
            os.close();
        }
    }

    static class InventoryApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String json = convertCategoryToJson(rootCategory);
            byte[] response = json.getBytes("UTF-8");
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }

        private String convertCategoryToJson(ProductCategory category) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"id\": \"").append(category.getName().hashCode()).append("\",");
            sb.append("\"type\": \"category\",");
            sb.append("\"name\": \"").append(category.getName()).append("\",");
            sb.append("\"children\": [");
            
            List<ProductComponent> children = category.getChildren();
            for (int i = 0; i < children.size(); i++) {
                ProductComponent child = children.get(i);
                if (child instanceof ProductCategory) {
                    sb.append(convertCategoryToJson((ProductCategory) child));
                } else if (child instanceof Product) {
                    Product p = (Product) child;
                    sb.append("{");
                    sb.append("\"id\": \"").append(p.getName().hashCode()).append("\",");
                    sb.append("\"type\": \"product\",");
                    sb.append("\"name\": \"").append(p.getName()).append("\",");
                    sb.append("\"price\": ").append(p.getPrice()).append(",");
                    sb.append("\"quantity\": ").append(p.getStockLevel()).append(",");
                    sb.append("\"threshold\": ").append(p.getThreshold());
                    sb.append("}");
                }
                if (i < children.size() - 1) {
                    sb.append(",");
                }
            }
            
            sb.append("]");
            sb.append("}");
            return sb.toString();
        }
    }

    static class StyleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            File file = new File("src/web/style.css");
            if (!file.exists()) {
                t.sendResponseHeaders(404, -1);
                return;
            }
            byte[] bytes = Files.readAllBytes(file.toPath());
            t.getResponseHeaders().set("Content-Type", "text/css");
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class ActionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                // Parse form data
                String formData = new String(t.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(formData);
                
                String productName = params.get("product");
                String action = params.get("type");
                String amountStr = params.get("amount");
                int amount = 1;
                if (amountStr != null) {
                    try {
                        amount = Integer.parseInt(amountStr);
                    } catch (NumberFormatException e) {
                        amount = 1;
                    }
                }

                Product target = findProduct(rootCategory, productName);
                if (target != null) {
                    if ("buy".equals(action)) {
                        CommandInterface cmd = new RemoveStock(target, amount);
                        manager.executeCommand(cmd);
                        logs.add("Action: Sold " + amount + " " + productName);
                    } else if ("restock".equals(action)) {
                        CommandInterface cmd = new AddStock(target, amount);
                        manager.executeCommand(cmd);
                        logs.add("Action: Restocked " + amount + " " + productName);
                    }
                    
                    // Save changes to database
                    Database.save(rootCategory);
                }

                // Return success JSON
                String response = "{\"status\":\"success\"}";
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private Product findProduct(ProductCategory category, String name) {
            for (ProductComponent child : category.getChildren()) {
                if (child instanceof Product) {
                    if (((Product) child).getName().equals(name)) {
                        return (Product) child;
                    }
                } else if (child instanceof ProductCategory) {
                    Product found = findProduct((ProductCategory) child, name);
                    if (found != null) return found;
                }
            }
            return null;
        }

        private Map<String, String> parseFormData(String formData) {
            Map<String, String> map = new HashMap<>();
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    try {
                        String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                        map.put(key, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return map;
        }
    }
}
