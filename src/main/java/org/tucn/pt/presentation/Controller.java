package org.tucn.pt.presentation;

import javax.swing.*;

/**
 * Initializes and starts the application's graphical user interface.
 */
public class Controller {
    /**
     * Launches the MainView on the Swing Event Dispatch Thread.
     */
    public void start(){
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}
