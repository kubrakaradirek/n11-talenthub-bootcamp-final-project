SELECT 'CREATE DATABASE user_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_db')\gexec

SELECT 'CREATE DATABASE product_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'product_db')\gexec

SELECT 'CREATE DATABASE stock_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'stock_db')\gexec

SELECT 'CREATE DATABASE order_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'order_db')\gexec

SELECT 'CREATE DATABASE "n11-postgres"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'n11-postgres')\gexec
