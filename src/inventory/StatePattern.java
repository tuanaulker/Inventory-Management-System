package inventory;

import inventory.CompositePattern.Product;

public class StatePattern {
    
    public interface State {
        void handleSale(Product product, int quantity);
        void handleRestock(Product product, int quantity);
        String getStateName();
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
        @Override
        public void handleRestock(Product product, int quantity) {
            int currentStock = product.getStockLevel();
            int newStock = currentStock + quantity;
            product.setStockLevel(newStock);
            System.out.println("Restocked " + product.getName() + " by " + quantity + " units (InStock)");
        }
        @Override
        public String getStateName() {
            return "InStock";
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

        @Override
        public void handleRestock(Product product, int quantity) {
            int currentStock = product.getStockLevel();
            int newStock = currentStock + quantity;
            product.setStockLevel(newStock);

            System.out.println("Restocked " + product.getName() + " by " + quantity + " units (LowStock)");

            if (newStock >= product.getThreshold()) {
                product.setState(new InStock());
                System.out.println("State changed: LowStock → InStock");
            }
        }

        @Override
        public String getStateName() {
            return "LowStock";
        }
    }

    public static class OutOfStock implements State {
        @Override
        public void handleSale(Product product, int quantity) {
            System.out.println("Error: Cannot sell " + product.getName() + ". Out of stock!");
        }
        @Override
        public void handleRestock(Product product, int quantity) {
            int newStock = quantity; // was 0
            product.setStockLevel(newStock);

            System.out.println("Restocked " + product.getName() + " by " + quantity + " units (OutOfStock)");

            if (newStock >= product.getThreshold()) {
                product.setState(new InStock());
                System.out.println("State changed: OutOfStock → InStock");
            } else {
                product.setState(new LowStock());
                System.out.println("State changed: OutOfStock → LowStock");
            }
        }

        @Override
        public String getStateName() {
            return "OutOfStock";
        }
    }
}
