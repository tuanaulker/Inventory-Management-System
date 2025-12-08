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
            PrintStream fileOut = new PrintStream(new File("outputReport.txt"));
            PrintStream consoleOut = System.out;
            System.setOut(fileOut);

            System.out.println("Design Patterns Inventory System Demo");

            System.out.println("\n[Factory Pattern] Creating Products");
            IProductFactory factory = new ElectronicProductFactory();
            
            Product laptop = factory.createProduct("Laptop", 1200, 10, 5);
            Product smartphone = factory.createProduct("Smartphone", 800, 20, 8);
            Product headphones = factory.createProduct("Headphones", 150, 2, 5);

            System.out.println("Created: " + laptop.getName());
            System.out.println("Created: " + smartphone.getName());
            System.out.println("Created: " + headphones.getName());

            System.out.println("\n[Observer Pattern] Setting up Inventory Manager");
            InventoryManager manager = new InventoryManager();
            
            laptop.registerObs(manager);
            smartphone.registerObs(manager);
            headphones.registerObs(manager);
            System.out.println("InventoryManager registered to observe all products.");

            System.out.println("\n[Composite Pattern] Organizing Hierarchy...");
            ProductCategory electronics = new ProductCategory("Electronics");
            ProductCategory computers = new ProductCategory("Computers");
            ProductCategory audio = new ProductCategory("Audio");

            electronics.add(computers);
            electronics.add(audio);
            
            computers.add(laptop);
            computers.add(smartphone);
            audio.add(headphones);

            System.out.println("Displaying Hierarchy:");
            electronics.display();
            
            System.out.println("Total Inventory Value: $" + electronics.getValue());

            System.out.println("\n[Command & State Patterns] Executing Transactions...");

            System.out.println("\n-- Transaction 1: Selling 2 Laptops --");
            CommandInterface sellLaptop = new RemoveStock(laptop, 2);
            manager.executeCommand(sellLaptop);

            System.out.println("\n-- Transaction 2: Selling 1 Headphone (Trigger Low Stock) --");

            CommandInterface sellHeadphone = new RemoveStock(headphones, 1);
            manager.executeCommand(sellHeadphone);

            System.out.println("\n-- Transaction 3: Selling remaining Headphone (Trigger OutOfStock) --");
            CommandInterface sellLastHeadphone = new RemoveStock(headphones, 1);
            manager.executeCommand(sellLastHeadphone);

            System.out.println("\n-- Transaction 4: Attempting to sell Headphone when OutOfStock --");
            CommandInterface sellFail = new RemoveStock(headphones, 1);
            manager.executeCommand(sellFail);

            System.out.println("\n-- Transaction 5: Undoing last command (Restocking Headphone) --");
            manager.undoLastCommand();
            
            System.out.println("Undoing 'sellFail' (which actually adds stock because the command object doesn't know the logic failed inside the model):");
            manager.undoLastCommand(); 
            
            System.out.println("Undoing 'sellLastHeadphone':");
            manager.undoLastCommand();

            System.out.println("\nFinal Inventory Status:");
            electronics.display();

            System.out.println("\n[UPDATE FEATURE] Updating Product Details...");
            laptop.setPrice(1500);
            laptop.setThreshold(3);

            System.out.println("\n[SEARCH FEATURE] Finding Products...");
            Product found = electronics.findProductByName("Laptop");
            if (found != null) {
                System.out.println("✓ Found: " + found.getName() + " - Stock: " + found.getStockLevel());
            }

            System.out.println("\n[ALERT SYSTEM] Checking Low Stock...");
            List<Product> lowStock = electronics.getAllLowStockProducts();
            System.out.println("Products needing attention: " + lowStock.size());

            System.out.println("\n[REPORTING] Generating Summary...");
            manager.generateInventoryReport(electronics);

            System.out.println("\n[HISTORY] Command History...");
            manager.printHistory();
            manager.exportHistoryToFile("command_history.txt");

            System.out.println("\n[STATE PATTERN] Testing handleRestock...");
            System.out.println("Restocking headphones (currently " +
                    headphones.getState().getStateName() + ")...");
            headphones.restock(10);

            saveInventoryDatabase(electronics);

            System.setOut(consoleOut);
            System.out.println("Report generated in outputReport.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
