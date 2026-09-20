package ui;

import db.UserDAO;
import model.User;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * RegisterFrame - New User Registration Screen
 * Demonstrates OOP: Inheritance, Encapsulation, Exception Handling
 */
public class RegisterFrame extends JFrame {

    private JTextField     nameField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton        registerBtn;
    private JButton        backToLoginBtn;
    private JLabel         statusLabel;

    private UserDAO   userDAO;
    private JFrame    parentFrame; // reference to LoginFrame

    public RegisterFrame(JFrame parent) {
        this.parentFrame = parent;
        this.userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Expense Manager – Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        // Left branding panel (reuse same style as Login)
        root.add(createLeftPanel(), BorderLayout.WEST);
        root.add(createFormPanel(), BorderLayout.CENTER);

        setContentPane(root);

        // When this window closes, show login again
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (parentFrame != null) parentFrame.setVisible(true);
            }
        });

        setVisible(true);
    }

    // ── Left branding panel ───────────────────────────────────────────────────
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(99, 102, 241),
                    0, getHeight(), new Color(37, 99, 235)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(360, 580));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel icon = new JLabel("📝");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(icon, gbc);

        JLabel title = new JLabel("Join ExpenseTracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, gbc);

        JLabel sub = new JLabel("<html><div style='text-align:center;'>Create your account and start<br>managing your finances today!</div></html>");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(186, 230, 253));
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(sub, gbc);

        return panel;
    }

    // ── Registration form panel ───────────────────────────────────────────────
    private JPanel createFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_MAIN);

        JPanel form = new JPanel();
        form.setBackground(UITheme.BG_CARD);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(35, 40, 35, 40)
        ));
        form.setPreferredSize(new Dimension(380, 480));

        // Title
        JLabel titleLbl = new JLabel("Create Account");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(titleLbl);
        form.add(Box.createVerticalStrut(4));

        JLabel subLbl = new JLabel("Fill in the details below to register");
        subLbl.setFont(UITheme.FONT_BODY);
        subLbl.setForeground(UITheme.TEXT_SECONDARY);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(subLbl);
        form.add(Box.createVerticalStrut(22));

        // Name
        nameField = UITheme.textField("");
        form.add(makeFieldBlock("Full Name", nameField));
        form.add(Box.createVerticalStrut(12));

        // Email
        emailField = UITheme.textField("");
        form.add(makeFieldBlock("Email Address", emailField));
        form.add(Box.createVerticalStrut(12));

        // Password
        passwordField = UITheme.passwordField();
        form.add(makeFieldBlock("Password (min. 6 chars)", passwordField));
        form.add(Box.createVerticalStrut(12));

        // Confirm password
        confirmPasswordField = UITheme.passwordField();
        form.add(makeFieldBlock("Confirm Password", confirmPasswordField));
        form.add(Box.createVerticalStrut(8));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(statusLabel);
        form.add(Box.createVerticalStrut(14));

        // Register button
        registerBtn = UITheme.primaryButton("Create Account");
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.BTN_HEIGHT));
        form.add(registerBtn);
        form.add(Box.createVerticalStrut(10));

        // Back to login link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linkPanel.setBackground(UITheme.BG_CARD);
        linkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel alreadyLbl = new JLabel("Already have an account?  ");
        alreadyLbl.setFont(UITheme.FONT_BODY);
        alreadyLbl.setForeground(UITheme.TEXT_SECONDARY);
        backToLoginBtn = new JButton("Sign In");
        backToLoginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backToLoginBtn.setForeground(UITheme.PRIMARY);
        backToLoginBtn.setBorderPainted(false);
        backToLoginBtn.setContentAreaFilled(false);
        backToLoginBtn.setFocusPainted(false);
        backToLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkPanel.add(alreadyLbl);
        linkPanel.add(backToLoginBtn);
        form.add(linkPanel);

        outer.add(form);

        // ── Event Listeners ────────────────────────────────────────────────────
        registerBtn.addActionListener(e -> handleRegister());
        backToLoginBtn.addActionListener(e -> goBackToLogin());

        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleRegister();
            }
        };
        nameField.addKeyListener(enterKey);
        emailField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
        confirmPasswordField.addKeyListener(enterKey);

        return outer;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private JPanel makeFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBackground(UITheme.BG_CARD);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        JLabel lbl = UITheme.label(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.add(lbl);
        block.add(Box.createVerticalStrut(4));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.FIELD_HEIGHT));
        block.add(field);
        return block;
    }

    // ── Business Logic ────────────────────────────────────────────────────────
    private void handleRegister() {
        String name            = nameField.getText().trim();
        String email           = emailField.getText().trim();
        String password        = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation chain
        if (!ValidationUtils.isValidName(name)) {
            setStatus("⚠  Please enter your full name (at least 2 characters).", UITheme.DANGER);
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            setStatus("⚠  Please enter a valid email address.", UITheme.DANGER);
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            setStatus("⚠  Password must be at least 6 characters.", UITheme.DANGER);
            return;
        }
        if (!ValidationUtils.passwordsMatch(password, confirmPassword)) {
            setStatus("⚠  Passwords do not match.", UITheme.DANGER);
            return;
        }

        // Check for duplicate email
        if (userDAO.emailExists(email)) {
            setStatus("⚠  This email is already registered. Please sign in.", UITheme.DANGER);
            return;
        }

        // Create user
        User newUser = new User(name, email, password);
        boolean success = userDAO.registerUser(newUser);

        if (success) {
            setStatus("✓ Account created successfully! Redirecting to login...", UITheme.SUCCESS);
            JOptionPane.showMessageDialog(this,
                "Registration successful!\nYou can now sign in with your credentials.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            goBackToLogin();
        } else {
            setStatus("✗ Registration failed. Please try again.", UITheme.DANGER);
        }
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private void goBackToLogin() {
        if (parentFrame != null) parentFrame.setVisible(true);
        dispose();
    }
}
