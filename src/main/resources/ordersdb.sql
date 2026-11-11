-- Drop tables if they already exist (for clean re-runs)
DROP TABLE IF EXISTS bill;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS client;

-- =============================================================
-- Table: client
-- =============================================================
CREATE TABLE client (
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    email   VARCHAR(150) NOT NULL,
    phone   VARCHAR(20)
);

-- =============================================================
-- Table: product
-- =============================================================
CREATE TABLE product (
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    price DOUBLE PRECISION NOT NULL CHECK (price >= 0),
    stock INTEGER       NOT NULL CHECK (stock >= 0)
);

-- =============================================================
-- Table: orders
-- =============================================================
CREATE TABLE orders (
    id        SERIAL PRIMARY KEY,
    clientid  INTEGER NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    productid INTEGER NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    quantity  INTEGER NOT NULL CHECK (quantity > 0)
);

-- =============================================================
-- Table: bill  (Log table - INSERT and SELECT only)
-- =============================================================
CREATE TABLE bill (
    id          SERIAL PRIMARY KEY,
    orderid     INTEGER          NOT NULL,
    clientname  VARCHAR(100)     NOT NULL,
    productname VARCHAR(100)     NOT NULL,
    quantity    INTEGER          NOT NULL,
    totalprice  DOUBLE PRECISION NOT NULL
);

-- =============================================================
-- Seed data
-- =============================================================

INSERT INTO client (name, address, email, phone) VALUES
    ('Alice Johnson',  '123 Maple St, Cluj-Napoca',   'alice.johnson@example.com',  '0740111222'),
    ('Bob Smith',      '456 Oak Ave, Bucharest',      'bob.smith@example.com',      '0741333444'),
    ('Carol Williams', '789 Pine Rd, Timisoara',      'carol.williams@example.com', '0742555666'),
    ('David Brown',    '321 Elm Blvd, Iasi',          'david.brown@example.com',    '0743777888');

INSERT INTO product (name, price, stock) VALUES
    ('Laptop',              1299.99, 20),
    ('Wireless Mouse',      25.50,   150),
    ('USB-C Hub',           45.00,   80),
    ('Mechanical Keyboard', 89.99,   60),
    ('Monitor 27"',         399.00,  15);