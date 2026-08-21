package com.screenplay.project.model;
public class ModelProduct {
    private final String id;
    private final String name;
    private final double price;
    private final String category;
    public ModelProduct(String id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', price=%.2f, category='%s'}", id, name, price, category);
    }
}