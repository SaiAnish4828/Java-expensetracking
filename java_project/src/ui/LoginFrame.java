package ui;

import db.UserDAO;
import model.User;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * LoginFrame - User Login Screen
 * Demonstrates OOP: Inheritance (extends JFrame), Event Handling
 */
public class LoginFrame extends JFrame {

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JButton        loginBtn;
    private JButton        registerBtn;
    private JLabel         statusLabel;
    private UserDAO        userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Expense Manager – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // Root split panel
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        // ── LEFT PANEL (branding) ──────────────────────────────────────────────
        JPanel leftPanel = createLeftBrandPanel();
        root.add(leftPanel, BorderLayout.WEST);

        // ── RIGHT PANEL (form) ────────────────────────────────────────────────
        JPanel rightPanel = createRightFormPanel();
        root.add(rightPanel, BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // ── Left branding panel ───────────────────────────────────────────────────
    private JPanel createLeftBrandPanel() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(37, 99, 235),
                    0, getHeight(), new Color(99, 102, 241)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(360, 560));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        // App Icon (wallet emoji rendered as label)
        JLabel icon = new JLabel("💰");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(icon, gbc);

        JLabel appName = new JLabel("ExpenseTracker");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appName.setForeground(Color.WHITE);
        panel.add(appName, gbc);

        JLabel tagline = new JLabel("<html><div style='text-align:center;'>Track your expenses.<br>Take control of your finances.</div></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(191, 219, 254));
        tagline.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(tagline, gbc);

        // Feature bullets
        String[] features = {"✓  User Login & Registration", "✓  Add & Manage Expenses", "✓  Expense Categorization"};
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fl.setForeground(new Color(186, 230, 253));
            panel.add(fl, gbc);
        }

        return panel;
    }

    // ── Right form panel ──────────────────────────────────────────────────────
    private JPanel createRightFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_MAIN);

        JPanel form = new JPanel();
        form.setBackground(UITheme.BG_CARD);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        form.setPreferredSize(new Dimension(380, 420));

        // Title
        JLabel titleLbl = new JLabel("Welcome Back!");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(titleLbl);
        form.add(Box.createVerticalStrut(4));

        JLabel subLbl = new JLabel("Sign in to your account");
        subLbl.setFont(UITheme.FONT_BODY);
        subLbl.setForeground(UITheme.TEXT_SECONDARY);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(subLbl);
        form.add(Box.createVerticalStrut(28));

        // Email
        form.add(makeFieldBlock("Email Address", emailField = UITheme.textField("")));
        form.add(Box.createVerticalStrut(14));

        // Password
        passwordField = UITheme.passwordField();
        form.add(makeFieldBlock("Password", passwordField));
        form.add(Box.createVerticalStrut(6));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(statusLabel);
        form.add(Box.createVerticalStrut(16));

        // Login button
        loginBtn = UITheme.primaryButton("Sign In");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.BTN_HEIGHT));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(12));

        // Register link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linkPanel.setBackground(UITheme.BG_CARD);
        linkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noAccLbl = new JLabel("Don't have an account?  ");
        noAccLbl.setFont(UITheme.FONT_BODY);
        noAccLbl.setForeground(UITheme.TEXT_SECONDARY);
        registerBtn = new JButton("Create Account");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerBtn.setForeground(UITheme.PRIMARY);
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkPanel.add(noAccLbl);
        linkPanel.add(registerBtn);
        form.add(linkPanel);

        outer.add(form);

        // ── Event Listeners ────────────────────────────────────────────────────
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> openRegister());

        // Allow Enter key to submit
        KeyAdapter enterListener = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        };
        emailField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        return outer;
    }

    // ── Helper: label + field stacked ────────────────────────────────────────
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
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validation
        if (!ValidationUtils.isValidEmail(email)) {
            statusLabel.setText("⚠  Please enter a valid email address.");
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            statusLabel.setText("⚠  Password must be at least 6 characters.");
            return;
        }

        // DB call
        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(UITheme.TEXT_SECONDARY);

        User user = userDAO.loginUser(email, password);
        if (user != null) {
            statusLabel.setText("✓ Login successful!");
            statusLabel.setForeground(UITheme.SUCCESS);
            // Open Dashboard and close Login
            SwingUtilities.invokeLater(() -> {
                new DashboardFrame(user);
                dispose();
            });
        } else {
            statusLabel.setText("✗ Invalid email or password.");
            statusLabel.setForeground(UITheme.DANGER);
            passwordField.setText("");
        }
    }

    private void openRegister() {
        new RegisterFrame(this);
        setVisible(false);
    }
}
