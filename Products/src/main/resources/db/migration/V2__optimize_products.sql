CREATE INDEX idx_products_sku_code ON products (sku_code);
ALTER TABLE products MODIFY price DECIMAL(19, 2) NOT NULL DEFAULT 0.00;