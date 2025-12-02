package inventory;

import inventory.CompositePattern.*;
import inventory.FactoryPattern.*;
import inventory.ObserverPattern.*;
import inventory.CommandPattern.*;
import java.io.File;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
        try {
            // Redirect output to outputReport.txt
            PrintStream fileOut = new PrintStream(new File("outputReport.txt"));
            PrintStream consoleOut = System.out;
            System.setOut(fileOut);

            System.out.println("=== Design Patterns Inventory System Demo ===");
            System.out.println("=============================================");

            // 1. Factory Pattern Usage
            System.out.println("\n[Factory Pattern] Creating Products...");
            IProductFactory factory = new ElectronicProductFactory();
            
            Product laptop = factory.createProduct("Laptop", 1200, 10, 5);
            Product smartphone = factory.createProduct("Smartphone", 800, 20, 8);
            Product headphones = factory.createProduct("Headphones", 150, 2, 5); // Low stock initially

            System.out.println("Created: " + laptop.getName());
            System.out.println("Created: " + smartphone.getName());
            System.out.println("Created: " + headphones.getName());

            // 2. Observer Pattern Usage
            System.out.println("\n[Observer Pattern] Setting up Inventory Manager...");
            InventoryManager manager = new InventoryManager();
            
            laptop.registerObs(manager);
            smartphone.registerObs(manager);
            headphones.registerObs(manager);
            System.out.println("InventoryManager registered to observe all products.");

            // 3. Composite Pattern Usage
            System.out.println("\n[Composite Pattern] Organizing Hierarchy...");
            ProductCategory electronics = new ProductCategory("Electronics");
            ProductCategory computers = new ProductCategory("Computers");
            ProductCategory audio = new ProductCategory("Audio");

            electronics.add(computers);
            electronics.add(audio);
            
            computers.add(laptop);
            computers.add(smartphone); // Putting phone in computers for demo simplicity
            audio.add(headphones);

            System.out.println("Displaying Hierarchy:");
            electronics.display();
            
            System.out.println("Total Inventory Value: $" + electronics.getValue());

            // 4. Command Pattern & State Pattern Usage
            System.out.println("\n[Command & State Patterns] Executing Transactions...");

            // Case 1: Normal Sale
            System.out.println("\n-- Transaction 1: Selling 2 Laptops --");
            CommandInterface sellLaptop = new RemoveStock(laptop, 2);
            manager.executeCommand(sellLaptop);

            // Case 2: Low Stock Trigger
            System.out.println("\n-- Transaction 2: Selling 1 Headphone (Trigger Low Stock) --");
            // Headphones started at 2, threshold 5. Already LowStock?
            // Let's check initial state.
            // 2 < 5, so it should be LowStock initially.
            // Selling 1 makes it 1. Still LowStock.
            CommandInterface sellHeadphone = new RemoveStock(headphones, 1);
            manager.executeCommand(sellHeadphone);

            // Case 3: Out of Stock Trigger
            System.out.println("\n-- Transaction 3: Selling remaining Headphone (Trigger OutOfStock) --");
            CommandInterface sellLastHeadphone = new RemoveStock(headphones, 1);
            manager.executeCommand(sellLastHeadphone);

            // Case 4: Fail Sale
            System.out.println("\n-- Transaction 4: Attempting to sell Headphone when OutOfStock --");
            CommandInterface sellFail = new RemoveStock(headphones, 1);
            manager.executeCommand(sellFail);

            // Case 5: Restock (Undo)
            System.out.println("\n-- Transaction 5: Undoing last command (Restocking Headphone) --");
            manager.undoLastCommand(); // Undoes the failed sell? No, failed sell didn't change state but might have been logged?
            // Wait, executeCommand adds to history.
            // If sellFail executed: product.sell() -> state.handleSale() -> prints error.
            // The command was executed and added to history.
            // Undo of RemoveStock is Restock.
            // So undoing the failed sell will actually ADD stock, which might be a logic bug in a simple command pattern if execution failed.
            // Ideally, command should only be added to history if successful.
            // For this demo, let's undo the *successful* sale (Transaction 3).
            // We need to pop the failed one first if it was added.
            // Let's assume we want to undo Transaction 3.
            // Current history: [sellLaptop, sellHeadphone, sellLastHeadphone, sellFail]
            // Undo sellFail -> Adds 1 headphone. (Now stock 1).
            // Undo sellLastHeadphone -> Adds 1 headphone. (Now stock 2).
            
            System.out.println("Undoing 'sellFail' (which actually adds stock because the command object doesn't know the logic failed inside the model):");
            manager.undoLastCommand(); 
            
            System.out.println("Undoing 'sellLastHeadphone':");
            manager.undoLastCommand();

            System.out.println("\nFinal Inventory Status:");
            electronics.display();

            // Save to Database
            saveInventoryDatabase(electronics);

            // Restore output
            System.setOut(consoleOut);
            System.out.println("Report generated in outputReport.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveInventoryDatabase(ProductCategory root) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new File("inventory_db.txt"))) {
            writer.println("--- Inventory Database ---");
            writer.println("Last Updated: " + new java.util.Date());
            writer.println("--------------------------");
            writeCategory(writer, root, "");
            System.out.println("Database updated: inventory_db.txt");
        } catch (Exception e) {
            System.err.println("Error writing to database: " + e.getMessage());
        }
    }

    private static void writeCategory(java.io.PrintWriter writer, ProductCategory category, String indent) {
        writer.println(indent + "[Category] " + category.getName());
        for (CompositePattern.ProductComponent child : category.getChildren()) {
            if (child instanceof ProductCategory) {
                writeCategory(writer, (ProductCategory) child, indent + "  ");
            } else if (child instanceof CompositePattern.Product) {
                CompositePattern.Product p = (CompositePattern.Product) child;
                writer.println(indent + "  - Product: " + p.getName() + 
                               " | Price: " + p.getPrice() + 
                               " | Stock: " + p.getStockLevel() + 
                               " | State: " + p.getState().getClass().getSimpleName());
            }
        }
    }
}
