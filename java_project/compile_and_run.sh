#!/bin/bash
# ═══════════════════════════════════════════════════════════════════
#  Expense Manager – Linux / macOS Compile & Run Script
#  Requirements: JDK 17+, MySQL Server, mysql-connector-java.jar
# ═══════════════════════════════════════════════════════════════════

echo ""
echo "  ╔══════════════════════════════════════════════╗"
echo "  ║       Expense Manager – Java Swing App       ║"
echo "  ╚══════════════════════════════════════════════╝"
echo ""

# ── Create output directory ────────────────────────────────────────
mkdir -p out

# ── Compile all Java files ─────────────────────────────────────────
echo "[1/2] Compiling Java source files..."
javac -encoding UTF-8 -cp ".:lib/mysql-connector-java.jar" \
      -d out \
      src/model/User.java \
      src/model/Category.java \
      src/model/Expense.java \
      src/utils/PasswordUtils.java \
      src/utils/ValidationUtils.java \
      src/db/DatabaseConnection.java \
      src/db/UserDAO.java \
      src/db/CategoryDAO.java \
      src/db/ExpenseDAO.java \
      src/ui/UITheme.java \
      src/ui/LoginFrame.java \
      src/ui/RegisterFrame.java \
      src/ui/DashboardHomePanel.java \
      src/ui/ExpensePanel.java \
      src/ui/CategoryPanel.java \
      src/ui/DashboardFrame.java \
      src/Main.java

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Compilation failed. Check errors above."
    exit 1
fi

echo "[1/2] Compilation successful!"
echo ""

# ── Run the application ────────────────────────────────────────────
echo "[2/2] Launching Expense Manager..."
java -cp "out:lib/mysql-connector-java.jar" Main
