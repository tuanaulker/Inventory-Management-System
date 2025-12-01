package com.inventory;

import com.inventory.model.Product;
import com.inventory.model.ProductCategory;

public class Main {
    public static void main(String[] args) {
        System.out.println("Test");

        ProductCategory electronics = new ProductCategory("CAT001", "Electronics");

        ProductCategory computers = new ProductCategory("CAT002", "Computers");

        Product laptop = new Product("P001", "Laptop", 5000.0, 15, "Computers");
        Product mouse = new Product("P002", "Mouse", 150.0, 50, "Computers");
        Product keyboard = new Product("P003", "Keyboard", 500.0, 30, "Computers");

        computers.add(laptop);
        computers.add(mouse);
        computers.add(keyboard);

        electronics.add(computers);

        Product phone = new Product("P004", "Iphone 12", 8000.0, 25, "Mobile");
        electronics.add(phone);
        electronics.display(0);

    }
}