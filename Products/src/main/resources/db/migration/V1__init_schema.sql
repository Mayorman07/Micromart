-- 1. Create Categories
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6)
);

-- 2. Create Products
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    stock_quantity INT,
    image_url VARCHAR(255),
    sku VARCHAR(255) NOT NULL UNIQUE,  -- <--- CHANGED THIS from 'sku_code' to 'sku'
    active BOOLEAN DEFAULT TRUE,       -- (Keep this from the previous fix)
    category_id BIGINT,
    created_at DATETIME(6),
    updated_at DATETIME(6),

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 3. Seed Data
INSERT INTO categories (name, description, created_at, updated_at)
VALUES
    ('Eyewear', 'Smart glasses, VR headsets, and vision tech', NOW(), NOW()),
    ('RC Hobbies', 'Remote controlled cars, boats, and planes', NOW(), NOW()),
    ('Drones', 'Aerial photography and racing quadcopters', NOW(), NOW()),
    ('Anime Collectibles', 'Figures, statues, and limited edition merchandise', NOW(), NOW());