package org.tucn.pt.model;

/**
 * Represents an order placed by a client for a specific product.
 * Maps to the 'orders' table in the database.
 */
public class Order {
    private int id;
    private int clientId;
    private int productId;
    private int quantity;

    public Order(){
    }

    /**
     * Constructs a new Order with a specified ID.
     *
     * @param id        The unique integer identifier of the order.
     * @param clientId  The unique identifier of the client placing the order.
     * @param productId The unique identifier of the ordered product.
     * @param quantity  The amount of the product requested.
     */
    public Order(int id, int clientId, int productId, int quantity) {
        this.id = id;
        this.clientId = clientId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Order(int clientId, int productId, int quantity) {
        this.clientId = clientId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId;}
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Returns a string representation of the Order object.
     *
     * @return A string containing the order's ID, client ID, product ID, and quantity.
     */
    @Override
    public String toString(){
        return "Order [id=" + id + ", clientId=" + clientId + ", productId=" + productId + ", quantity=" + quantity + "]";
    }
}
