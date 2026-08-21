package com.screenplay.project.model;
import java.util.ArrayList;
import java.util.List;
public class ModelCart {
    private final List<ModelProduct> products;
    private double discount;
    public ModelCart() {
        this.products = new ArrayList<>();
        this.discount = 0.0;
    }
    public void addProduct(ModelProduct product) {
        products.add(product);
    }
    public void removeProduct(ModelProduct product) {
        products.remove(product);
    }
    public void setDiscount(double discountPercent) {
        this.discount = discountPercent;
    }
    public double getSubtotal() {
        return products.stream().mapToDouble(ModelProduct::getPrice).sum();
    }
    public double getDiscountAmount() {
        return getSubtotal() * (discount / 100.0);
    }
    public double getTotal() {
        return getSubtotal() - getDiscountAmount();
    }
    public List<ModelProduct> getProducts() {
        return new ArrayList<>(products);
    }
    public int getProductCount() {
        return products.size();
    }
    @Override
    public String toString() {
        return String.format("Cart{items=%d, subtotal=%.2f, discount=%.1f%%, total=%.2f}", 
            products.size(), getSubtotal(), discount, getTotal());
    }
}
