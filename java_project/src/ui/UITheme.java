package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * UITheme - Centralized UI styling constants
 * Ensures consistent look and feel across all frames
 * Demonstrates OOP: Constants class, Reusability
 */
public class UITheme {

    // ── Color Palette ─────────────────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(37,  99,  235);  // Blue-600
    public static final Color PRIMARY_DARK   = new Color(29,  78,  216);  // Blue-700
    public static final Color PRIMARY_LIGHT  = new Color(219, 234, 254);  // Blue-100
    public static final Color SECONDARY      = new Color(99,  102, 241);  // Indigo
    public static final Color SUCCESS        = new Color(34,  197, 94);   // Green
    public static final Color DANGER         = new Color(239, 68,  68);   // Red
    public static final Color WARNING        = new Color(245, 158, 11);   // Amber
    public static final Color TEXT_PRIMARY   = new Color(15,  23,  42);   // Slate-900
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);  // Slate-500
    public static final Color BG_MAIN        = new Color(248, 250, 252);  // Slate-50
    public static final Color BG_CARD        = Color.WHITE;
    public static final Color BG_SIDEBAR     = new Color(15,  23,  42);   // Slate-900
    public static final Color SIDEBAR_TEXT   = new Color(203, 213, 225);  // Slate-300
    public static final Color SIDEBAR_ACTIVE = new Color(37,  99,  235);  // Blue-600
    public static final Color BORDER         = new Color(226, 232, 240);  // Slate-200
    public static final Color TABLE_HEADER   = new Color(241, 245, 249);  // Slate-100
    public static final Color TABLE_ROW_ALT  = new Color(248, 250, 252);  // Slate-50

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font FONT_SUBTITLE  = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_HEADING   = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_LABEL     = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BTN       = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_MONO      = new Font("Consolas",  Font.PLAIN, 13);

    // ── Dimensions ────────────────────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH    = 220;
    public static final int FIELD_HEIGHT     = 38;
    public static final int BTN_HEIGHT       = 38;
    public static final int CORNER_RADIUS    = 8;

    // ── Factory Methods ───────────────────────────────────────────────────────

    /** Styled primary button */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY_DARK);
                } else {
                    g2.setColor(PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, BTN_HEIGHT));
        return btn;
    }

    /** Styled danger/delete button */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(220, 38, 38) : DANGER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Styled secondary/outline button */
    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(TEXT_PRIMARY);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Styled text field */
    public static JTextField textField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, FIELD_HEIGHT));
        return field;
    }

    /** Styled password field */
    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, FIELD_HEIGHT));
        return field;
    }

    /** Styled combo box */
    public static <T> JComboBox<T> comboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new LineBorder(BORDER, 1, true));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, FIELD_HEIGHT));
        return combo;
    }

    /** Form label */
    public static JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    /** Card panel with shadow-like border */
    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    /** Apply global Look & Feel settings */
    public static void applyGlobalLAF() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",         BG_MAIN);
        UIManager.put("Label.font",               FONT_BODY);
        UIManager.put("Button.font",              FONT_BTN);
        UIManager.put("TextField.font",           FONT_BODY);
        UIManager.put("PasswordField.font",       FONT_BODY);
        UIManager.put("ComboBox.font",            FONT_BODY);
        UIManager.put("Table.font",               FONT_BODY);
        UIManager.put("TableHeader.font",         FONT_LABEL);
        UIManager.put("TableHeader.background",   TABLE_HEADER);
        UIManager.put("Table.selectionBackground",PRIMARY_LIGHT);
        UIManager.put("Table.selectionForeground",TEXT_PRIMARY);
        UIManager.put("Table.gridColor",          BORDER);
        UIManager.put("ScrollBar.width",          8);
        UIManager.put("OptionPane.messageFont",   FONT_BODY);
        UIManager.put("OptionPane.buttonFont",    FONT_BTN);
    }
}
