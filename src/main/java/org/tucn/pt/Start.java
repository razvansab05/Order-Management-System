package org.tucn.pt;

import org.tucn.pt.presentation.Controller;

/**
 * The entry point for the Orders Management Application.
 */
public class Start {

    /**
     * The main method that bootstraps the application controller.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args){
        Controller controller = new Controller();
        controller.start();
    }
}
