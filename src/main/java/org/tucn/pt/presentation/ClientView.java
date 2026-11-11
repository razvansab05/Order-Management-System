package org.tucn.pt.presentation;

import org.tucn.pt.business_logic.ClientBLL;
import org.tucn.pt.model.Client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * A JPanel representing the graphical user interface for managing clients.
 * Provides forms and tables to add, edit, delete, and view client data.
 */
public class ClientView extends JPanel{
    private final ClientBLL clientBLL;

    private JTable clientTable;
    private JScrollPane tableScrollPane;

    private JTextField idField;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField emailField;
    private JTextField phoneField;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearButton;

    public ClientView() {
        clientBLL = new ClientBLL();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    /**
     * Initializes the layout, tables, input fields, and buttons for this panel.
     */
    private void initComponents() {
        // Table panel
        clientTable = new JTable();
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromSelection();
            }
        });
        tableScrollPane = new JScrollPane(clientTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 250));
        add(tableScrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Client Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(10);
        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);
        nameField = new JTextField(20);
        addressField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(15);

        addLabelAndField(formPanel, gbc, "ID:", idField, 0);
        addLabelAndField(formPanel, gbc, "Name:", nameField, 1);
        addLabelAndField(formPanel, gbc, "Address:", addressField, 2);
        addLabelAndField(formPanel, gbc, "Email:", emailField, 3);
        addLabelAndField(formPanel, gbc, "Phone:", phoneField, 4);

        // Buttons
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

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // Action listeners
        addButton.addActionListener(e -> addClient());
        editButton.addActionListener(e -> editClient());
        deleteButton.addActionListener(e -> deleteClient());
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
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String label,  JTextField field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    /**
     * Fetches the latest data from the database and refreshes the display table.
     */
    private void refreshTable() {
        List<Client> clients = clientBLL.findAllClients();
        ReflectionTableUtil.refreshTable(clientTable, clients);
    }

    /**
     * Extracts data from the currently selected row in the table
     * and populates the corresponding text fields in the form.
     */
    private void populateFieldsFromSelection() {
        int row = clientTable.getSelectedRow();
        if (row < 0) return;
        idField.setText(String.valueOf(clientTable.getValueAt(row, 0)));
        nameField.setText(String.valueOf(clientTable.getValueAt(row, 1)));
        emailField.setText(String.valueOf(clientTable.getValueAt(row, 2)));
        addressField.setText(String.valueOf(clientTable.getValueAt(row, 3)));
        phoneField.setText(String.valueOf(clientTable.getValueAt(row, 4)));
    }

    /**
     * Extracts data from the input fields, instantiates a new object,
     * and passes it to the Business Logic layer for insertion.
     */
    private void addClient() {
        try {
            Client client = new Client(
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );
            clientBLL.insertClient(client);
            JOptionPane.showMessageDialog(this, "Client added successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Extracts data from the input fields, instantiates an updated object,
     * and passes it to the Business Logic layer for updating.
     */
    private void editClient() {
        try {
            if (idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a client to edit.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Client client = new Client(
                    Integer.parseInt(idField.getText().trim()),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );
            clientBLL.updateClient(client);
            JOptionPane.showMessageDialog(this, "Client updated successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Identifies the selected object by its ID and prompts the Business Logic layer to delete it.
     */
    private void deleteClient() {
        try {
            if (idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a client to delete.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(idField.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete client with id " + id + "?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                clientBLL.deleteClient(id);
                JOptionPane.showMessageDialog(this, "Client deleted.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all text input fields and deselects any highlighted table rows.
     */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        emailField.setText("");
        phoneField.setText("");
        clientTable.clearSelection();
    }
}
