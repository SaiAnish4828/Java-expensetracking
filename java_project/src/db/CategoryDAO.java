package db;

import model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO - Data Access Object for Category
 * Handles all database operations related to categories
 */
public class CategoryDAO {

    private Connection connection;

    public CategoryDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setDescription(rs.getString("description"));
                cat.setColorCode(rs.getString("color_code"));
                categories.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Get all error: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Get category by ID
     */
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setDescription(rs.getString("description"));
                cat.setColorCode(rs.getString("color_code"));
                return cat;
            }
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Get by ID error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Add a new category
     */
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (name, description, color_code) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setString(3, category.getColorCode());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Add error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing category
     */
    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ?, color_code = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setString(3, category.getColorCode());
            pstmt.setInt(4, category.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Update error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a category (only if no expenses linked)
     */
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Delete error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if category name already exists
     */
    public boolean categoryNameExists(String name) {
        String sql = "SELECT id FROM categories WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Name check error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check how many expenses use this category
     */
    public int getExpenseCountForCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM expenses WHERE category_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Count error: " + e.getMessage());
        }
        return 0;
    }
}
