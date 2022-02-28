-- **************************************************************
-- This script destroys the database and associated users
-- **************************************************************

-- The following line terminates any active connections to the database so that it can be destroyed
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'stock_db';

DROP DATABASE stock_db;

DROP USER stock_db_owner;
DROP USER stock_db_app_user;
