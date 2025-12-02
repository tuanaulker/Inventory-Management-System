package inventory;

import java.util.ArrayList;
import java.util.List;
import inventory.StatePattern.*;
import inventory.ObserverPattern.ObserverInterface;

public class CompositePattern {

    public interface ProductComponent {
        void display();
        int getValue();
        void registerObs(ObserverInterface obs);
        void removeObs(ObserverInterface obs);
    }

    public static class ProductCategory implements ProductComponent {
        private String name;
        private List<ProductComponent> children = new ArrayList<>();

        public ProductCategory(String name) {
            this.name = name;
        }

        public String getName() { return name; }
        public List<ProductComponent> getChildren() { return children; }

        public void add(ProductComponent component) {
            children.add(component);
        }

        public void remove(ProductComponent component) {
            children.remove(component);
        }

        @Override
        public void display() {
            System.out.println("Category: " + name);
            for (ProductComponent child : children) {
                child.display();
            }
        }

        @Override
        public int getValue() {
            int total = 0;
            for (ProductComponent child : children) {
                total += child.getValue();
            }
            return total;
        }

        @Override
        public void registerObs(ObserverInterface obs) {
            // Delegate to children or ignore? 
            // Usually Composite doesn't hold observers for children, but for simplicity let's ignore or implement if needed.
            // The diagram implies ProductComponent has these methods.
            // We'll just implement empty or delegate. Let's delegate to children for bulk registration?
            // Or maybe just leave empty as Category itself isn't observed in this specific diagram context (Product is the Subject).
        }

        @Override
        public void removeObs(ObserverInterface obs) {
            // See above
        }
    }

    public static class Product implements ProductComponent {
        private String name;
        private int price;
        private int stockLevel;
        private int threshold;
        private List<ObserverInterface> observers = new ArrayList<>();
        private State state;

        public Product(String name, int price, int stockLevel, int threshold) {
            this.name = name;
            this.price = price;
            this.stockLevel = stockLevel;
            this.threshold = threshold;
            
            // Initialize state based on stock
            if (stockLevel == 0) {
                this.state = new OutOfStock();
            } else if (stockLevel < threshold) {
                this.state = new LowStock();
            } else {
                this.state = new InStock();
            }
        }

        public String getName() { return name; }
        public int getPrice() { return price; }
        public int getStockLevel() { return stockLevel; }
        public int getThreshold() { return threshold; }
        public State getState() { return state; }

        public void setStockLevel(int newLevel) {
            this.stockLevel = newLevel;
            // State transition logic is handled in State.handleSale usually, 
            // but if set manually, we might need to check state.
            // For this implementation, we assume setStockLevel is called by State or Command.
            // If called directly, we should probably re-evaluate state.
            if (stockLevel == 0) {
                setState(new OutOfStock());
            } else if (stockLevel < threshold) {
                setState(new LowStock());
            } else if (state instanceof OutOfStock && stockLevel > 0) {
                 // Recovering from out of stock
                 setState(stockLevel < threshold ? new LowStock() : new InStock());
            } else if (state instanceof LowStock && stockLevel >= threshold) {
                setState(new InStock());
            }
        }

        public void setState(State newState) {
            this.state = newState;
            notifyObs();
        }

        @Override
        public void display() {
            System.out.println("Product: " + name + " | Price: " + price + " | Stock: " + stockLevel + " | State: " + state.getClass().getSimpleName());
        }

        @Override
        public int getValue() {
            return price * stockLevel;
        }

        @Override
        public void registerObs(ObserverInterface obs) {
            observers.add(obs);
        }

        @Override
        public void removeObs(ObserverInterface obs) {
            observers.remove(obs);
        }

        public void notifyObs() {
            for (ObserverInterface obs : observers) {
                obs.update(this);
            }
        }
        
        public void sell(int quantity) {
            state.handleSale(this, quantity);
        }
        
        public void restock(int quantity) {
            setStockLevel(this.stockLevel + quantity);
            System.out.println("Restocked " + name + " by " + quantity + ". New Level: " + stockLevel);
        }
    }
}
