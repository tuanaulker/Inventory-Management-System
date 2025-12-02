package inventory;

import inventory.CompositePattern.Product;
import inventory.CommandPattern.CommandInterface;
import java.util.ArrayList;
import java.util.List;

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
        public void update(Product prd) {
            System.out.println("InventoryManager Notification: Product " + prd.getName() + 
                               " state changed to " + prd.getState().getClass().getSimpleName() + 
                               ", Stock: " + prd.getStockLevel());
        }

        @Override
        public void showInventory() {
            System.out.println("Inventory Manager Dashboard: Checking Inventory...");
            // In a real app, this might iterate over a list of managed products
        }

        public void executeCommand(CommandInterface cmd) {
            cmd.execute();
            commandHistory.add(cmd);
        }

        public void undoLastCommand() {
            if (!commandHistory.isEmpty()) {
                CommandInterface cmd = commandHistory.remove(commandHistory.size() - 1);
                cmd.undo();
            } else {
                System.out.println("No commands to undo.");
            }
        }
    }
}
