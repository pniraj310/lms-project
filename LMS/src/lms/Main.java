package lms;

import lms.ui.LoginFrame;
import lms.util.UIHelper;

import javax.swing.*;

/**
 * Main.java
 * ----------
 * Entry point of the LMS application.
 *
 * All Swing UI must run on the Event Dispatch Thread (EDT).
 * SwingUtilities.invokeLater() ensures this.
 */
public class Main {

    public static void main(String[] args) {
        // Apply system look-and-feel before any Swing component is created
        UIHelper.setLookAndFeel();

        // Launch UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== Learning Management System ===");
            System.out.println("Starting application...");
            new LoginFrame();
        });
    }
}
