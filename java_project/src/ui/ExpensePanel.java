package ui;

import db.CategoryDAO;
import db.ExpenseDAO;
import model.Category;
import model.Expense;
import model.User;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

/**
 * ExpensePanel - Full expense management panel (Add / Edit / Delete / View)
 * Demonstrates OOP: Composition, Inner Dialogs, TableModel
 */
public class ExpensePanel extends JPanel {

    private User        currentUser;
    private ExpenseDAO  expenseDAO;
    private CategoryDAO categoryDAO;

    // Table
    private JTable            expenseTable;
    private DefaultTableModel tableModel;

    // Filter controls
    private JComboBox<String>   filterMonth;
    private JComboBox<String>   filterYear;
    private JComboBox<Category> filterCategory;

    // Summary label
    private JLabel totalLabel;

    // Cached data
    private List<Expense>  allExpenses = new ArrayList<>();

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final String[] MONTHS = {
        "All Months","January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    public ExpensePanel(User user) {
        this.currentUser = user;
        this.expenseDAO  = new ExpenseDAO();
        this.categoryDAO = new CategoryDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ── Top toolbar ────────────────────────────────────────────────────────
        add(buildToolbar(), BorderLayout.NORTH);

        // ── Expense table ──────────────────────────────────────────────────────
        add(buildTablePanel(), BorderLayout.CENTER);

        // ── Bottom bar ─────────────────────────────────────────────────────────
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(UITheme.BG_MAIN);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        // Filter controls
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setBackground(UITheme.BG_MAIN);

        // Month filter
        filterMonth = UITheme.comboBox();
        for (String m : MONTHS) filterMonth.addItem(m);
        filterMonth.setPreferredSize(new Dimension(130, UITheme.FIELD_HEIGHT));
        filterMonth.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH) + 1);

        // Year filter
        filterYear = UITheme.comboBox();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        filterYear.addItem("All Years");
        for (int y = curYear; y >= curYear - 5; y--) filterYear.addItem(String.valueOf(y));
        filterYear.setSelectedItem(String.valueOf(curYear));
        filterYear.setPreferredSize(new Dimension(100, UITheme.FIELD_HEIGHT));

        // Category filter
        filterCategory = UITheme.comboBox();
        filterCategory.addItem(new Category(0, "All Categories", "", "#607D8B"));
        filterCategory.setPreferredSize(new Dimension(160, UITheme.FIELD_HEIGHT));

        JButton applyFilterBtn = UITheme.secondaryButton("Apply Filter");
        JButton clearFilterBtn = UITheme.secondaryButton("Clear");

        filters.add(UITheme.label("Month:"));
        filters.add(filterMonth);
        filters.add(UITheme.label("Year:"));
        filters.add(filterYear);
        filters.add(UITheme.label("Category:"));
        filters.add(filterCategory);
        filters.add(applyFilterBtn);
        filters.add(clearFilterBtn);

        // Action buttons on right
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(UITheme.BG_MAIN);
        JButton addBtn    = UITheme.primaryButton("➕  Add Expense");
        JButton editBtn   = UITheme.secondaryButton("✏  Edit");
        JButton deleteBtn = UITheme.dangerButton("🗑  Delete");

        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(addBtn);

        bar.add(filters, BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);

        // Events
        applyFilterBtn.addActionListener(e -> applyFilter());
        clearFilterBtn.addActionListener(e -> { filterMonth.setSelectedIndex(0); filterYear.setSelectedIndex(0); filterCategory.setSelectedIndex(0); applyFilter(); });
        addBtn.addActionListener(e    -> showAddEditDialog(null));
        editBtn.addActionListener(e   -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());

        return bar;
    }

    // ── Table panel ───────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        String[] cols = {"#", "Date", "Description", "Category", "Shop / Store", "Amount"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 0) return Integer.class;
                return String.class;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setFont(UITheme.FONT_BODY);
        expenseTable.setRowHeight(32);
        expenseTable.setShowGrid(true);
        expenseTable.setGridColor(UITheme.BORDER);
        expenseTable.setBackground(UITheme.BG_CARD);
        expenseTable.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        expenseTable.setSelectionForeground(UITheme.TEXT_PRIMARY);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.getTableHeader().setFont(UITheme.FONT_LABEL);
        expenseTable.getTableHeader().setBackground(UITheme.TABLE_HEADER);
        expenseTable.getTableHeader().setReorderingAllowed(false);

        // Column widths
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        expenseTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Right-align amount
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        expenseTable.getColumnModel().getColumn(5).setCellRenderer(rightAlign);

        // Center-align #
        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        expenseTable.getColumnModel().getColumn(0).setCellRenderer(centerAlign);

        // Alternating row colors
        expenseTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ROW_ALT);
                }
                if (col == 5) ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                else if (col == 0) ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                else ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        });

        // Double-click to edit
        expenseTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handleEdit();
            }
        });

        JScrollPane scroll = new JScrollPane(expenseTable);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Bottom bar ────────────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_MAIN);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLabel.setForeground(UITheme.PRIMARY);
        bar.add(totalLabel, BorderLayout.EAST);

        JLabel helpLbl = new JLabel("Double-click any row to edit. Select a row and click Delete to remove.");
        helpLbl.setFont(UITheme.FONT_SMALL);
        helpLbl.setForeground(UITheme.TEXT_SECONDARY);
        bar.add(helpLbl, BorderLayout.WEST);

        return bar;
    }

    // ── Refresh (called on panel switch) ──────────────────────────────────────
    public void refresh() {
        // Reload categories into filter combo
        filterCategory.removeAllItems();
        filterCategory.addItem(new Category(0, "All Categories", "", "#607D8B"));
        for (Category c : categoryDAO.getAllCategories()) {
            filterCategory.addItem(c);
        }
        applyFilter();
    }

    // ── Filter Logic ──────────────────────────────────────────────────────────
    private void applyFilter() {
        allExpenses = expenseDAO.getExpensesByUser(currentUser.getId());

        int monthIdx = filterMonth.getSelectedIndex();   // 0 = All, 1–12 = Jan–Dec
        int yearSel  = 0;
        try { yearSel = Integer.parseInt((String) Objects.requireNonNull(filterYear.getSelectedItem())); }
        catch (NumberFormatException ignored) {}

        Category catFilter = (Category) filterCategory.getSelectedItem();
        int catId = (catFilter != null) ? catFilter.getId() : 0;

        List<Expense> filtered = new ArrayList<>();
        for (Expense e : allExpenses) {
            Calendar c = Calendar.getInstance();
            c.setTime(e.getDate());
            int expMonth = c.get(Calendar.MONTH) + 1;
            int expYear  = c.get(Calendar.YEAR);

            boolean monthOk = (monthIdx == 0) || (expMonth == monthIdx);
            boolean yearOk  = (yearSel == 0)  || (expYear  == yearSel);
            boolean catOk   = (catId   == 0)  || (e.getCategoryId() == catId);

            if (monthOk && yearOk && catOk) filtered.add(e);
        }

        loadTableData(filtered);
    }

    private void loadTableData(List<Expense> expenses) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        double total = 0;
        int idx = 1;
        for (Expense e : expenses) {
            tableModel.addRow(new Object[]{
                idx++,
                sdf.format(e.getDate()),
                e.getDescription() != null ? e.getDescription() : "—",
                e.getCategoryName(),
                e.getShop() != null ? e.getShop() : "—",
                CURRENCY.format(e.getAmount())
            });
            total += e.getAmount();
        }
        totalLabel.setText("Total: " + CURRENCY.format(total) + "  (" + expenses.size() + " records)");
    }

    // ── Add / Edit Dialog ─────────────────────────────────────────────────────
    private void showAddEditDialog(Expense existingExpense) {
        boolean isEdit = (existingExpense != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Expense" : "Add New Expense", true);
        dialog.setSize(480, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header
        JLabel hdr = new JLabel(isEdit ? "✏  Edit Expense" : "➕  Add New Expense");
        hdr.setFont(UITheme.FONT_SUBTITLE);
        hdr.setForeground(UITheme.TEXT_PRIMARY);
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        panel.add(hdr, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1;

        // Amount
        JTextField amountField = UITheme.textField("");
        // Description
        JTextField descField = UITheme.textField("");
        // Category
        JComboBox<Category> catCombo = UITheme.comboBox();
        List<Category> cats = categoryDAO.getAllCategories();
        for (Category c : cats) catCombo.addItem(c);
        // Date
        JTextField dateField = UITheme.textField("");
        dateField.setToolTipText("Format: YYYY-MM-DD");
        // Shop
        JTextField shopField = UITheme.textField("");

        // Pre-fill if editing
        if (isEdit) {
            amountField.setText(String.valueOf(existingExpense.getAmount()));
            descField.setText(existingExpense.getDescription() != null ? existingExpense.getDescription() : "");
            shopField.setText(existingExpense.getShop() != null ? existingExpense.getShop() : "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateField.setText(sdf.format(existingExpense.getDate()));
            for (int i = 0; i < catCombo.getItemCount(); i++) {
                if (catCombo.getItemAt(i).getId() == existingExpense.getCategoryId()) {
                    catCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }

        // Grid layout for form fields
        int row = 0;
        addFormRow(form, gbc, row++, "Amount (₹) *",  amountField);
        addFormRow(form, gbc, row++, "Description",    descField);
        addFormRow(form, gbc, row++, "Category *",     catCombo);
        addFormRow(form, gbc, row++, "Date (YYYY-MM-DD) *", dateField);
        addFormRow(form, gbc, row++, "Shop / Store",   shopField);

        // Status
        JLabel statusLbl = new JLabel(" ");
        statusLbl.setFont(UITheme.FONT_SMALL);
        statusLbl.setForeground(UITheme.DANGER);
        gbc.gridy = row;
        form.add(statusLbl, gbc);

        panel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(UITheme.BG_CARD);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        JButton cancelBtn = UITheme.secondaryButton("Cancel");
        JButton saveBtn   = UITheme.primaryButton(isEdit ? "Save Changes" : "Add Expense");
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);

        // Events
        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            // Validate
            if (!ValidationUtils.isValidAmount(amountField.getText())) {
                statusLbl.setText("⚠ Please enter a valid positive amount.");
                return;
            }
            if (catCombo.getSelectedItem() == null) {
                statusLbl.setText("⚠ Please select a category.");
                return;
            }
            Date parsedDate;
            try {
                parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText().trim());
            } catch (ParseException ex) {
                statusLbl.setText("⚠ Date must be in YYYY-MM-DD format.");
                return;
            }

            double amount = Double.parseDouble(amountField.getText().trim());
            Category selectedCat = (Category) catCombo.getSelectedItem();

            boolean success;
            if (isEdit) {
                existingExpense.setAmount(amount);
                existingExpense.setDescription(descField.getText().trim());
                existingExpense.setCategoryId(selectedCat.getId());
                existingExpense.setDate(parsedDate);
                existingExpense.setShop(shopField.getText().trim());
                success = expenseDAO.updateExpense(existingExpense);
            } else {
                Expense newExp = new Expense(
                    currentUser.getId(), amount,
                    descField.getText().trim(),
                    selectedCat.getId(), parsedDate,
                    shopField.getText().trim()
                );
                success = expenseDAO.addExpense(newExp);
            }

            if (success) {
                dialog.dispose();
                refresh();
                JOptionPane.showMessageDialog(this,
                    isEdit ? "Expense updated successfully!" : "Expense added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLbl.setText("✗ Operation failed. Please try again.");
            }
        });

        dialog.setVisible(true);
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy  = row * 2;
        gbc.insets = new Insets(8, 0, 2, 0);
        form.add(UITheme.label(label), gbc);
        gbc.gridy  = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(field, gbc);
    }

    // ── Handle Edit ───────────────────────────────────────────────────────────
    private void handleEdit() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Expense> filtered = getFilteredExpenses();
        if (selectedRow < filtered.size()) {
            showAddEditDialog(filtered.get(selectedRow));
        }
    }

    // ── Handle Delete ─────────────────────────────────────────────────────────
    private void handleDelete() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Expense> filtered = getFilteredExpenses();
        if (selectedRow >= filtered.size()) return;

        Expense toDelete = filtered.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this expense?\n\n" + CURRENCY.format(toDelete.getAmount()) +
            " – " + toDelete.getCategoryName(),
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = expenseDAO.deleteExpense(toDelete.getId(), currentUser.getId());
            if (success) {
                refresh();
                JOptionPane.showMessageDialog(this, "Expense deleted successfully.",
                    "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete expense.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Returns the current filtered expense list (same order as table)
    private List<Expense> getFilteredExpenses() {
        List<Expense> all = expenseDAO.getExpensesByUser(currentUser.getId());
        int monthIdx = filterMonth.getSelectedIndex();
        int yearSel = 0;
        try { yearSel = Integer.parseInt((String) Objects.requireNonNull(filterYear.getSelectedItem())); }
        catch (NumberFormatException ignored) {}
        Category catFilter = (Category) filterCategory.getSelectedItem();
        int catId = (catFilter != null) ? catFilter.getId() : 0;

        List<Expense> filtered = new ArrayList<>();
        for (Expense e : all) {
            Calendar c = Calendar.getInstance();
            c.setTime(e.getDate());
            boolean monthOk = (monthIdx == 0) || (c.get(Calendar.MONTH) + 1 == monthIdx);
            boolean yearOk  = (yearSel  == 0) || (c.get(Calendar.YEAR) == yearSel);
            boolean catOk   = (catId    == 0) || (e.getCategoryId() == catId);
            if (monthOk && yearOk && catOk) filtered.add(e);
        }
        return filtered;
    }
}
