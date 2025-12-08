package inventory;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
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
import inventory.Database.*;

public class SimpleWebServer {

    private static ProductCategory rootCategory;
    private static InventoryManager manager;
    private static List<String> logs = new ArrayList<>();
    private static final Map<String, IProductFactory> FACTORIES = new HashMap<>();

    public static void main(String[] args) throws IOException {
        setupInventory();
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/action", new ActionHandler());

        server.createContext("/api/inventory", new InventoryApiHandler());
        server.createContext("/api/logs", new LogsHandler());

        server.createContext("/", new DashboardHandler());
        server.createContext("/style.css", new StyleHandler());

        server.setExecutor(null);
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
        FACTORIES.put("electronic", new ElectronicProductFactory());
        FACTORIES.put("apparel", new ApparelProductFactory());

        rootCategory = inventory.Database.load();

        if (rootCategory == null) {
            System.out.println("No database found. Creating default inventory");
            IProductFactory factory = new ElectronicProductFactory();
            IProductFactory apparelFactory = new ApparelProductFactory();

            Product laptop = factory.createProduct("Laptop", 1200, 10, 5);
            Product smartphone = factory.createProduct("Smartphone", 800, 20, 8);
            Product headphones = factory.createProduct("Headphones", 150, 2, 5);

            Product tshirt = apparelFactory.createProduct("T-Shirt", 30, 15, 5);
            Product jeans = apparelFactory.createProduct("Jeans", 70, 8, 3);

            rootCategory = new ProductCategory("Global Inventory");
            ProductCategory electronics = new ProductCategory("Electronics");
            ProductCategory apparel = new ProductCategory("Apparel");

            ProductCategory computers = new ProductCategory("Computers");
            ProductCategory audio = new ProductCategory("Audio");

            rootCategory.add(electronics);
            rootCategory.add(apparel);

            electronics.add(computers);
            electronics.add(audio);

            computers.add(laptop);
            computers.add(smartphone);
            audio.add(headphones);

            apparel.add(tshirt);
            apparel.add(jeans);

            inventory.Database.save(rootCategory);
        } else {
            System.out.println("Loaded inventory from database.");
        }
        rootCategory.registerObs(manager);
        //registerObserversRecursively(rootCategory);
    }

    /*private static void registerObserversRecursively(ProductComponent component) {
        if (component instanceof ProductCategory) {
            for (ProductComponent child : ((ProductCategory) component).getChildren()) {
                registerObserversRecursively(child);
            }
        } else if (component instanceof Product) {
            ((Product) component).registerObs(manager);
        }
    }*/

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
            byte[] response = json.getBytes(StandardCharsets.UTF_8);
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
                String formData = new String(t.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(formData);

                String productName = params.get("product");
                String action = params.get("type");
                String amountStr = params.get("amount");
                int amount = 1;

                System.out.println("Action : " + action);

                if (amountStr != null) {
                    try {
                        amount = Integer.parseInt(amountStr);
                    } catch (NumberFormatException e) {
                        amount = 1;
                    }
                }

                if ("create_product".equals(action)) {

                    String productType = params.get("productType");
                    String parentCategoryName = params.get("parentCategory");
                    String name = params.get("name");

                    int price = Integer.parseInt(params.get("price"));
                    int stock = Integer.parseInt(params.get("stock"));
                    int threshold = Integer.parseInt(params.get("threshold"));
                    String specificParam = params.get("specificParam");

                    IProductFactory factory = FACTORIES.get(productType.toLowerCase());
                    if (factory == null) {
                        throw new IllegalArgumentException("Unknown product type: " + productType);
                    }

                    Product newProduct;
                    if (productType.equalsIgnoreCase("electronic")) {
                        int warranty = Integer.parseInt(specificParam);
                        newProduct = new inventory.ElectronicProduct(name, price, stock, threshold, warranty);
                    } else if (productType.equalsIgnoreCase("apparel")) {
                        newProduct = new inventory.ApparelProduct (name, price, stock, threshold, specificParam);
                    } else {
                        newProduct = factory.createProduct(name, price, stock, threshold);
                    }

                    ProductCategory parent = findCategory(rootCategory, parentCategoryName);
                    if (parent == null) {
                        throw new IllegalArgumentException("Parent category not found: " + parentCategoryName);
                    }

                    parent.add(newProduct);
                    newProduct.registerObs(manager);
                    addLog("CREATE: " + name + " added to " + parentCategoryName + ".");
                    inventory.Database.save(rootCategory);
                }
                else if ("undo".equals(action)) {
                    manager.undoLastCommand();
                    inventory.Database.save(rootCategory);
                } else {
                    Product target = findProduct(rootCategory, productName);

                    if (target != null) {
                        CommandInterface cmd = null;
                        if ("buy".equals(action)) {
                            cmd = new RemoveStock(target, amount);
                        } else if ("restock".equals(action)) {
                            cmd = new AddStock(target, amount);
                        }

                        if (cmd != null) {
                            manager.executeCommand(cmd);
                            inventory.Database.save(rootCategory);
                        }
                    }
                }

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
                        String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        map.put(key, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return map;
        }
    }

    public static void addLog(String logEntry) {
        java.time.LocalTime now = java.time.LocalTime.now();
        logs.add(now.toString().substring(0, 8) + " | " + logEntry);
        if (logs.size() > 50) {
            logs.removeFirst();
        }
    }

    private static class LogsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("Log");
            String response = "[]";
            if ("GET".equals(t.getRequestMethod())) {
                Gson gson = new Gson();
                response = gson.toJson(logs);
            }

            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private static ProductCategory findCategory(ProductCategory category, String name) {
        if (category.getName().equals(name)) return category;

        for (ProductComponent child : category.getChildren()) {
            if (child instanceof ProductCategory) {
                ProductCategory found = findCategory((ProductCategory) child, name);
                if (found != null) return found;
            }
        }
        return null;
    }
}
