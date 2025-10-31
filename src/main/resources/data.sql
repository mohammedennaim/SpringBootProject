-- =================================================
-- 0. Extension UUID
-- =================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto"; BEGIN;

-- =================================================
-- 1. Base Tables
-- =================================================

-- Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

INSERT INTO
    users (email, password, role)
VALUES (
        'admin1@system.com',
        'adminpass',
        'ADMIN'
    ),
    (
        'manager1@system.com',
        'managerpass',
        'WAREHOUSE_MANAGER'
    ),
    (
        'client1@system.com',
        'pass123',
        'CLIENT'
    ),
    (
        'client2@system.com',
        'pass456',
        'CLIENT'
    ),
    (
        'client3@system.com',
        'pass789',
        'CLIENT'
    );

-- Clients
CREATE TABLE IF NOT EXISTS clients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_client_user FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);

-- Insert clients linked to users created above (use VALUES with subselects to be safe in psql)
INSERT INTO clients (id, name, contact)
VALUES
  ((SELECT id FROM users WHERE email = 'client1@system.com'), 'Client One', 'client1@example.com'),
  ((SELECT id FROM users WHERE email = 'client2@system.com'), 'Client Two', 'client2@example.com'),
  ((SELECT id FROM users WHERE email = 'client3@system.com'), 'Client Three', 'client3@example.com')
ON CONFLICT DO NOTHING;

-- Admins
CREATE TABLE IF NOT EXISTS admins (
    id UUID PRIMARY KEY,
    notes VARCHAR(255),
    CONSTRAINT fk_admin_user FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);

-- Only create admin rows for existing users to respect FK constraints
INSERT INTO
    admins (id, notes)
SELECT id, 'Super admin'
FROM users
WHERE
    email = 'admin1@system.com';

-- Warehouses
CREATE TABLE IF NOT EXISTS warehouses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    code VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

INSERT INTO
    warehouses (code, name, active)
VALUES (
        'MAIN',
        'Main Warehouse',
        TRUE
    ),
    (
        'BACKUP',
        'Backup Warehouse',
        TRUE
    ),
    (
        'EAST',
        'East Warehouse',
        TRUE
    );

-- Managers
CREATE TABLE IF NOT EXISTS managers (
    id UUID PRIMARY KEY,
    warehouse_id UUID,
    CONSTRAINT fk_manager_user FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_manager_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE SET NULL
);

-- Only create manager rows for existing users
INSERT INTO
    managers (id, warehouse_id)
SELECT id, (
        SELECT id
        FROM warehouses
        WHERE
            code = 'MAIN'
    )
FROM users
WHERE
    email = 'manager1@system.com';

-- Suppliers
CREATE TABLE IF NOT EXISTS suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    name VARCHAR(255) NOT NULL,
    contact_info TEXT
);

INSERT INTO
    suppliers (name, contact_info)
VALUES (
        'Supplier One',
        'supplier1@example.com'
    ),
    (
        'Supplier Two',
        'supplier2@example.com'
    ),
    (
        'Supplier Three',
        'supplier3@example.com'
    );

-- Carriers
CREATE TABLE IF NOT EXISTS carriers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    code VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(255),
    max_daily_shipments INTEGER,
    shipping_rate NUMERIC(10, 2),
    status VARCHAR(20) CHECK (
        status IN (
            'ACTIVE',
            'INACTIVE',
            'SUSPENDED'
        )
    )
);

INSERT INTO
    carriers (
        code,
        name,
        email,
        phone,
        max_daily_shipments,
        shipping_rate,
        status
    )
VALUES (
        'CARR-001',
        'FastShip',
        'contact@fastship.com',
        '555-0101',
        100,
        5.00,
        'ACTIVE'
    ),
    (
        'CARR-002',
        'QuickTrans',
        'info@quicktrans.com',
        '555-0102',
        80,
        4.50,
        'ACTIVE'
    ),
    (
        'CARR-003',
        'SafeLog',
        'support@safelog.com',
        '555-0103',
        60,
        6.00,
        'INACTIVE'
    );

-- Products
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    sku VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    unit_price NUMERIC(10, 2),
    active BOOLEAN DEFAULT TRUE
);

