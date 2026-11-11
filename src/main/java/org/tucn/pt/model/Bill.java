package org.tucn.pt.model;

/**
 * An immutable record representing a generated bill for a completed order.
 * Maps to the 'bill' log table in the database.
 *
 * @param id          The unique identifier of the bill.
 * @param orderId     The identifier of the associated order.
 * @param clientName  The name of the client at the time of purchase.
 * @param productName The name of the product at the time of purchase.
 * @param quantity    The amount of product purchased.
 * @param totalPrice  The total cost of the order (price * quantity).
 */
public record Bill(int id, int orderId, String clientName, String productName, int quantity, double totalPrice) {

    /**
     * Generates a formatted string summary of the bill details.
     *
     * @return A string containing the bill's overview.
     */
    public String summary(){
        return "Bill #" + id + " | Order #" + orderId + " | Client: " + clientName + " | Product: " + productName + " | Quantity: " + quantity + " | Total: $" + String.format("%.2f", totalPrice);
    }
}
