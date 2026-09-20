package ui;

import db.CategoryDAO;
import model.Category;
import model.User;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * CategoryPanel - Manage expense categories (Add / Edit / Delete)
 * Demonstrates OOP: CRUD operations, Event-driven programming
 */
public class CategoryPanel extends JPanel {

    private User        currentUser;
    private CategoryDAO categoryDAO;

    private JTable            categoryTable;
    private DefaultTableModel tableModel;
    private JLabel            countLabel;

    // Color options for categories
    private static final String[][] COLOR_OPTIONS = {
        {"Red",     "#E53935"},
        {"Blue",    "#1E88E5"},
        {"Purple",  "#8E24AA"},
        {"Orange",  "#FB8C00"},
        {"Green",   "#43A047"},
        {"Teal",    "#00ACC1"},
        {"Deep Orange","#F4511E"},
        {"Indigo",  "#3949AB"},
        {"Slate",   "#607D8B"},
        {"Pink",    "#D81B60"},
        {"Lime",    "#C0CA33"},
        {"Cyan",    "#00BCD4"}
    };

    public CategoryPanel(User user) {
        this.currentUser = user;
        this.categoryDAO = new CategoryDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(buildToolbar(),   BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_MAIN);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel infoLbl = new JLabel("Manage your expense categories – add, edit, or delete categories.");
        infoLbl.setFont(UITheme.FONT_BODY);
        infoLbl.setForeground(UITheme.TEXT_SECONDARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(UITheme.BG_MAIN);
        JButton addBtn    = UITheme.primaryButton("➕  Add Category");
        JButton editBtn   = UITheme.secondaryButton("✏  Edit");
        JButton deleteBtn = UITheme.dangerButton("🗑  Delete");

        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(addBtn);

        bar.add(infoLbl,  BorderLayout.WEST);
        bar.add(actions,  BorderLayout.EAST);

        addBtn.addActionListener(e    -> showCategoryDialog(null));
        editBtn.addActionListener(e   -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());

        return bar;
    }

    // ── Main content: table + color legend side-by-side ───────────────────────
    private JPanel buildMainContent() {
        JPanel split = new JPanel(new BorderLayout(16, 0));
        split.setBackground(UITheme.BG_MAIN);

        // LEFT: category table
        split.add(buildTableCard(), BorderLayout.CENTER);

        // RIGHT: default categories info card
        split.add(buildInfoCard(), BorderLayout.EAST);

        return split;
    }

    // ── Category Table ────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new LineBorder(UITheme.BORDER, 1, true));

        // Table header inside card
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.TABLE_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel hdr = new JLabel("All Categories");
        hdr.setFont(UITheme.FONT_HEADING);
        hdr.setForeground(UITheme.TEXT_PRIMARY);
        header.add(hdr, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"#", "Color", "Category Name", "Description", "Expenses Linked"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setFont(UITheme.FONT_BODY);
        categoryTable.setRowHeight(36);
        categoryTable.setShowGrid(true);
        categoryTable.setGridColor(UITheme.BORDER);
        categoryTable.setBackground(UITheme.BG_CARD);
        categoryTable.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        categoryTable.setSelectionForeground(UITheme.TEXT_PRIMARY);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.getTableHeader().setFont(UITheme.FONT_LABEL);
        categoryTable.getTableHeader().setBackground(UITheme.TABLE_HEADER);
        categoryTable.getTableHeader().setReorderingAllowed(false);

        // Column widths
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        categoryTable.getColumnModel().getColumn(3).setPreferredWidth(280);
        categoryTable.getColumnModel().getColumn(4).setPreferredWidth(110);

        // Color swatch renderer for column 1
        categoryTable.getColumnModel().getColumn(1).setCellRenderer(new ColorSwatchRenderer());

        // Center renderers
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        categoryTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        categoryTable.getColumnModel().getColumn(4).setCellRenderer(centerRender);

        // Alternating rows
        categoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ROW_ALT);
                }
                if (col == 0 || col == 4) ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                else ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        });

        // Re-attach color renderer after default renderer override
        categoryTable.getColumnModel().getColumn(1).setCellRenderer(new ColorSwatchRenderer());

        // Double-click to edit
        categoryTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handleEdit();
            }
        });

        JScrollPane scroll = new JScrollPane(categoryTable);
        scroll.setBorder(null);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Info / Tips Card ──────────────────────────────────────────────────────
    private JPanel buildInfoCard() {
        JPanel card = UITheme.cardPanel();
        card.setPreferredSize(new Dimension(220, 0));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel hdr = new JLabel("💡  Tips");
        hdr.setFont(UITheme.FONT_HEADING);
        hdr.setForeground(UITheme.TEXT_PRIMARY);
        hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(hdr);
        card.add(Box.createVerticalStrut(14));

        String[] tips = {
            "• Default categories are pre-loaded for you.",
            "• Assign a color to each category to identify them visually.",
            "• You cannot delete a category that has expenses linked to it.",
            "• Edit a category name/color anytime.",
            "• Add custom categories that fit your spending habits."
        };
        for (String tip : tips) {
            JLabel lbl = new JLabel("<html><body style='width:170px'>" + tip + "</body></html>");
            lbl.setFont(UITheme.FONT_SMALL);
            lbl.setForeground(UITheme.TEXT_SECONDARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            card.add(lbl);
        }

        card.add(Box.createVerticalGlue());

        // Color palette reference
        JLabel paletteLbl = new JLabel("Available Colors");
        paletteLbl.setFont(UITheme.FONT_LABEL);
        paletteLbl.setForeground(UITheme.TEXT_PRIMARY);
        paletteLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        paletteLbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        card.add(paletteLbl);

        JPanel colorGrid = new JPanel(new GridLayout(0, 4, 4, 4));
        colorGrid.setBackground(UITheme.BG_CARD);
        colorGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String[] co : COLOR_OPTIONS) {
            JPanel swatch = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.decode(co[1]));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                }
            };
            swatch.setToolTipText(co[0] + " " + co[1]);
            swatch.setPreferredSize(new Dimension(28, 18));
            colorGrid.add(swatch);
        }
        card.add(colorGrid);

        return card;
    }

    // ── Bottom bar ────────────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_MAIN);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        countLabel = new JLabel("0 categories");
        countLabel.setFont(UITheme.FONT_LABEL);
        countLabel.setForeground(UITheme.PRIMARY);
        bar.add(countLabel, BorderLayout.EAST);
        JLabel helpLbl = new JLabel("Double-click to edit. You cannot delete categories that have linked expenses.");
        helpLbl.setFont(UITheme.FONT_SMALL);
        helpLbl.setForeground(UITheme.TEXT_SECONDARY);
        bar.add(helpLbl, BorderLayout.WEST);
        return bar;
    }

    // ── Refresh ───────────────────────────────────────────────────────────────
    public void refresh() {
        tableModel.setRowCount(0);
        List<Category> categories = categoryDAO.getAllCategories();
        int idx = 1;
        for (Category cat : categories) {
            int expCount = categoryDAO.getExpenseCountForCategory(cat.getId());
            tableModel.addRow(new Object[]{
                idx++,
                cat.getColorCode(),
                cat.getName(),
                cat.getDescription() != null ? cat.getDescription() : "—",
                expCount
            });
        }
        countLabel.setText(categories.size() + " categories");
    }

    // ── Add / Edit Dialog ─────────────────────────────────────────────────────
    private void showCategoryDialog(Category existing) {
        boolean isEdit = (existing != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Category" : "Add New Category", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel hdr = new JLabel(isEdit ? "✏  Edit Category" : "➕  Add New Category");
        hdr.setFont(UITheme.FONT_SUBTITLE);
        hdr.setForeground(UITheme.TEXT_PRIMARY);
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        panel.add(hdr, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Name field
        JTextField nameField = UITheme.textField("");
        // Description field
        JTextField descField = UITheme.textField("");
        // Color picker combo
        JComboBox<String[]> colorCombo = new JComboBox<>();
        for (String[] co : COLOR_OPTIONS) colorCombo.addItem(co);
        colorCombo.setFont(UITheme.FONT_BODY);
        colorCombo.setPreferredSize(new Dimension(200, UITheme.FIELD_HEIGHT));
        colorCombo.setRenderer(new ColorComboRenderer());

        // Pre-fill if editing
        if (isEdit) {
            nameField.setText(existing.getName());
            descField.setText(existing.getDescription() != null ? existing.getDescription() : "");
            // Select matching color
            for (int i = 0; i < COLOR_OPTIONS.length; i++) {
                if (COLOR_OPTIONS[i][1].equalsIgnoreCase(existing.getColorCode())) {
                    colorCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        int row = 0;
        addRow(form, gbc, row++, "Category Name *", nameField);
        addRow(form, gbc, row++, "Description",      descField);
        addRow(form, gbc, row++, "Color",            colorCombo);

        JLabel statusLbl = new JLabel(" ");
        statusLbl.setFont(UITheme.FONT_SMALL);
        statusLbl.setForeground(UITheme.DANGER);
        gbc.gridy = row * 2;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(statusLbl, gbc);

        panel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(UITheme.BG_CARD);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        JButton cancelBtn = UITheme.secondaryButton("Cancel");
        JButton saveBtn   = UITheme.primaryButton(isEdit ? "Save Changes" : "Add Category");
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            String[] selectedColor = (String[]) colorCombo.getSelectedItem();
            String colorCode = (selectedColor != null) ? selectedColor[1] : "#607D8B";

            if (!ValidationUtils.isValidName(name)) {
                statusLbl.setText("⚠ Category name must be at least 2 characters.");
                return;
            }

            // Check duplicate (skip self if editing)
            boolean exists = categoryDAO.categoryNameExists(name);
            if (exists && (!isEdit || !existing.getName().equalsIgnoreCase(name))) {
                statusLbl.setText("⚠ A category with this name already exists.");
                return;
            }

            boolean success;
            if (isEdit) {
                existing.setName(name);
                existing.setDescription(desc);
                existing.setColorCode(colorCode);
                success = categoryDAO.updateCategory(existing);
            } else {
                success = categoryDAO.addCategory(new Category(name, desc, colorCode));
            }

            if (success) {
                dialog.dispose();
                refresh();
                JOptionPane.showMessageDialog(this,
                    isEdit ? "Category updated successfully!" : "Category added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLbl.setText("✗ Operation failed. Please try again.");
            }
        });

        dialog.setVisible(true);
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row * 2;
        gbc.insets = new Insets(8, 0, 2, 0);
        form.add(UITheme.label(label), gbc);
        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(field, gbc);
    }

    // ── Handle Edit ───────────────────────────────────────────────────────────
    private void handleEdit() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Category> cats = categoryDAO.getAllCategories();
        if (selectedRow < cats.size()) {
            showCategoryDialog(cats.get(selectedRow));
        }
    }

    // ── Handle Delete ─────────────────────────────────────────────────────────
    private void handleDelete() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Category> cats = categoryDAO.getAllCategories();
        if (selectedRow >= cats.size()) return;

        Category toDelete = cats.get(selectedRow);
        int expCount = categoryDAO.getExpenseCountForCategory(toDelete.getId());

        if (expCount > 0) {
            JOptionPane.showMessageDialog(this,
                "Cannot delete \"" + toDelete.getName() + "\".\n" +
                "It has " + expCount + " expense(s) linked to it.\n" +
                "Please reassign or delete those expenses first.",
                "Cannot Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete category \"" + toDelete.getName() + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = categoryDAO.deleteCategory(toDelete.getId());
            if (success) {
                refresh();
                JOptionPane.showMessageDialog(this, "Category deleted successfully.",
                    "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Inner class: Color Swatch Renderer for table ──────────────────────────
    static class ColorSwatchRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JPanel panel = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    try {
                        g2.setColor(Color.decode(value.toString()));
                    } catch (Exception e) {
                        g2.setColor(Color.GRAY);
                    }
                    int w = getWidth(), h = getHeight();
                    g2.fillRoundRect(w/2 - 16, h/2 - 8, 32, 16, 8, 8);
                }
            };
            panel.setBackground(isSelected ? UITheme.PRIMARY_LIGHT :
                (row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ROW_ALT));
            return panel;
        }
    }

    // ── Inner class: Color combo renderer ────────────────────────────────────
    static class ColorComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
            row.setBackground(isSelected ? UITheme.PRIMARY_LIGHT : UITheme.BG_CARD);

            if (value instanceof String[] co) {
                JPanel swatch = new JPanel() {
                    @Override protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        try { g2.setColor(Color.decode(co[1])); } catch (Exception e) { g2.setColor(Color.GRAY); }
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    }
                };
                swatch.setPreferredSize(new Dimension(24, 14));
                swatch.setOpaque(false);
                JLabel nameLbl = new JLabel(co[0]);
                nameLbl.setFont(UITheme.FONT_BODY);
                row.add(swatch);
                row.add(nameLbl);
            }
            return row;
        }
    }
}
