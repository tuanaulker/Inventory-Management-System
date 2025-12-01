package com.inventory.model;
import java.util.List;

public abstract class ProductComponent {
    protected String id;
    protected String name;

    public ProductComponent(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract void display(int indent);
    public abstract double getTotalValue();
    public abstract int getTotalStock();

    public void add(ProductComponent component) {
        throw new UnsupportedOperationException("Cannot add");
    }

    public void remove(ProductComponent component) {
        throw new UnsupportedOperationException("Cannot remove");
    }

    public List<ProductComponent> getChildren() {
        throw new UnsupportedOperationException("Has no children");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    protected String getIndentation(int indent) {
        return "  ".repeat(indent);
    }
}
