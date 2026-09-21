@echo off
REM ═══════════════════════════════════════════════════════════════════
REM  Expense Manager – Start local MySQL (port 3307)
REM  Run this FIRST and leave the window open, then run
REM  compile_and_run.bat in a second window.
REM ═══════════════════════════════════════════════════════════════════
cd /d "%~dp0"

REM ── Clear a stale lock from an unclean shutdown ─────────────────────
if exist "mysql-data-3307\mysqld-3307.pid" del "mysql-data-3307\mysqld-3307.pid"

echo Starting MySQL on port 3307 (data: mysql-data-3307)...
echo Leave this window open while using the app.
echo.
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --datadir="%~dp0mysql-data-3307" --port=3307 --mysqlx-port=33070 --console
