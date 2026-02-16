CREATE TABLE t_inventory (
    id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    sku_code VARCHAR(255) DEFAULT NULL,
    quantity INT DEFAULT NULL
);

ALTER TABLE t_inventory ADD CONSTRAINT uc_t_inventory_sku_code UNIQUE (sku_code);