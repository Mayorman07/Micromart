-- 1. Create ROLES Table
CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    CONSTRAINT uk_roles_name UNIQUE (name)
);

-- 2. Create AUTHORITIES Table
CREATE TABLE authorities (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL, -- Increased size to 50 to handle "product:WRITE"
    CONSTRAINT uk_authorities_name UNIQUE (name)
);

-- 3. Create ROLES_AUTHORITIES (Junction Table)
-- Links Roles to Authorities (e.g., MANAGER has product:WRITE)
CREATE TABLE roles_authorities (
    role_id BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles (id),
    FOREIGN KEY (authority_id) REFERENCES authorities (id)
);

-- 4. Create USERS_ROLES (Junction Table)
-- Links your existing Users table to the new Roles table
-- Note: It uses 'user_id' referring to the BIGINT id, NOT the UUID string
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);