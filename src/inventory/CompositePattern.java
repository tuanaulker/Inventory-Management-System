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
            for (ProductComponent child : getChildren()) {
                child.registerObs(obs);
            }
        }

        @Override
        public void removeObs(ObserverInterface obs) {
            for (ProductComponent child : getChildren()) {
                child.removeObs(obs);
            }
        }

        public Product findProductByName(String name) {
            for (ProductComponent child : children) {
                if (child instanceof Product) {
                    Product p = (Product) child;
                    if (p.getName().equalsIgnoreCase(name)) {
                        return p;
                    }
                } else if (child instanceof ProductCategory) {
                    Product found = ((ProductCategory) child).findProductByName(name);
                    if (found != null) return found;
                }
            }
            return null;
        }

        public List<Product> getAllLowStockProducts() {
            List<Product> lowStockList = new ArrayList<>();
            collectLowStockProducts(lowStockList);
            return lowStockList;
        }

        private void collectLowStockProducts(List<Product> result) {
            for (ProductComponent child : children) {
                if (child instanceof Product) {
                    Product p = (Product) child;
                    String stateName = p.getState().getStateName();
                    if (stateName.equals("LowStock") || stateName.equals("OutOfStock")) {
                        result.add(p);
                    }
                } else if (child instanceof ProductCategory) {
                    ((ProductCategory) child).collectLowStockProducts(result);
                }
            }
        }

        public List<Product> getAllProducts() {
            List<Product> allProducts = new ArrayList<>();
            collectAllProducts(allProducts);
            return allProducts;
        }

        private void collectAllProducts(List<Product> result) {
            for (ProductComponent child : children) {
                if (child instanceof Product) {
                    result.add((Product) child);
                } else if (child instanceof ProductCategory) {
                    ((ProductCategory) child).collectAllProducts(result);
                }
            }
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

        public void setStockLevel(int newStockLevel) {
            this.stockLevel = newStockLevel;

            State newState;
            if (newStockLevel == 0) {
                newState = new OutOfStock();
            } else if (newStockLevel <= this.threshold) {
                newState = new LowStock();
            } else {
                newState = new InStock();
            }

            if (this.state.getClass() != newState.getClass()) {
                this.state = newState;
                System.out.println(this.name + " New State: " + newState.getClass().getSimpleName());
            }
            notifyObs();
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
            state.handleRestock(this, quantity);
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
            System.out.println("Updated threshold for " + name + " to " + threshold);

            int current = this.stockLevel;
            if (current == 0) {
                setState(new OutOfStock());
            } else if (current < threshold) {
                setState(new LowStock());
            } else {
                setState(new InStock());
            }
        }

        public void setPrice(int price) {
            this.price = price;
            System.out.println("Updated price for " + name + " to $" + price);
        }
    }
}
