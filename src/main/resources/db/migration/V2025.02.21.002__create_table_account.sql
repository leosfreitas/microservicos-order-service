CREATE TABLE IF NOT EXISTS order_schema.order_tb (
    id_order VARCHAR(255) PRIMARY KEY,
    id_account VARCHAR(255) NOT NULL,
    dt_date TIMESTAMP NOT NULL,
    db_total NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_schema.order_item (
    id_order_item VARCHAR(255) PRIMARY KEY,
    id_order VARCHAR(255) NOT NULL,
    id_product VARCHAR(255) NOT NULL,
    num_quantity INTEGER NOT NULL,
    db_total NUMERIC(10, 2) NOT NULL,
    FOREIGN KEY (id_order) REFERENCES order_schema.order_tb(id_order)
);