-- V1__Create_initial_schema.sql

-- Create the main employees table based on the User entity

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    encrypted_password VARCHAR(255) NOT NULL,
    gender VARCHAR(255),
    last_logged_in TIMESTAMP,
    last_password_reset_date TIMESTAMP,
    status VARCHAR(255) NOT NULL,
    verification_token VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    password_reset_token VARCHAR(255),
    password_reset_token_expiry_date TIMESTAMP
);