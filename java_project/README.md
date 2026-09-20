# 💰 Expense Manager — Java Swing Application

A fully functional **Monthly Expense Tracker** built with **Java Swing** and **MySQL (JDBC)** implementing the architecture from the project diagram.

---

## ✅ Features Implemented (Phase 1)

| # | Feature | Status |
|---|---------|--------|
| 1 | User Login | ✅ Done |
| 2 | User Registration | ✅ Done |
| 3 | Add Expense | ✅ Done |
| 4 | Edit Expense | ✅ Done |
| 5 | Delete Expense | ✅ Done |
| 6 | View Expense List | ✅ Done |
| 7 | Expense Categorization | ✅ Done |
| 8 | Category Management (CRUD) | ✅ Done |
| 9 | Dashboard with Summary | ✅ Done |
| 10 | Month/Year/Category Filter | ✅ Done |
| 11 | Password Hashing (SHA-256 + Salt) | ✅ Done |
| 12 | Input Validation | ✅ Done |

---

## 🗂️ Project Structure

```
java_project/
├── src/
│   ├── Main.java                    ← Entry point
│   ├── model/
│   │   ├── User.java                ← User model (OOP: Encapsulation)
│   │   ├── Category.java            ← Category model
│   │   └── Expense.java             ← Expense model
│   ├── db/
│   │   ├── DatabaseConnection.java  ← Singleton JDBC connection + table init
│   │   ├── UserDAO.java             ← User CRUD operations
│   │   ├── CategoryDAO.java         ← Category CRUD operations
│   │   └── ExpenseDAO.java          ← Expense CRUD operations
│   ├── ui/
│   │   ├── UITheme.java             ← Centralized styling (colors, fonts, components)
│   │   ├── LoginFrame.java          ← Login screen
│   │   ├── RegisterFrame.java       ← Registration screen
│   │   ├── DashboardFrame.java      ← Main window with sidebar navigation
│   │   ├── DashboardHomePanel.java  ← Home dashboard (stats, charts, recent)
│   │   ├── ExpensePanel.java        ← Expense management (table + CRUD)
│   │   └── CategoryPanel.java       ← Category management (table + CRUD)
│   └── utils/
│       ├── PasswordUtils.java       ← SHA-256 + Salt hashing
│       └── ValidationUtils.java     ← Input validation
├── lib/
│   └── mysql-connector-java.jar     ← (you place this here)
├── compile_and_run.bat              ← Windows build script
├── compile_and_run.sh               ← Linux/macOS build script
└── README.md
```

---

## 🛠️ Setup Instructions

### Step 1 — Prerequisites
- **JDK 17** or higher → https://adoptium.net
- **MySQL Server 8.0+** → https://dev.mysql.com/downloads/mysql/
- **MySQL Connector/J** (JDBC Driver) → https://dev.mysql.com/downloads/connector/j/

### Step 2 — Create the Database
Open MySQL command line or MySQL Workbench and run:
```sql
CREATE DATABASE expense_manager;
```
> The application auto-creates all tables on first run.

### Step 3 — Add MySQL JDBC Driver
1. Download `mysql-connector-java-8.x.x.jar`
2. Create a `lib/` folder inside `java_project/`
3. Place the `.jar` file inside `lib/`

### Step 4 — Configure Database Password
Open `src/db/DatabaseConnection.java` and change:
```java
private static final String PASSWORD = "your_password"; // ← Change this
```
Also update `USERNAME` if it's not `root`.

### Step 5 — Compile & Run

**Windows:**
```bat
compile_and_run.bat
```

**Linux / macOS:**
```bash
chmod +x compile_and_run.sh
./compile_and_run.sh
```

**Manual compile (any OS):**
```bash
# Windows
javac -cp ".;lib/mysql-connector-java.jar" -d out src/model/*.java src/utils/*.java src/db/*.java src/ui/*.java src/Main.java
java  -cp "out;lib/mysql-connector-java.jar" Main

# Linux/macOS (use : instead of ;)
javac -cp ".:lib/mysql-connector-java.jar" -d out src/model/*.java src/utils/*.java src/db/*.java src/ui/*.java src/Main.java
java  -cp "out:lib/mysql-connector-java.jar" Main
```

---

## 🗄️ Database Tables (auto-created)

```sql
CREATE TABLE users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,   -- SHA-256 hashed with salt
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    color_code  VARCHAR(10) DEFAULT '#607D8B'
);

CREATE TABLE expenses (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    amount      DOUBLE NOT NULL,
    description VARCHAR(255),
    category_id INT NOT NULL,
    date        DATE NOT NULL,
    shop        VARCHAR(150),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)     REFERENCES users(id)     ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

---

## 🎨 Default Categories (auto-seeded)
| Category | Color |
|----------|-------|
| Food & Dining | 🔴 Red |
| Transportation | 🔵 Blue |
| Shopping | 🟣 Purple |
| Entertainment | 🟠 Orange |
| Health & Medical | 🟢 Green |
| Utilities | 🩵 Teal |
| Education | 🔶 Deep Orange |
| Travel | 🔷 Indigo |
| Others | ⚫ Slate |

---

## 🏗️ OOP Concepts Used

| Concept | Where Applied |
|---------|---------------|
| **Encapsulation** | All model classes (User, Expense, Category) with private fields + getters/setters |
| **Inheritance** | All `JFrame` and `JPanel` subclasses (LoginFrame, RegisterFrame, DashboardFrame, etc.) |
| **Abstraction** | DAO classes abstract all DB operations; UITheme abstracts styling |
| **Polymorphism** | TableCellRenderer implementations, custom painted components |
| **Singleton** | DatabaseConnection uses Singleton pattern |
| **Composition** | DashboardFrame composes ExpensePanel, CategoryPanel, DashboardHomePanel |

---

## 🔜 Coming Next (Phase 2)
- Monthly Tracker with budgets
- Report & Analytics (PDF/CSV export)
- Price Comparison feature
- Settings & Profile management
