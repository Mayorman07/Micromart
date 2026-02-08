-- 1. Create Categories (Flat Structure)
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 2. Create Products (Linked to Category)
CREATE TABLE products (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    stock_quantity INT,
    image_url VARCHAR(255),
    sku_code VARCHAR(255) NOT NULL UNIQUE,
    category_id BIGINT,
    created_at DATETIME(6),
    updated_at DATETIME(6),

    -- Named constraint for easier migrations later
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 3. Seed Data (Categories for Navbar)
INSERT INTO categories (name, description)
VALUES
    ('Eyewear', 'Smart glasses, VR headsets, and vision tech'),
    ('RC Hobbies', 'Remote controlled cars, boats, and planes'),
    ('Drones', 'Aerial photography and racing quadcopters'),
    ('Anime Collectibles', 'Figures, statues, and limited edition merchandise');