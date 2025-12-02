package inventory;

import inventory.CompositePattern.Product;

public class StatePattern {
    
    public interface State {
        void handleSale(Product product, int quantity);
    }

    public static class InStock implements State {
        @Override
        public void handleSale(Product product, int quantity) {
            int currentStock = product.getStockLevel();
            int newStock = currentStock - quantity;
            
            if (newStock < 0) {
                System.out.println("Error: Not enough stock for " + product.getName());
                return;
            }

            product.setStockLevel(newStock);
            System.out.println("Sale handled by InStock state. " + quantity + " items sold.");

            if (newStock == 0) {
                product.setState(new OutOfStock());
            } else if (newStock < product.getThreshold()) {
                product.setState(new LowStock());
            }
        }
    }

    public static class LowStock implements State {
        @Override
        public void handleSale(Product product, int quantity) {
            int currentStock = product.getStockLevel();
            int newStock = currentStock - quantity;

            if (newStock < 0) {
                System.out.println("Error: Not enough stock for " + product.getName());
                return;
            }

            product.setStockLevel(newStock);
            System.out.println("Sale handled by LowStock state. " + quantity + " items sold. Warning: Stock is low!");

            if (newStock == 0) {
                product.setState(new OutOfStock());
            }
        }
    }

    public static class OutOfStock implements State {
        @Override
        public void handleSale(Product product, int quantity) {
            System.out.println("Error: Cannot sell " + product.getName() + ". Out of stock!");
        }
    }
}
