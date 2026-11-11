package org.tucn.pt.presentation;

import org.tucn.pt.business_logic.ClientBLL;
import org.tucn.pt.business_logic.OrderBLL;
import org.tucn.pt.business_logic.ProductBLL;
import org.tucn.pt.model.Bill;
import org.tucn.pt.model.Client;
import org.tucn.pt.model.Order;
import org.tucn.pt.model.Product;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * A JPanel representing the graphical user interface for managing orders.
 * Provides forms and tables to add, edit, delete, and view client data.
 */
public class OrderView extends JPanel {
    private final OrderBLL orderBLL;
    private final ClientBLL clientBLL;
    private final ProductBLL productBLL;

    private JComboBox<String> clientComboBox;
    private JComboBox<String> productComboBox;
    private JTextField quantityField;
    private JButton placeOrderButton;
    private JButton deleteOrderButton;
    private JButton refreshButton;

    private JTable orderTable;
    private JTable billTable;

    private List<Client> clients;
    private List<Product> products;

    public OrderView() {
        orderBLL = new OrderBLL();
        clientBLL = new ClientBLL();
        productBLL = new ProductBLL();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshData();
    }

    /**
     * Initializes the layout, tables, combo boxes, input fields, and buttons for the orders panel.
     */
    private void initComponents(){
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Place New Order"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        clientComboBox = new JComboBox<>();
        productComboBox = new JComboBox<>();
        quantityField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Client: "), gbc);
        gbc.gridx = 1;
        formPanel.add(clientComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Product: "), gbc);
        gbc.gridx = 1;
        formPanel.add(productComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Quantity: "), gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        placeOrderButton = new JButton("Place Order");
        placeOrderButton.setBackground(new Color(70,130, 180));
        placeOrderButton.setForeground(Color.WHITE);

        deleteOrderButton = new JButton("Delete Selected Order");
        deleteOrderButton.setBackground(new Color(205,92, 92));
        deleteOrderButton.setForeground(Color.WHITE);

        refreshButton = new JButton("Refresh");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        btnPanel.add(placeOrderButton);
        btnPanel.add(deleteOrderButton);
        btnPanel.add(refreshButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        orderTable = new JTable();
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(new TitledBorder("Orders"));

        billTable = new JTable();
        JScrollPane billScroll = new JScrollPane(billTable);
        billScroll.setBorder(new TitledBorder("Bills Log"));

        tablesPanel.add(orderScroll);
        tablesPanel.add(billScroll);

        add(tablesPanel, BorderLayout.CENTER);

        placeOrderButton.addActionListener(e -> placeOrder());
        deleteOrderButton.addActionListener(e -> deleteOrder());
        refreshButton.addActionListener(e -> refreshData());
    }

    /**
     * Fetches the latest client, product, order, and bill data from the database
     * to populate the combo boxes and refresh the display tables.
     */
    private void refreshData(){
        clients = clientBLL.findAllClients();
        products = productBLL.findAllProducts();

        clientComboBox.removeAllItems();
        clients.forEach(c -> clientComboBox.addItem(c.getId() + " - " + c.getName()));

        productComboBox.removeAllItems();
        products.forEach(p -> productComboBox.addItem(p.getId() + " - " + p.getName() + " [stock: " + p.getStock() + "]"));

        List<Order> orders = orderBLL.findAllOrders();
        ReflectionTableUtil.refreshTable(orderTable, orders);

        List<Bill> bills = orderBLL.findAllBills();

        String[] colNames = {"id", "orderId", "clientName", "productName", "quantity", "totalPrice"};
        Object[][] data = new Object[bills.size()][6];
        for (int i = 0; i < bills.size(); i++) {
            Bill b = bills.get(i);
            data[i] = new Object[]{b.id(), b.orderId(), b.clientName(), b.productName(), b.quantity(), b.totalPrice()};
        }

        billTable.setModel(new javax.swing.table.DefaultTableModel(data, colNames) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    /**
     * Extracts data from the combo boxes and quantity field, validates the input,
     * and passes the data to the Business Logic layer to generate a new order and bill.
     */
    private void placeOrder(){
        try {
            int clientIdx = clientComboBox.getSelectedIndex();
            int productIdx = productComboBox.getSelectedIndex();
            if (clientIdx < 0 || productIdx < 0) {
                JOptionPane.showMessageDialog(this, "Please select a client and product.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (quantityField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a quantity.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int clientId = clients.get(clientIdx).getId();
            int productId = products.get(productIdx).getId();
            int quantity = Integer.parseInt(quantityField.getText().trim());

            Bill bill = orderBLL.createOrder(clientId, productId, quantity);
            JOptionPane.showMessageDialog(this, "Order placed successfully!\n" + bill.summary(), "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
            quantityField.setText("");
            refreshData();
        } catch (IllegalArgumentException e){
            String msg = e.getMessage() != null && e.getMessage().contains("For input string") ? "Quantity must be a valid integer." : e.getMessage();
            JOptionPane.showMessageDialog(this, msg, e.getMessage() != null && e.getMessage().contains("Under-stock") ? "Under-Stock Error" : "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Identifies the selected order by its ID and prompts the Business Logic layer to delete it.
     */
    private void deleteOrder(){
        int row = orderTable.getSelectedRow();
        if(row < 0){
            JOptionPane.showMessageDialog(this, "Select an order to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int orderId = (int) orderTable.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete order #" + orderId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            orderBLL.deleteOrder(orderId);
            JOptionPane.showMessageDialog(this, "Order deleted", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        }
    }
}
