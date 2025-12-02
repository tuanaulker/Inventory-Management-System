package inventory;

import inventory.CompositePattern.Product;

public class FactoryPattern {

    public interface IProductFactory {
        Product createProduct(String name, int price, int stock, int threshold);
    }

    public static class ElectronicProductFactory implements IProductFactory {
        @Override
        public Product createProduct(String name, int price, int stock, int threshold) {
            // In a more complex scenario, this might return a subclass like ElectronicProduct
            // But for this diagram, it returns Product (which is concrete).
            // We can assume "Type B Product" from diagram maps to a specific configuration or subclass.
            // Let's just return the standard Product for simplicity as it has all needed fields.
            return new Product(name, price, stock, threshold);
        }
    }
}
