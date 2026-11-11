package org.tucn.pt.model;

/**
 * Represents a product available in the warehouse.
 * Maps to the 'product' table in the database.
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(){
    }

    /**
     * Constructs a new Product with a specified ID.
     *
     * @param id    The unique integer identifier of the product.
     * @param name  The name or description of the product.
     * @param price The unit price of the product.
     * @param stock The current available quantity in the warehouse.
     */
    public Product(int id, String name, double price, int stock){
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public Product(String name, double price, int stock){
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStock() { return stock; }
    public void setStock(int quantity) { this.stock = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    /**
     * Returns a string representation of the Product object.
     *
     * @return A string containing the product's ID, name, price, and stock.
     */
    @Override
    public String toString(){
        return "Product [id=" + id +", name=" + name + ", price=" + price + ", stock" + stock + "]";
    }
}
