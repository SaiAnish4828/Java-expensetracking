@echo off
REM ═══════════════════════════════════════════════════════════════════
REM  Expense Manager – Windows Compile & Run Script
REM  Requirements: JDK 17+, MySQL Server, mysql-connector-java.jar
REM ═══════════════════════════════════════════════════════════════════

echo.
echo  ╔══════════════════════════════════════════════╗
echo  ║       Expense Manager – Java Swing App       ║
echo  ╚══════════════════════════════════════════════╝
echo.

REM ── Prefer JDK 17+ (Microsoft Build) if present ─────────────────────────
if exist "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\javac.exe" (
    set "PATH=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin;%PATH%"
)
javac -version 2>&1 | findstr /R "17\. 1[89]\. 2[0-9]\." >nul
if %ERRORLEVEL% NEQ 0 (
    echo [WARN] JDK 17+ not found on PATH. Install Microsoft.OpenJDK.17 via: winget install Microsoft.OpenJDK.17
)

REM ── Check if output directory exists ──────────────────────────────
if not exist out mkdir out

REM ── Compile all Java files ─────────────────────────────────────────
echo [1/2] Compiling Java source files...
javac -encoding UTF-8 -cp ".;lib\mysql-connector-java.jar" ^
      -d out ^
      src\model\User.java ^
      src\model\Category.java ^
      src\model\Expense.java ^
      src\utils\PasswordUtils.java ^
      src\utils\ValidationUtils.java ^
      src\db\DatabaseConnection.java ^
      src\db\UserDAO.java ^
      src\db\CategoryDAO.java ^
      src\db\ExpenseDAO.java ^
      src\ui\UITheme.java ^
      src\ui\LoginFrame.java ^
      src\ui\RegisterFrame.java ^
      src\ui\DashboardHomePanel.java ^
      src\ui\ExpensePanel.java ^
      src\ui\CategoryPanel.java ^
      src\ui\DashboardFrame.java ^
      src\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Check the errors above.
    pause
    exit /b 1
)

echo [1/2] Compilation successful!
echo.

REM ── Run the application ────────────────────────────────────────────
echo [2/2] Launching Expense Manager...
java -cp "out;lib\mysql-connector-java.jar" Main

pause
