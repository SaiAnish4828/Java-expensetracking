import ui.LoginFrame;
import ui.UITheme;

import javax.swing.*;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         EXPENSE MANAGER – Java Swing Application                ║
 * ║                                                                  ║
 * ║  Features Implemented:                                           ║
 * ║  1. User Login & Registration (with SHA-256 password hashing)   ║
 * ║  2. Add, Edit, Delete & View Expenses                           ║
 * ║  3. Expense Categorization (9 defaults + custom categories)     ║
 * ║  4. Dashboard with spending summary & category breakdown        ║
 * ║  5. Month/Year/Category filtering                               ║
 * ║                                                                  ║
 * ║  Tech Stack:  Java 17+, Java Swing, MySQL, JDBC                 ║
 * ║  Author:      Expense Manager Project                           ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * HOW TO RUN:
 *  1. Install JDK 17+ and MySQL Server
 *  2. Create a MySQL database:  CREATE DATABASE expense_manager;
 *  3. Edit DatabaseConnection.java → set your MySQL PASSWORD
 *  4. Add mysql-connector-java-8.x.x.jar to your classpath
 *  5. Compile:  javac -cp ".;lib/mysql-connector-java.jar" -d out src/**\/*.java src/Main.java
 *  6. Run:      java  -cp "out;lib/mysql-connector-java.jar" Main
 *
 * (On Linux/macOS replace  ;  with  :  in the classpath)
 */
public class Main {

    public static void main(String[] args) {
        // Apply global UI styling
        UITheme.applyGlobalLAF();

        // Launch on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Failed to connect to the database.\n\n" +
                    "Please check:\n" +
                    "  • MySQL is running\n" +
                    "  • Database 'expense_manager' exists\n" +
                    "  • Password is correct in DatabaseConnection.java\n" +
                    "  • mysql-connector-java.jar is in the classpath\n\n" +
                    "Error: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
