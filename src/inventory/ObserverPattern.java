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
    }
}
