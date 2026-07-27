package lms.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * UIHelper — Reusable Swing styling utilities
 * Keywords: Utility class, Abstraction, Code Reusability
 */
public class UIHelper {

    // ── Color Palette ──
    public static final Color PRIMARY    = new Color(52, 152, 219);   // Blue
    public static final Color SUCCESS    = new Color(46, 204, 113);   // Green
    public static final Color DANGER     = new Color(231, 76, 60);    // Red
    public static final Color WARNING    = new Color(241, 196, 15);   // Yellow
    public static final Color DARK       = new Color(44, 62, 80);     // Dark navy
    public static final Color LIGHT_BG   = new Color(236, 240, 241);  // Light gray bg
    public static final Color WHITE      = Color.WHITE;

    // ── Fonts ──
    public static final Font TITLE_FONT   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY_FONT    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT   = new Font("Segoe UI", Font.PLAIN, 12);

    /** Creates a styled primary button */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY);
        btn.setForeground(WHITE);
        btn.setFont(BODY_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        return btn;
    }

    /** Creates a styled danger (red) button */
    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        return btn;
    }

    /** Creates a styled success (green) button */
    public static JButton successButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(SUCCESS);
        return btn;
    }

    /** Creates a styled text field */
    public static JTextField styledField(int cols) {
        JTextField field = new JTextField(cols);
        field.setFont(BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    /** Creates a styled password field */
    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField field = new JPasswordField(cols);
        field.setFont(BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    /** Creates a styled label */
    public static JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(BODY_FONT);
        lbl.setForeground(DARK);
        return lbl;
    }

    /** Creates a heading label */
    public static JLabel heading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(HEADING_FONT);
        lbl.setForeground(DARK);
        return lbl;
    }

    /** Creates a title label */
    public static JLabel title(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(TITLE_FONT);
        lbl.setForeground(WHITE);
        return lbl;
    }

    /** Creates a header panel with colored background */
    public static JPanel headerPanel(String titleText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.add(title(titleText), BorderLayout.CENTER);
        return panel;
    }

    /** Creates a non-editable JTable with default styling */
    public static JTable styledTable(String[] columns, Object[][] data) {
        JTable table = new JTable(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setFont(BODY_FONT);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(WHITE);
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        return table;
    }

    /** Shows an info dialog */
    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Shows an error dialog */
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Shows a confirm dialog */
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Sets a consistent look and feel */
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default
        }
    }
}
