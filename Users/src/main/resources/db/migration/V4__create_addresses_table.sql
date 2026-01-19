CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address_id VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20),
    type VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,

    -- Constraint to ensure data integrity
    CONSTRAINT fk_user_address FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);