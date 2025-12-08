package inventory;

import inventory.CompositePattern.*;
import inventory.CommandPattern.CommandInterface;
import inventory.SimpleWebServer;
import java.util.ArrayList;
import java.util.List;

import static inventory.SimpleWebServer.addLog;

public class ObserverPattern {

    public interface ObserverInterface {
        void update(Product prd);
    }

    public interface InventoryDashboard {
        void showInventory();
    }

    public static class InventoryManager implements ObserverInterface, InventoryDashboard {
        private List<CommandInterface> commandHistory = new ArrayList<>();

        @Override
        public void update(Product product) {
            if (product.getStockLevel() <= product.getThreshold()) {
                String message = "WARN: " + product.getName() + " low stock (" + product.getStockLevel() + ").";
                addLog(message);
                System.out.println(message);
            }
        }

        @Override
        public void showInventory() {
            System.out.println("Inventory Manager Dashboard: Checking Inventory...");
        }

        public void executeCommand(CommandInterface cmd) {
            cmd.execute();
            commandHistory.add(cmd);
            addLog("COMMAND EXECUTED: " + cmd.getClass().getSimpleName());
        }


        public void undoLastCommand() {
            if (!commandHistory.isEmpty()) {
                inventory.CommandPattern.CommandInterface lastCommand = commandHistory.remove(commandHistory.size() - 1);
                lastCommand.undo();
                addLog("UNDO: " + lastCommand.getClass().getSimpleName());
            } else {
                addLog("Error: There is no command for undo.");
            }
        }

        public void exportHistoryToFile(String filename) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(filename))) {
                writer.println("Generated: " + new java.util.Date());
                writer.println("Total Commands Executed: " + commandHistory.size());

                if (commandHistory.isEmpty()) {
                    writer.println("No commands executed yet.");
                } else {
                    writer.println("HISTORY (Newest first):");
                    int count = 1;
                    for (int i = commandHistory.size() - 1; i >= 0; i--) {
                        CommandInterface cmd = commandHistory.get(i);
                        writer.println(String.format("%-4s %s", "[" + count + "]", cmd.getClass().getSimpleName()));
                        count++;
                    }
                }

                System.out.println("History exported to: " + filename);

            } catch (Exception e) {
                System.err.println("Error exporting history: " + e.getMessage());
            }
        }

        public void printHistory() {
            System.out.println("\n COMMAND HISTORY ");

            if (commandHistory.isEmpty()) {
                System.out.println("No commands executed yet.");
            } else {
                System.out.println("Total commands: " + commandHistory.size());

                int count = 1;
                for (int i = commandHistory.size() - 1; i >= 0; i--) {
                    CommandInterface cmd = commandHistory.get(i);
                    System.out.println(String.format("%-4s %s", "[" + count + "]", cmd.getClass().getSimpleName()));
                    count++;
                }
            }
        }

        public void generateInventoryReport(ProductCategory rootCategory) {

            List<Product> lowStockProducts = rootCategory.getAllLowStockProducts();
            List<Product> allProducts = rootCategory.getAllProducts();

            System.out.println(">> Overall Statistics:");
            System.out.println("   Total Products: " + allProducts.size());
            System.out.println("   Total Value: $" + rootCategory.getValue());
            System.out.println("   Low Stock Alerts: " + lowStockProducts.size());

            if (!lowStockProducts.isEmpty()) {
                System.out.println("\n>> ATTENTION REQUIRED: LOW STOCK ITEMS:");
                for (Product p : lowStockProducts) {
                    System.out.println(String.format("   • %-20s - Stock: %-3d (%s)",
                            p.getName(),
                            p.getStockLevel(),
                            p.getState().getStateName()));
                }
            } else {
                System.out.println("\n>> Status: All products are adequately stocked.");
            }
        }
    }
}
