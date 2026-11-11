package org.tucn.pt.presentation;

import org.tucn.pt.model.Product;

import javax.swing.*;
import java.awt.*;

/**
 * The main graphical user interface window for the Orders Management Application.
 * Contains tabs for managing Clients, Products, and Orders.
 */
public class MainView extends JFrame {
    public MainView(){
        setTitle("Orders Management Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);
        initComponents();
    }

    /**
     * Initializes and configures the main components of the window,
     * including the tabbed panes for Clients, Products, and Orders.
     */
    private void initComponents(){
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabbedPane.addTab("Clients", new ClientView());
        tabbedPane.addTab("Products", new ProductView());
        tabbedPane.addTab("Orders", new OrderView());

        add(tabbedPane);
    }
}
