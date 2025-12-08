
package inventory;

import inventory.CompositePattern.Product;
public class ApparelProduct extends Product {
    private final String size;

    public ApparelProduct(String name, int price, int stockLevel, int threshold, String size) {
        super(name, price, stockLevel, threshold);
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    @Override
    public void display() {
        super.display();
        System.out.println(" | Size: " + size);
    }
}