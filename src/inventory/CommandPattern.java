package inventory;

import inventory.CompositePattern.Product;

public class CommandPattern {

    public interface CommandInterface {
        void execute();
        void undo();
    }

    public static class AddStock implements CommandInterface {
        private Product product;
        private int quantity;

        public AddStock(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        @Override
        public void execute() {
            product.restock(quantity);
        }

        @Override
        public void undo() {
            // Undo add is remove (sell logic without payment check for simplicity, or direct set)
            // Using sell might trigger state changes which is correct.
            // But sell checks for stock.
            // Let's manually decrease.
            int current = product.getStockLevel();
            product.setStockLevel(current - quantity);
            System.out.println("Undo AddStock: Removed " + quantity + " from " + product.getName());
        }
    }

    public static class RemoveStock implements CommandInterface {
        private Product product;
        private int quantity;

        public RemoveStock(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        @Override
        public void execute() {
            product.sell(quantity);
        }

        @Override
        public void undo() {
            product.restock(quantity);
            System.out.println("Undo RemoveStock: Added back " + quantity + " to " + product.getName());
        }
    }
}
