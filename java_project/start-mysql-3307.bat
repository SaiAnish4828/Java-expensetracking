@echo off
REM Expense Manager - Start local MySQL (port 3307).
REM Run this FIRST and leave the window open, then run
REM compile_and_run.bat in a second window.
cd /d "%~dp0"

if not exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" (
    echo [ERROR] mysqld.exe not found at "C:\Program Files\MySQL\MySQL Server 8.0\bin\".
    echo Install MySQL Server 8.0 first, then try again.
    pause
    exit /b 1
)

REM Clear a stale lock left by an unclean shutdown.
if exist "mysql-data-3307\mysqld-3307.pid" del "mysql-data-3307\mysqld-3307.pid"

echo Starting MySQL on port 3307 ...
echo Leave this window open while using the app.
echo.
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --datadir="%~dp0mysql-data-3307" --port=3307 --mysqlx-port=33070 --console

echo.
echo [INFO] MySQL stopped with code %ERRORLEVEL%.
echo If it closed immediately, read the error lines above.
pause
