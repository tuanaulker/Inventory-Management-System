package com.inventory.model;
import java.util.ArrayList;
import java.util.List;

public class ProductCategory extends ProductComponent {
    private List<ProductComponent> children;

    public ProductCategory(String id, String name) {
        super(id, name);
        this.children = new ArrayList<>();
    }

    @Override
    public void add(ProductComponent component) {
        children.add(component);
        System.out.println("Added '" + component.getName() + "' to category '" + this.name + "'");
    }

    @Override
    public void remove(ProductComponent component) {
        children.remove(component);
        System.out.println("Removed '" + component.getName() + "' from category '" + this.name + "'");
    }

    @Override
    public List<ProductComponent> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public void display(int indent) {
        String indentation = getIndentation(indent);
        System.out.println(indentation + "Category: " + name + " (ID: " + id + ")");
        System.out.println(indentation + "Total Items: " + children.size());
        System.out.println(indentation + "Total Stock: " + getTotalStock() + " units");
        System.out.println(indentation + "Total Value: $" + getTotalValue());
        System.out.println(indentation + "Contents:");

        for (ProductComponent child : children) {
            child.display(indent + 1);
        }
    }

    @Override
    public double getTotalValue() {
        double total = 0;
        for (ProductComponent child : children) {
            total += child.getTotalValue();
        }
        return total;
    }

    @Override
    public int getTotalStock() {
        int total = 0;
        for (ProductComponent child : children) {
            total += child.getTotalStock();
        }
        return total;
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }
}