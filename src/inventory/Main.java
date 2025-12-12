package inventory;

import inventory.CompositePattern.*;
import inventory.FactoryPattern.*;
import inventory.ObserverPattern.*;
import inventory.CommandPattern.*;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            PrintStream consoleOut = System.out;

            System.out.println("Design Patterns Inventory System Demo");

            System.out.println("\n[Observer Pattern] Setting up Inventory Manager");
            InventoryManager manager = new InventoryManager();

            System.out.println("\n[Composite Pattern] Organizing Hierarchy...");
            ProductCategory rootCategory = initializeInventory(manager);

            // Retrieve objects for demo
            Product laptop = rootCategory.findProductByName("Laptop");
            Product smartphone = rootCategory.findProductByName("Smartphone");
            Product headphones = rootCategory.findProductByName("Headphones");

            System.out.println("Displaying Hierarchy:");
            rootCategory.display();
            
            System.out.println("Total Inventory Value: $" + rootCategory.getValue());

            System.out.println("\n[UPDATE FEATURE] Updating Product Details...");
            if (laptop != null) {
                laptop.setPrice(1500);
                laptop.setThreshold(3);
            }

            System.out.println("\n[SEARCH FEATURE] Finding Products...");
            Product found = rootCategory.findProductByName("Laptop");
            if (found != null) {
                System.out.println("✓ Found: " + found.getName() + " - Stock: " + found.getStockLevel());
            }

            System.out.println("\n[ALERT SYSTEM] Checking Low Stock...");
            List<Product> lowStock = rootCategory.getAllLowStockProducts();
            System.out.println("Products needing attention: " + lowStock.size());

            System.out.println("\n[STATE PATTERN] Testing handleRestock...");
            if (headphones != null) {
                System.out.println("Restocking headphones (currently " +
                        headphones.getState().getStateName() + ")...");
                headphones.restock(10);
            }

            saveInventoryDatabase(rootCategory);

            System.setOut(consoleOut);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ProductCategory initializeInventory(InventoryManager manager) {
        ProductCategory rootCategory = inventory.Database.load();

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
            ProductCategory clothing = new ProductCategory("Clothing");

            rootCategory.add(electronics);
            rootCategory.add(apparel);

            electronics.add(computers);
            electronics.add(audio);

            computers.add(laptop);
            computers.add(smartphone);
            audio.add(headphones);

            apparel.add(clothing);

            clothing.add(tshirt);
            clothing.add(jeans);

            inventory.Database.save(rootCategory);
        } else {
            System.out.println("Loaded inventory from database.");
        }
        
        if (manager != null) {
            rootCategory.registerObs(manager);
        }
        return rootCategory;
    }

    public static void saveInventoryDatabase(ProductCategory root) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new File("inventory_db.txt"))) {
            writer.println("Inventory Database");
            writer.println("Last Updated: " + new java.util.Date());
            writeCategory(writer, root, "");
            System.out.println("Database updated: inventory_db.txt");
        } catch (Exception e) {
            System.err.println("Error writing to database: " + e.getMessage());
        }
    }

    private static void writeCategory(java.io.PrintWriter writer, ProductCategory category, String indent) {
        writer.println(indent + "[Category] " + category.getName());
        for (ProductComponent child : category.getChildren()) {
            if (child instanceof ProductCategory) {
                writeCategory(writer, (ProductCategory) child, indent + "  ");
            } else if (child instanceof Product) {
                Product p = (Product) child;
                writer.println(indent + "  - Product: " + p.getName() + 
                               " | Price: " + p.getPrice() + 
                               " | Stock: " + p.getStockLevel() + 
                               " | State: " + p.getState().getClass().getSimpleName());
            }
        }
    }
}
