package org.tucn.pt.business_logic;

import org.tucn.pt.data_access.BillDAO;
import org.tucn.pt.data_access.OrderDAO;
import org.tucn.pt.model.Bill;
import org.tucn.pt.model.Client;
import org.tucn.pt.model.Order;
import org.tucn.pt.model.Product;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Encapsulates the business logic for managing orders and generating bills.
 */
public class OrderBLL {
    private OrderDAO orderDAO;
    private BillDAO billDAO;
    private ClientBLL clientBLL;
    private ProductBLL productBLL;

    public OrderBLL(){
        orderDAO = new OrderDAO();
        billDAO = new BillDAO();
        clientBLL = new ClientBLL();
        productBLL = new ProductBLL();
    }

    /**
     * Retrieves a specific order from the database by its ID.
     *
     * @param id The unique identifier of the order.
     * @return The Order object containing the retrieved data.
     * @throws NoSuchElementException if the order is not found.
     */
    public Order findOrderById(int id){
        Order order = orderDAO.findById(id);
        if(order == null){
            throw new NoSuchElementException("Order with id: " + id + " was not found");
        }
        return order;
    }

    /**
     * Retrieves a list of all orders stored in the database.
     *
     * @return A list of Order objects.
     */
    public List<Order> findAllOrders() { return orderDAO.findAll(); }

    /**
     * Processes a new order by validating stock, decrementing inventory,
     * inserting the order, and generating an immutable bill.
     *
     * @param clientId  The ID of the client placing the order.
     * @param productId The ID of the ordered product.
     * @param quantity  The requested quantity.
     * @return The generated Bill record for this transaction.
     * @throws IllegalArgumentException if the requested quantity exceeds available stock.
     */
    public Bill createOrder(int clientId, int productId, int quantity){
        Client client = clientBLL.findClientById(clientId);
        Product product = productBLL.findProductById(productId);

        if(product.getStock() < quantity){
            throw new IllegalArgumentException("Stock to low. Available stock: " + product.getStock() + " " + product.getName());
        }

        Order order = new Order(clientId, productId, quantity);
        order = orderDAO.insert(order);

        product.setStock(product.getStock() - quantity);
        productBLL.updateProduct(product);

        double totalPrice = product.getPrice() * quantity;
        Bill bill = new Bill(0, order.getId(), client.getName(), product.getName(), quantity, totalPrice);
        return billDAO.insert(bill);
    }

    /**
     * Deletes an order from the database.
     *
     * @param id The unique identifier of the order to delete.
     */
    public void deleteOrder(int id) { orderDAO.delete(id); }

    /**
     * Retrieves all generated bills from the database log.
     *
     * @return A list of Bill records.
     */
    public List<Bill> findAllBills() { return billDAO.findAll(); }

    /**
     * Generates a list of formatted string summaries for all existing orders.
     *
     * @return A list of strings detailing order IDs, associated client/product IDs, and quantities.
     */
    public List<String> getOrderSummaries(){
        return orderDAO.findAll().stream()
                .map(o -> "Order #" + o.getId() + " | Client: " + o.getClientId() + " | Product: " + o.getProductId() + " | Quantity: " + o.getQuantity())
                .collect(Collectors.toList());
    }
}
