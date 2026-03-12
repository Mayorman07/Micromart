-- Create the Orders Table
CREATE TABLE `orders` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    user_email VARCHAR(255),
    total_amount DECIMAL(19, 2),
    order_status VARCHAR(255),
    cancellation_reason VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6)
);

-- Create the Order Line Items Table
CREATE TABLE order_line_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_code VARCHAR(255),
    product_name VARCHAR(255),
    image_url VARCHAR(255),
    unit_price DECIMAL(19, 2),
    quantity INT,
    order_id BIGINT,
    CONSTRAINT fk_order_line_items_order_id FOREIGN KEY (order_id) REFERENCES `orders`(id) ON DELETE CASCADE
);