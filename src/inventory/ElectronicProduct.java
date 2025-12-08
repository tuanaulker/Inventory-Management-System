// Yeni Dosya: ElectronicProduct.java
package inventory;

import inventory.CompositePattern.Product;
public class ElectronicProduct extends Product {
    private final int warrantyMonths;

    public ElectronicProduct(String name, int price, int stockLevel, int threshold, int warrantyMonths) {
        super(name, price, stockLevel, threshold);
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    @Override
    public void display() {
        super.display();
        System.out.println("Warranty Month : " + warrantyMonths);
    }
}