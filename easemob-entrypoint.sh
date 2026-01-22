#!/bin/bash
set -e

# Configuration
DB_NAME="app_server"
DB_USER="root"
# Default password matching application.properties
DB_PASS="${DB_PASSWORD:-cy990810}" 

echo "[Easemob] Starting All-in-One Service..."

# 1. Start Redis
echo "[Easemob] Starting Redis Server..."
service redis-server start

# 2. Start MySQL
echo "[Easemob] Starting MySQL Server..."
# Ensure /var/lib/mysql ownership
chown -R mysql:mysql /var/lib/mysql

# Check if MySQL is initialized
if [ ! -d "/var/lib/mysql/mysql" ]; then
    echo "[Easemob] Initializing MySQL data directory..."
    mysqld --initialize-insecure --user=mysql
fi

# Start MySQL service
service mysql start

# Wait for MySQL to be ready
echo "[Easemob] Waiting for MySQL to be ready..."
until mysqladmin ping -h localhost --silent; do
    echo "Waiting for mysqld..."
    sleep 2
done

# 3. Configure Database
echo "[Easemob] Configuring Database..."

# Check if database exists
if ! mysql -u root -e "use $DB_NAME" 2>/dev/null; then
    echo "[Easemob] Database '$DB_NAME' not found. Creating..."
    
    # Reset root password and create database
    # Note: MySQL 8.0 syntax
    mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '$DB_PASS'; FLUSH PRIVILEGES;"
    
    # Now use the password
    mysql -u root -p"$DB_PASS" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    
    # Import Schema
    if [ -f "/docker-entrypoint-initdb.d/create_tables.sql" ]; then
        echo "[Easemob] Importing schema from create_tables.sql..."
        mysql -u root -p"$DB_PASS" $DB_NAME < /docker-entrypoint-initdb.d/create_tables.sql
    else
        echo "[Easemob] WARNING: create_tables.sql not found!"
    fi
else
    echo "[Easemob] Database '$DB_NAME' already exists. Skipping initialization."
fi

# 4. Start Java Application
echo "[Easemob] Starting App Server..."
exec java -jar /app/app-server.jar