INSERT INTO
    products (
        sku,
        name,
        category,
        unit_price,
        active
    )
VALUES (
        'SKU-A',
        'Product A',
        'General',
        100.00,
        TRUE
    ),
    (
        'SKU-B',
        'Product B',
        'Electronics',
        250.00,
        TRUE
    ),
    (
        'SKU-C',
        'Product C',
        'Accessories',
        75.00,
        TRUE
    );

-- Inventories
CREATE TABLE IF NOT EXISTS inventories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    qty_on_hand INT DEFAULT 0,
    qty_reserved INT DEFAULT 0,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_inventory_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE CASCADE
);

-- Inventories: insert per (product,warehouse) using subselects
INSERT INTO inventories (product_id, warehouse_id, qty_on_hand, qty_reserved)
VALUES
  ((SELECT id FROM products WHERE sku = 'SKU-A'), (SELECT id FROM warehouses WHERE code = 'MAIN'), 100, 5),
  ((SELECT id FROM products WHERE sku = 'SKU-B'), (SELECT id FROM warehouses WHERE code = 'BACKUP'), 50, 10),
  ((SELECT id FROM products WHERE sku = 'SKU-C'), (SELECT id FROM warehouses WHERE code = 'EAST'), 200, 0)
ON CONFLICT DO NOTHING;

-- Inventory Movements
CREATE TABLE IF NOT EXISTS inventory_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    type VARCHAR(20) CHECK (
        type IN (
            'INBOUND',
            'OUTBOUND',
            'ADJUSTMENT'
        )
    ),
    occurred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reference_document VARCHAR(255),
    description VARCHAR(255),
    CONSTRAINT fk_movement_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_movement_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE CASCADE
);

-- Inventory movements: use VALUES with subselects
INSERT INTO inventory_movements (product_id, warehouse_id, quantity, type, reference_document, description)
VALUES
  ((SELECT id FROM products WHERE sku='SKU-A'), (SELECT id FROM warehouses WHERE code='MAIN'), 50, 'INBOUND', 'PO-1001', 'Initial stock'),
  ((SELECT id FROM products WHERE sku='SKU-B'), (SELECT id FROM warehouses WHERE code='BACKUP'), -10, 'OUTBOUND', 'SO-2001', 'Shipment to client'),
  ((SELECT id FROM products WHERE sku='SKU-C'), (SELECT id FROM warehouses WHERE code='EAST'), 5, 'ADJUSTMENT', 'ADJ-3001', 'Inventory correction')
ON CONFLICT DO NOTHING;

-- Purchase Orders
CREATE TABLE IF NOT EXISTS purchase_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    supplier_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expected_delivery TIMESTAMP,
    status VARCHAR(20) CHECK (
        status IN (
            'CREATED',
            'APPROVED',
            'RECEIVED',
            'CANCELED'
        )
    ),
    CONSTRAINT fk_purchase_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id) ON DELETE SET NULL
);

    -- Insert a few purchase orders for the first suppliers. Use VALUES with subselects to avoid
    -- potential UNION parsing issues during psql execution in init scripts.
    INSERT INTO purchase_orders (supplier_id, expected_delivery, status)
    VALUES
      ((SELECT id FROM suppliers ORDER BY id LIMIT 1), CURRENT_TIMESTAMP + INTERVAL '7 days', 'CREATED'),
      ((SELECT id FROM suppliers ORDER BY id OFFSET 1 LIMIT 1), CURRENT_TIMESTAMP + INTERVAL '10 days', 'APPROVED'),
      ((SELECT id FROM suppliers ORDER BY id OFFSET 2 LIMIT 1), CURRENT_TIMESTAMP + INTERVAL '3 days', 'RECEIVED')
    ON CONFLICT DO NOTHING;

-- Purchase Order Lines
CREATE TABLE IF NOT EXISTS purchase_order_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    purchase_order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_pol_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_pol_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- Purchase order lines: reference purchase_orders and products by subselects
INSERT INTO purchase_order_lines (purchase_order_id, product_id, quantity, unit_price)
VALUES
  ((SELECT id FROM purchase_orders ORDER BY id LIMIT 1), (SELECT id FROM products WHERE sku='SKU-A' LIMIT 1), 50, 90.00),
  ((SELECT id FROM purchase_orders ORDER BY id OFFSET 1 LIMIT 1), (SELECT id FROM products WHERE sku='SKU-B' LIMIT 1), 20, 230.00),
  ((SELECT id FROM purchase_orders ORDER BY id OFFSET 2 LIMIT 1), (SELECT id FROM products WHERE sku='SKU-C' LIMIT 1), 100, 70.00)
