package model;

import java.util.Date;

/**
 * Expense Model Class
 * Represents an expense entry in the system
 * Demonstrates OOP: Encapsulation
 */
public class Expense {
    private int id;
    private int userId;
    private double amount;
    private String description;
    private int categoryId;
    private String categoryName; // for display purposes
    private Date date;
    private String shop;

    // Default Constructor
    public Expense() {}

    // Parameterized Constructor (full)
    public Expense(int id, int userId, double amount, String description,
                   int categoryId, String categoryName, Date date, String shop) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.date = date;
        this.shop = shop;
    }

    // Constructor without id (for new expense)
    public Expense(int userId, double amount, String description,
                   int categoryId, Date date, String shop) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.categoryId = categoryId;
        this.date = date;
        this.shop = shop;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getShop() { return shop; }
    public void setShop(String shop) { this.shop = shop; }

    @Override
    public String toString() {
        return "Expense{id=" + id + ", amount=" + amount + ", category='" + categoryName
                + "', date=" + date + ", shop='" + shop + "'}";
    }
}
