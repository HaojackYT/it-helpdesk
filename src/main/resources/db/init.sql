-- Create database if not exists
CREATE DATABASE IF NOT EXISTS it_helpdesk;
USE it_helpdesk;

-- Drop tables if exist (in reverse order of dependencies)
DROP TABLE IF EXISTS ticket_comments;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS permissions;

-- =====================================================
-- CREATE TABLES
-- =====================================================

-- Permissions table
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- Roles table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Role_Permissions junction table
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Users table
CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(100),
    status ENUM('ACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User_Roles junction table
CREATE TABLE user_roles (
    user_id BINARY(16) NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Tickets table
CREATE TABLE tickets (
    id BINARY(16) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category ENUM('NETWORK', 'SOFTWARE', 'HARDWARE', 'ACCOUNT') NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    created_by BINARY(16) NOT NULL,
    assigned_to BINARY(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (assigned_to) REFERENCES users(id)
);

-- Ticket_Comments table
CREATE TABLE ticket_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =====================================================
-- VERIFICATION QUERIES (Optional - run to verify data)
-- =====================================================

-- SELECT 'Permissions' as 'Table', COUNT(*) as 'Count' FROM permissions
-- UNION ALL
-- SELECT 'Roles', COUNT(*) FROM roles
-- UNION ALL
-- SELECT 'Users', COUNT(*) FROM users
-- UNION ALL
-- SELECT 'User_Roles', COUNT(*) FROM user_roles
-- UNION ALL
-- SELECT 'Role_Permissions', COUNT(*) FROM role_permissions
-- UNION ALL
-- SELECT 'Tickets', COUNT(*) FROM tickets
-- UNION ALL
-- SELECT 'Comments', COUNT(*) FROM ticket_comments;

-- =====================================================
-- TEST CREDENTIALS
-- =====================================================
-- All users have the same password: password123
--
-- | Username    | Role          | Password    |
-- |-------------|---------------|-------------|
-- | admin       | ROLE_ADMIN    | password123 |
-- | itsupport1  | ROLE_IT_SUPPORT | password123 |
-- | itsupport2  | ROLE_IT_SUPPORT | password123 |
-- | employee1   | ROLE_EMPLOYEE | password123 |
-- | employee2   | ROLE_EMPLOYEE | password123 |
-- | employee3   | ROLE_EMPLOYEE | password123 |
-- =====================================================
