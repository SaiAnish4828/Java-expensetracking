package ui;

import db.ExpenseDAO;
import model.Expense;
import model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * DashboardHomePanel - Overview panel shown after login
 * Shows summary stats, category breakdown and recent expenses
 * Demonstrates OOP: Composition, Inner classes
 */
public class DashboardHomePanel extends JPanel {

    private User             currentUser;
    private ExpenseDAO       expenseDAO;
    private DashboardFrame   parentFrame;

    // Stat card labels (so we can refresh)
    private JLabel lblTotalSpent;
    private JLabel lblMonthlyTotal;
    private JLabel lblTotalExpenses;
    private JLabel lblTopCategory;

    // Recent expenses table
    private DefaultTableModel recentTableModel;

    // Category breakdown panel
    private JPanel categoryBreakdownPanel;

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public DashboardHomePanel(User user, DashboardFrame parent) {
        this.currentUser = user;
        this.expenseDAO  = new ExpenseDAO();
        this.parentFrame = parent;
        initUI();
        refresh();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ── Welcome header ─────────────────────────────────────────────────────
        JLabel welcomeLbl = new JLabel("Good " + getGreeting() + ", " + currentUser.getName() + "! 👋");
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLbl.setForeground(UITheme.TEXT_PRIMARY);
        welcomeLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        add(welcomeLbl, BorderLayout.NORTH);

        // ── Scroll content ─────────────────────────────────────────────────────
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(UITheme.BG_MAIN);

        // 1) Stat cards row
        scrollContent.add(buildStatCardsRow());
        scrollContent.add(Box.createVerticalStrut(20));

        // 2) Middle row: recent expenses + category breakdown
        JPanel middleRow = new JPanel(new GridLayout(1, 2, 16, 0));
        middleRow.setBackground(UITheme.BG_MAIN);
        middleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        middleRow.add(buildRecentExpensesCard());
        middleRow.add(buildCategoryBreakdownCard());
        scrollContent.add(middleRow);
        scrollContent.add(Box.createVerticalStrut(20));

        // 3) Quick action buttons
        scrollContent.add(buildQuickActions());

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setBackground(UITheme.BG_MAIN);
        scroll.getViewport().setBackground(UITheme.BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Stat Cards Row ────────────────────────────────────────────────────────
    private JPanel buildStatCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setBackground(UITheme.BG_MAIN);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblTotalSpent    = new JLabel("₹0.00");
        lblMonthlyTotal  = new JLabel("₹0.00");
        lblTotalExpenses = new JLabel("0");
        lblTopCategory   = new JLabel("—");

        row.add(buildStatCard("💰", "Total Spent",        lblTotalSpent,    UITheme.PRIMARY));
        row.add(buildStatCard("📅", "This Month",         lblMonthlyTotal,  UITheme.SUCCESS));
        row.add(buildStatCard("📊", "Total Entries",      lblTotalExpenses, UITheme.WARNING));
        row.add(buildStatCard("🏆", "Top Category",       lblTopCategory,   UITheme.SECONDARY));

        return row;
    }

    private JPanel buildStatCard(String icon, String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        // Left accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(4, 0));
        card.add(accentBar, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setBackground(UITheme.BG_CARD);

        JLabel iconLbl = new JLabel(icon + "  " + title);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        iconLbl.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);

        info.add(iconLbl);
        info.add(valueLabel);
        info.add(new JLabel()); // spacer
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    // ── Recent Expenses Card ──────────────────────────────────────────────────
    private JPanel buildRecentExpensesCard() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(0, 10));

        JLabel hdr = new JLabel("Recent Expenses");
        hdr.setFont(UITheme.FONT_HEADING);
        hdr.setForeground(UITheme.TEXT_PRIMARY);
        card.add(hdr, BorderLayout.NORTH);

        String[] cols = {"Date", "Description", "Category", "Amount"};
        recentTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(recentTableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(UITheme.BORDER);
        table.setBackground(UITheme.BG_CARD);
        table.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        table.getTableHeader().setFont(UITheme.FONT_LABEL);
        table.getTableHeader().setBackground(UITheme.TABLE_HEADER);
        table.getTableHeader().setReorderingAllowed(false);

        // Right-align amount column
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightAlign);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UITheme.BORDER, 1));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Category Breakdown Card ───────────────────────────────────────────────
    private JPanel buildCategoryBreakdownCard() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(0, 10));

        JLabel hdr = new JLabel("Spending by Category");
        hdr.setFont(UITheme.FONT_HEADING);
        hdr.setForeground(UITheme.TEXT_PRIMARY);
        card.add(hdr, BorderLayout.NORTH);

        categoryBreakdownPanel = new JPanel();
        categoryBreakdownPanel.setLayout(new BoxLayout(categoryBreakdownPanel, BoxLayout.Y_AXIS));
        categoryBreakdownPanel.setBackground(UITheme.BG_CARD);

        JScrollPane scroll = new JScrollPane(categoryBreakdownPanel);
        scroll.setBorder(new LineBorder(UITheme.BORDER, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Quick Actions ─────────────────────────────────────────────────────────
    private JPanel buildQuickActions() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel hdr = new JLabel("Quick Actions");
        hdr.setFont(UITheme.FONT_HEADING);
        hdr.setForeground(UITheme.TEXT_PRIMARY);
        card.add(hdr, BorderLayout.NORTH);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setBackground(UITheme.BG_CARD);

        JButton addExpenseBtn = UITheme.primaryButton("➕  Add Expense");
        addExpenseBtn.addActionListener(e -> parentFrame.goToExpenses());

        JButton viewAllBtn = UITheme.secondaryButton("📋  View All Expenses");
        viewAllBtn.addActionListener(e -> parentFrame.goToExpenses());

        btns.add(addExpenseBtn);
        btns.add(viewAllBtn);
        card.add(btns, BorderLayout.CENTER);
        return card;
    }

    // ── Refresh Data ──────────────────────────────────────────────────────────
    public void refresh() {
        int userId = currentUser.getId();
        Calendar cal = Calendar.getInstance();

        // Total spent (all time)
        double totalSpent = expenseDAO.getTotalByUser(userId);
        lblTotalSpent.setText(CURRENCY.format(totalSpent));

        // Monthly total
        double monthTotal = expenseDAO.getTotalByMonth(userId, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
        lblMonthlyTotal.setText(CURRENCY.format(monthTotal));

        // All expenses
        List<Expense> all = expenseDAO.getExpensesByUser(userId);
        lblTotalExpenses.setText(String.valueOf(all.size()));

        // Top category
        List<Object[]> catTotals = expenseDAO.getTotalByCategory(userId);
        if (!catTotals.isEmpty()) {
            lblTopCategory.setText((String) catTotals.get(0)[0]);
        }

        // Recent 7 expenses in table
        recentTableModel.setRowCount(0);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yy");
        int count = 0;
        for (Expense exp : all) {
            if (count++ >= 7) break;
            recentTableModel.addRow(new Object[]{
                sdf.format(exp.getDate()),
                exp.getDescription() != null ? exp.getDescription() : "—",
                exp.getCategoryName(),
                CURRENCY.format(exp.getAmount())
            });
        }

        // Category breakdown bars
        categoryBreakdownPanel.removeAll();
        if (catTotals.isEmpty()) {
            JLabel empty = new JLabel("No expenses recorded yet.");
            empty.setFont(UITheme.FONT_BODY);
            empty.setForeground(UITheme.TEXT_SECONDARY);
            empty.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            categoryBreakdownPanel.add(empty);
        } else {
            double max = (double) catTotals.get(0)[1];
            Color[] barColors = {
                UITheme.PRIMARY, UITheme.DANGER, UITheme.SUCCESS,
                UITheme.WARNING, UITheme.SECONDARY,
                new Color(20, 184, 166), new Color(249, 115, 22)
            };
            int ci = 0;
            for (Object[] row : catTotals) {
                String catName = (String) row[0];
                double amount  = (double) row[1];
                int    pct     = (int) (amount / max * 100);
                Color  color   = barColors[ci % barColors.length];
                categoryBreakdownPanel.add(buildCategoryBar(catName, amount, pct, color));
                ci++;
            }
        }
        categoryBreakdownPanel.revalidate();
        categoryBreakdownPanel.repaint();
    }

    private JPanel buildCategoryBar(String name, double amount, int pct, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 2));
        row.setBackground(UITheme.BG_CARD);
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(UITheme.BG_CARD);
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(UITheme.FONT_BODY);
        JLabel amtLbl = new JLabel(CURRENCY.format(amount));
        amtLbl.setFont(UITheme.FONT_LABEL);
        amtLbl.setForeground(color);
        topRow.add(nameLbl, BorderLayout.WEST);
        topRow.add(amtLbl,  BorderLayout.EAST);
        row.add(topRow, BorderLayout.NORTH);

        // Progress bar
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setStringPainted(false);
        bar.setForeground(color);
        bar.setBackground(UITheme.BORDER);
        bar.setBorder(null);
        bar.setPreferredSize(new Dimension(0, 8));
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Morning";
        if (hour < 17) return "Afternoon";
        return "Evening";
    }
}
