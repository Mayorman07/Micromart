CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME(6) NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);