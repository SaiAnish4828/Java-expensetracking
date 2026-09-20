package ui;

import db.ExpenseDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * DashboardFrame - Main application window after login
 * Contains sidebar navigation and content panels
 * Demonstrates OOP: Composition, CardLayout navigation
 */
public class DashboardFrame extends JFrame {

    private User       currentUser;
    private ExpenseDAO expenseDAO;

    // Navigation buttons
    private JButton btnDashboard;
    private JButton btnExpenses;
    private JButton btnCategories;
    private JButton btnLogout;

    // Content area using CardLayout
    private JPanel      contentArea;
    private CardLayout  cardLayout;

    // Panels
    private DashboardHomePanel  homePanel;
    private ExpensePanel        expensePanel;
    private CategoryPanel       categoryPanel;

    // Currently active nav button
    private JButton activeNavBtn;

    // Header title label (updated on navigation)
    private JLabel pageTitleLabel;

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.expenseDAO  = new ExpenseDAO();
        initUI();
    }

    private void initUI() {
        setTitle("ExpenseTracker – " + currentUser.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_MAIN);

        // ── Sidebar ────────────────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // ── Main content area ──────────────────────────────────────────────────
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(UITheme.BG_MAIN);

        homePanel     = new DashboardHomePanel(currentUser, this);
        expensePanel  = new ExpensePanel(currentUser);
        categoryPanel = new CategoryPanel(currentUser);

        contentArea.add(homePanel,     "dashboard");
        contentArea.add(expensePanel,  "expenses");
        contentArea.add(categoryPanel, "categories");

        // Top header bar
        JPanel topBar = buildTopBar();
        root.add(topBar,       BorderLayout.NORTH);
        root.add(contentArea,  BorderLayout.CENTER);

        setContentPane(root);

        // Show dashboard by default
        navigateTo("dashboard", btnDashboard);

        setVisible(true);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // App logo area
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        logoArea.setBackground(UITheme.BG_SIDEBAR);
        logoArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel logoIcon = new JLabel("💰");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        JLabel logoText = new JLabel("ExpenseTracker");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoText.setForeground(Color.WHITE);
        logoArea.add(logoIcon);
        logoArea.add(logoText);
        sidebar.add(logoArea);

        // Divider
        sidebar.add(createSidebarDivider());

        // Nav section label
        sidebar.add(createNavSectionLabel("MAIN MENU"));

        // Navigation buttons
        btnDashboard  = createNavButton("🏠  Dashboard",   "dashboard");
        btnExpenses   = createNavButton("💳  Expenses",    "expenses");
        btnCategories = createNavButton("🏷️  Categories",  "categories");

        sidebar.add(btnDashboard);
        sidebar.add(btnExpenses);
        sidebar.add(btnCategories);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createSidebarDivider());

        // User info at bottom
        JPanel userArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        userArea.setBackground(UITheme.BG_SIDEBAR);
        userArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel avatarLbl = new JLabel("👤");
        avatarLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setBackground(UITheme.BG_SIDEBAR);
        JLabel nameLbl = new JLabel(currentUser.getName());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLbl.setForeground(Color.WHITE);
        JLabel emailLbl = new JLabel(currentUser.getEmail());
        emailLbl.setFont(UITheme.FONT_SMALL);
        emailLbl.setForeground(UITheme.SIDEBAR_TEXT);
        userInfo.add(nameLbl);
        userInfo.add(emailLbl);
        userArea.add(avatarLbl);
        userArea.add(userInfo);
        sidebar.add(userArea);

        // Logout button
        btnLogout = createNavButton("🚪  Logout", "logout");
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(8));

        return sidebar;
    }

    private JButton createNavButton(String text, String card) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == activeNavBtn) {
                    g2.setColor(UITheme.SIDEBAR_ACTIVE);
                    g2.fillRoundRect(6, 2, getWidth()-12, getHeight()-4, 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(6, 2, getWidth()-12, getHeight()-4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(UITheme.SIDEBAR_TEXT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        btn.addActionListener(e -> {
            if ("logout".equals(card)) {
                handleLogout();
            } else {
                navigateTo(card, btn);
                refreshPanel(card);
            }
        });
        return btn;
    }

    private JPanel createSidebarDivider() {
        JPanel div = new JPanel();
        div.setBackground(new Color(51, 65, 85));
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 1));
        return div;
    }

    private JLabel createNavSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setBorder(BorderFactory.createEmptyBorder(14, 18, 6, 18));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Top header bar ────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        bar.setPreferredSize(new Dimension(0, 56));

        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setName("pageTitle");
        pageTitle.setFont(UITheme.FONT_SUBTITLE);
        pageTitle.setForeground(UITheme.TEXT_PRIMARY);
        bar.add(pageTitle, BorderLayout.WEST);
        pageTitleLabel = pageTitle;

        // Date display on the right
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        JLabel dateLbl = new JLabel(sdf.format(new Date()));
        dateLbl.setFont(UITheme.FONT_BODY);
        dateLbl.setForeground(UITheme.TEXT_SECONDARY);
        bar.add(dateLbl, BorderLayout.EAST);

        return bar;
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    public void navigateTo(String card, JButton navBtn) {
        activeNavBtn = navBtn;

        // Update page title in top bar
        String title = switch (card) {
            case "dashboard"   -> "Dashboard";
            case "expenses"    -> "Expense Management";
            case "categories"  -> "Expense Categories";
            default            -> "Dashboard";
        };

        if (pageTitleLabel != null) {
            pageTitleLabel.setText(title);
        }

        cardLayout.show(contentArea, card);
        repaint();
    }

    // Refresh panel data when navigating
    private void refreshPanel(String card) {
        switch (card) {
            case "dashboard"  -> homePanel.refresh();
            case "expenses"   -> expensePanel.refresh();
            case "categories" -> categoryPanel.refresh();
        }
    }

    // Convenience: navigate from child panels
    public void goToExpenses() { navigateTo("expenses", btnExpenses); expensePanel.refresh(); }

    // ── Logout ────────────────────────────────────────────────────────────────
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}
