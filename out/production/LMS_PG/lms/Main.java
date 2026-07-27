package lms;

import lms.db.DBConnection;
import lms.ui.LoginFrame;

import javax.swing.*;

/**
 * Main — Application Entry Point
 * Keywords: SwingUtilities.invokeLater, Event Dispatch Thread (EDT)
 *
 * WHY invokeLater?
 * Swing is NOT thread-safe. All UI components must be created and
 * modified on the Event Dispatch Thread (EDT) to avoid race conditions.
 * SwingUtilities.invokeLater() schedules the UI creation on the EDT.
 */
public class Main {
    public static void main(String[] args) {
        // Ensure DB connection on startup
        DBConnection.getConnection();

        // Launch UI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });

        // Close DB connection on app exit
        Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::closeConnection));
    }
}
