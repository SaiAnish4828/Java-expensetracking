package model;

/**
 * Category Model Class
 * Represents an expense category
 * Demonstrates OOP: Encapsulation
 */
public class Category {
    private int id;
    private String name;
    private String description;
    private String colorCode; // for UI color coding

    // Default Constructor
    public Category() {}

    // Parameterized Constructor
    public Category(int id, String name, String description, String colorCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
    }

    // Constructor without id (for new category)
    public Category(String name, String description, String colorCode) {
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    @Override
    public String toString() {
        // Used in JComboBox dropdowns
        return name;
    }
}
