CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY, -- UUID
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    stock_quantity INT,
    image_url VARCHAR(255),
    sku_code VARCHAR(255) NOT NULL,
    category_id BIGINT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- SEED DATA (This solves your Navbar problem!)
INSERT INTO categories (name, description) VALUES ('Electronics', 'Gadgets and devices');
INSERT INTO categories (name, description) VALUES ('Books', 'Read and learn');
INSERT INTO categories (name, description) VALUES ('Clothing', 'Wearables and fashion');