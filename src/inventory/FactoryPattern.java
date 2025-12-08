package inventory;

import inventory.CompositePattern.Product;
import inventory.ElectronicProduct;
import inventory.ApparelProduct;
public class FactoryPattern {

    public interface IProductFactory {
        Product createProduct(String name, int price, int stock, int threshold);
    }

    public static class ElectronicProductFactory implements inventory.FactoryPattern.IProductFactory {
        public ElectronicProductFactory() { /* compiled code */ }

        @Override
        public Product createProduct(java.lang.String name, int price, int stock, int threshold) {
            int defaultWarranty = 12;
            return new ElectronicProduct(name, price, stock, threshold, defaultWarranty);
        }
    }

    public static class ApparelProductFactory implements inventory.FactoryPattern.IProductFactory {
        public ApparelProductFactory() { /* compiled code */ }

        @Override
        public Product createProduct(java.lang.String name, int price, int stock, int threshold) {
            String defaultSize = "M";
            return new ApparelProduct(name, price, stock, threshold, defaultSize);
        }
    }
}
