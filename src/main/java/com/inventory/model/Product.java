package com.inventory.model;

public class Product extends ProductComponent {
    private double price;
    private int stockLevel;
    private int reorderThreshold;
    private String category;

    public Product(String id, String name, double price, int stockLevel) {
        super(id, name);
        this.price = price;
        this.stockLevel = stockLevel;
        this.reorderThreshold = 10;
        this.category = "General";
    }

    public Product(String id, String name, double price, int stockLevel, String category) {
        super(id, name);
        this.price = price;
        this.stockLevel = stockLevel;
        this.reorderThreshold = 10;
        this.category = category;
    }

    @Override
    public void display(int indent) {
        String indentation = getIndentation(indent);
        System.out.println(indentation + "Product: " + name);
        System.out.println(indentation + "ID: " + id);
        System.out.println(indentation + "Price: $" + price);
        System.out.println(indentation + "Stock: " + stockLevel + " units");
        System.out.println(indentation + "Category: " + category);
        System.out.println(indentation + "Value: $" + getTotalValue());
    }

    @Override
    public double getTotalValue() {
        return price * stockLevel;
    }

    @Override
    public int getTotalStock() {
        return stockLevel;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}