package org.tucn.pt.presentation;

import org.tucn.pt.business_logic.ProductBLL;
import org.tucn.pt.model.Product;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * A JPanel representing the graphical user interface for managing products.
 * Provides forms and tables to add, edit, delete, and view client data.
 */
public class ProductView extends JPanel{
    private final ProductBLL productBLL;

    private JTable productTable;
    private JScrollPane tableScrollPane;

    private JTextField idField;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearButton;

    public ProductView() {
        productBLL = new ProductBLL();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    /**
     * Initializes the layout, tables, input fields, and buttons for this panel.
     */
    private void initComponents(){
        productTable = new JTable();
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromSelection();
            }
        });
        tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 250));
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Product Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(10);
        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);
        nameField = new JTextField(20);
        priceField = new JTextField(10);
        stockField = new JTextField(10);

        addLabelAndField(formPanel, gbc, "ID:", idField, 0);
        addLabelAndField(formPanel, gbc, "Name:", nameField, 1);
        addLabelAndField(formPanel, gbc, "Price:", priceField, 2);
        addLabelAndField(formPanel, gbc, "Stock:", stockField, 3);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");

        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(60, 179, 113));
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(205, 92, 92));
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addProduct());
        editButton.addActionListener(e -> editProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearFields());
    }

    /**
     * A helper method to align and add a label and a text field to the form panel.
     *
     * @param panel The target JPanel.
     * @param gbc   The GridBagConstraints for layout positioning.
     * @param label The text for the JLabel.
     * @param field The JTextField to add.
     * @param row   The grid row index where the components should be placed.
     */
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    /**
     * Fetches the latest data from the database and refreshes the display table.
     */
    private void refreshTable(){
        List<Product> products = productBLL. findAllProducts();
        ReflectionTableUtil.refreshTable(productTable, products);
    }

    /**
     * Extracts data from the currently selected row in the table
     * and populates the corresponding text fields in the form.
     */
    private void populateFieldsFromSelection() {
        int row = productTable.getSelectedRow();
        if (row < 0) return;
        idField.setText(String.valueOf(productTable.getValueAt(row, 0)));
        nameField.setText(String.valueOf(productTable.getValueAt(row, 1)));
        priceField.setText(String.valueOf(productTable.getValueAt(row, 2)));
        stockField.setText(String.valueOf(productTable.getValueAt(row, 3)));
    }

    /**
     * Extracts data from the input fields, instantiates a new object,
     * and passes it to the Business Logic layer for insertion.
     */
    private void addProduct() {
        try {
            Product product = new Product(
                    nameField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    Integer.parseInt(stockField.getText().trim())
            );
            productBLL.insertProduct(product);
            JOptionPane.showMessageDialog(this, "Product added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and stock must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Extracts data from the input fields, instantiates an updated object,
     * and passes it to the Business Logic layer for updating.
     */
    private void editProduct() {
        try {
            if (idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a product to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Product product = new Product(
                    Integer.parseInt(idField.getText().trim()),
                    nameField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    Integer.parseInt(stockField.getText().trim())
            );
            productBLL.updateProduct(product);
            JOptionPane.showMessageDialog(this, "Product updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and stock must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Identifies the selected object by its ID and prompts the Business Logic layer to delete it.
     */
    private void deleteProduct() {
        try {
            if (idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a product to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(idField.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete product with id " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                productBLL.deleteProduct(id);
                JOptionPane.showMessageDialog(this, "Product deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all text input fields and deselects any highlighted table rows.
     */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        productTable.clearSelection();
    }
}