ON CONFLICT DO NOTHING;

-- Sales Orders
CREATE TABLE IF NOT EXISTS sales_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    client_id UUID NOT NULL,
    status VARCHAR(20) CHECK (
        status IN (
            'CREATED',
            'RESERVED',
            'SHIPPED',
            'DELIVERED',
            'CANCELED'
        )
    ),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    CONSTRAINT fk_sales_order_client FOREIGN KEY (client_id) REFERENCES clients (id) ON DELETE CASCADE
);

-- Sales orders: create one order per first three clients
INSERT INTO sales_orders (client_id, status)
VALUES
  ((SELECT id FROM clients ORDER BY id LIMIT 1), 'CREATED'),
  ((SELECT id FROM clients ORDER BY id OFFSET 1 LIMIT 1), 'SHIPPED'),
  ((SELECT id FROM clients ORDER BY id OFFSET 2 LIMIT 1), 'DELIVERED')
ON CONFLICT DO NOTHING;

-- Sales Order Lines
CREATE TABLE IF NOT EXISTS sales_order_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    sales_order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    backorder BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_sol_order FOREIGN KEY (sales_order_id) REFERENCES sales_orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_sol_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- Sales order lines: link to the sales_orders created above and products
INSERT INTO sales_order_lines (sales_order_id, product_id, quantity, unit_price, backorder)
VALUES
  ((SELECT id FROM sales_orders ORDER BY id LIMIT 1), (SELECT id FROM products WHERE sku='SKU-A' LIMIT 1), 5, 100.00, FALSE),
  ((SELECT id FROM sales_orders ORDER BY id OFFSET 1 LIMIT 1), (SELECT id FROM products WHERE sku='SKU-B' LIMIT 1), 2, 250.00, FALSE),
  ((SELECT id FROM sales_orders ORDER BY id OFFSET 2 LIMIT 1), (SELECT id FROM products WHERE sku='SKU-C' LIMIT 1), 1, 75.00, TRUE)
ON CONFLICT DO NOTHING;

-- Shipments
CREATE TABLE IF NOT EXISTS shipments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    order_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    carrier_id UUID,
    shipped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP,
    tracking_number VARCHAR(255),
    status VARCHAR(20) CHECK (
        status IN (
            'PLANNED',
            'IN_TRANSIT',
            'DELIVERED'
        )
    ),
    CONSTRAINT fk_shipment_order FOREIGN KEY (order_id) REFERENCES sales_orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_shipment_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE SET NULL,
    CONSTRAINT fk_shipment_carrier FOREIGN KEY (carrier_id) REFERENCES carriers (id) ON DELETE SET NULL
);

-- Shipments: create a few shipments referencing orders, warehouses and carriers
INSERT INTO shipments (order_id, warehouse_id, carrier_id, status, tracking_number)
VALUES
  ((SELECT id FROM sales_orders ORDER BY id LIMIT 1), (SELECT id FROM warehouses WHERE code='MAIN' LIMIT 1), (SELECT id FROM carriers WHERE code='CARR-001' LIMIT 1), 'PLANNED', 'TRK-001'),
  ((SELECT id FROM sales_orders ORDER BY id OFFSET 1 LIMIT 1), (SELECT id FROM warehouses WHERE code='BACKUP' LIMIT 1), (SELECT id FROM carriers WHERE code='CARR-002' LIMIT 1), 'IN_TRANSIT', 'TRK-002'),
  ((SELECT id FROM sales_orders ORDER BY id OFFSET 2 LIMIT 1), (SELECT id FROM warehouses WHERE code='EAST' LIMIT 1), (SELECT id FROM carriers WHERE code='CARR-003' LIMIT 1), 'DELIVERED', 'TRK-003')
ON CONFLICT DO NOTHING;

COMMIT;