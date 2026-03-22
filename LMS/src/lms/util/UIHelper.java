package lms.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * UIHelper.java
 * --------------
 * Utility class providing reusable Swing helper methods.
 * Keeps UI code DRY (Don't Repeat Yourself).
 *
 * Contains: color palette, font helpers, button factory,
 *           table styling, and common dialog methods.
 */
public class UIHelper {

    // ─── Colour Palette ──────────────────────────────────────
    public static final Color PRIMARY      = new Color(41,  128, 185);   // blue
    public static final Color PRIMARY_DARK = new Color(21,  100, 160);
    public static final Color SUCCESS      = new Color(39,  174, 96);    // green
    public static final Color DANGER       = new Color(192, 57,  43);    // red
    public static final Color WARNING      = new Color(243, 156, 18);    // orange
    public static final Color LIGHT_BG     = new Color(236, 240, 241);   // light grey
    public static final Color WHITE        = Color.WHITE;
    public static final Color DARK_TEXT    = new Color(44,  62,  80);    // near-black

    // ─── Fonts ────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_NORMAL  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);

    // ─── Button Factory ───────────────────────────────────────

    /** Creates a primary (blue) styled button */
    public static JButton primaryButton(String text) {
        return styledButton(text, PRIMARY, WHITE);
    }

    /** Creates a success (green) styled button */
    public static JButton successButton(String text) {
        return styledButton(text, SUCCESS, WHITE);
    }

    /** Creates a danger (red) styled button */
    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER, WHITE);
    }

    /** Creates a warning (orange) styled button */
    public static JButton warningButton(String text) {
        return styledButton(text, WARNING, WHITE);
    }

    /** Generic styled button factory */
    private static JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = bg;
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(original);
            }
        });
        return btn;
    }

    // ─── Label Factory ────────────────────────────────────────

    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(DARK_TEXT);
        return lbl;
    }

    public static JLabel headerLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(DARK_TEXT);
        return lbl;
    }

    public static JLabel normalLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_NORMAL);
        lbl.setForeground(DARK_TEXT);
        return lbl;
    }

    // ─── Text Field Factory ───────────────────────────────────

    public static JTextField textField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_NORMAL);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    public static JPasswordField passwordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(FONT_NORMAL);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(6, 8, 6, 8)
        ));
        return pf;
    }

    public static JTextArea textArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_NORMAL);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(6, 8, 6, 8));
        return ta;
    }

    // ─── Table Styling ────────────────────────────────────────

    /** Applies consistent styling to a JTable */
    public static void styleTable(JTable table) {
        table.setFont(FONT_NORMAL);
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(DARK_TEXT);
        table.setShowVerticalLines(false);

        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BUTTON);
        header.setBackground(PRIMARY);
        header.setForeground(WHITE);
        header.setReorderingAllowed(false);

        // Centre-align all columns
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centre);
        }
    }

    // ─── Panel Helpers ────────────────────────────────────────

    /** Creates a panel with padding */
    public static JPanel paddedPanel(int pad) {
        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(pad, pad, pad, pad));
        p.setBackground(WHITE);
        return p;
    }

    /** Creates a titled border panel */
    public static JPanel titledPanel(String title) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY, 1), title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            FONT_BUTTON, PRIMARY
        ));
        p.setBackground(WHITE);
        return p;
    }

    // ─── Dialog Helpers ───────────────────────────────────────

    public static void showSuccess(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ─── Window Helper ────────────────────────────────────────

    /** Centres a window on screen */
    public static void centreWindow(Window w) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        w.setLocation((screen.width  - w.getWidth())  / 2,
                      (screen.height - w.getHeight()) / 2);
    }

    /** Applies system look-and-feel */
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }
    }
}
