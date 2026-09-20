package db;

import model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ExpenseDAO - Data Access Object for Expense
 * Handles all database operations related to expenses
 */
public class ExpenseDAO {

    private Connection connection;

    public ExpenseDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Add a new expense
     */
    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (user_id, amount, description, category_id, date, shop) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, expense.getUserId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getDescription());
            pstmt.setInt(4, expense.getCategoryId());
            pstmt.setDate(5, new java.sql.Date(expense.getDate().getTime()));
            pstmt.setString(6, expense.getShop());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Add error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all expenses for a user (with category name via JOIN)
     */
    public List<Expense> getExpensesByUser(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT e.*, c.name AS category_name " +
                     "FROM expenses e " +
                     "JOIN categories c ON e.category_id = c.id " +
                     "WHERE e.user_id = ? " +
                     "ORDER BY e.date DESC, e.created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Expense exp = mapResultSetToExpense(rs);
                expenses.add(exp);
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Get by user error: " + e.getMessage());
        }
        return expenses;
    }

    /**
     * Get expenses filtered by category
     */
    public List<Expense> getExpensesByCategory(int userId, int categoryId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT e.*, c.name AS category_name " +
                     "FROM expenses e " +
                     "JOIN categories c ON e.category_id = c.id " +
                     "WHERE e.user_id = ? AND e.category_id = ? " +
                     "ORDER BY e.date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, categoryId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Get by category error: " + e.getMessage());
        }
        return expenses;
    }

    /**
     * Get expenses filtered by month and year
     */
    public List<Expense> getExpensesByMonth(int userId, int month, int year) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT e.*, c.name AS category_name " +
                     "FROM expenses e " +
                     "JOIN categories c ON e.category_id = c.id " +
                     "WHERE e.user_id = ? AND MONTH(e.date) = ? AND YEAR(e.date) = ? " +
                     "ORDER BY e.date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Get by month error: " + e.getMessage());
        }
        return expenses;
    }

    /**
     * Get total spending for a user in a specific month/year
     */
    public double getTotalByMonth(int userId, int month, int year) {
        String sql = "SELECT SUM(amount) FROM expenses " +
                     "WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Total by month error: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Get total spending by category for a user
     */
    public List<Object[]> getTotalByCategory(int userId) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT c.name, SUM(e.amount) AS total " +
                     "FROM expenses e " +
                     "JOIN categories c ON e.category_id = c.id " +
                     "WHERE e.user_id = ? " +
                     "GROUP BY c.name ORDER BY total DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{rs.getString("name"), rs.getDouble("total")});
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Total by category error: " + e.getMessage());
        }
        return results;
    }

    /**
     * Update an existing expense
     */
    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET amount = ?, description = ?, category_id = ?, date = ?, shop = ? " +
                     "WHERE id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, expense.getAmount());
            pstmt.setString(2, expense.getDescription());
            pstmt.setInt(3, expense.getCategoryId());
            pstmt.setDate(4, new java.sql.Date(expense.getDate().getTime()));
            pstmt.setString(5, expense.getShop());
            pstmt.setInt(6, expense.getId());
            pstmt.setInt(7, expense.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Update error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete an expense
     */
    public boolean deleteExpense(int expenseId, int userId) {
        String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, expenseId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Delete error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get overall total spending for a user
     */
    public double getTotalByUser(int userId) {
        String sql = "SELECT SUM(amount) FROM expenses WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Get total error: " + e.getMessage());
        }
        return 0.0;
    }

    // ── Private helper ──────────────────────────────────────────────────────────
    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        Expense exp = new Expense();
        exp.setId(rs.getInt("id"));
        exp.setUserId(rs.getInt("user_id"));
        exp.setAmount(rs.getDouble("amount"));
        exp.setDescription(rs.getString("description"));
        exp.setCategoryId(rs.getInt("category_id"));
        exp.setCategoryName(rs.getString("category_name"));
        exp.setDate(rs.getDate("date"));
        exp.setShop(rs.getString("shop"));
        return exp;
    }
}
