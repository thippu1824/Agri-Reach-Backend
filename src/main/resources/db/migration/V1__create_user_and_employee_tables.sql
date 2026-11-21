-- V1: Create user, role and employee tables
-- 1. Roles table
CREATE TABLE roles (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   name VARCHAR(50) NOT NULL UNIQUE,
   description VARCHAR(255),
   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- 2. Users table
CREATE TABLE users (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   username VARCHAR(50) NOT NULL UNIQUE,
   password_hash VARCHAR(255) NOT NULL,
   email VARCHAR(255) UNIQUE,
   is_active BOOLEAN NOT NULL DEFAULT TRUE,
   must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- 3. User and role mapping table
CREATE TABLE user_roles (
   user_id UUID NOT NULL,
   role_id UUID NOT NULL,
   assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (user_id, role_id),
   CONSTRAINT fk_user_roles_user
       FOREIGN KEY (user_id)
       REFERENCES users(id),
   CONSTRAINT fk_user_roles_role
       FOREIGN KEY (role_id)
       REFERENCES roles(id)
);
-- 4. Employees table
CREATE TABLE employees (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   user_id UUID NOT NULL UNIQUE,
   employee_code VARCHAR(50) NOT NULL UNIQUE,
   full_name VARCHAR(150) NOT NULL,
   phone_number VARCHAR(20),
   is_active BOOLEAN NOT NULL DEFAULT TRUE,
   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT fk_employees_user
       FOREIGN KEY (user_id)
       REFERENCES users(id)
);
-- 5. Insert initial application roles
INSERT INTO roles (name, description)
VALUES
   ('ADMIN', 'Administrator with application management access'),
   ('EMPLOYEE', 'Field employee who registers farmer visits');