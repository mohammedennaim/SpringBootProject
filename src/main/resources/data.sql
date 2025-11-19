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

INSERT INTO users (email, password, role, active)
VALUES
    -- adminpass
    ('admin1@system.com', '$2b$12$SOSkZ5.7N/yDRk98mWh8vODelmKDl8Rf4IQ1A1Y2sQmiabKqUnAEe', 'ADMIN', TRUE),
    -- managerpass
    ('manager1@system.com', '$2b$12$jQhrmF9Q9kzmwK3NLuLWvuvsXN9wzBt9/bZU6S7blMeN0qNeJQOqa', 'WAREHOUSE_MANAGER', TRUE),
    -- pass123
    ('client1@system.com', '$2b$12$E.KK5idmjKgMm5Udin8LwemDQYPVP4Y41jTG1xYlNdBEuVDGZ7BdK', 'CLIENT', TRUE),
    -- pass456
    ('client2@system.com', '$2b$12$A9E3GMyUSqSPloLwsmTmL.w2i/QZQBC5Yvu2kDRP6dDDu4no03nxC', 'CLIENT', TRUE),
    -- pass789
    ('client3@system.com', '$2b$12$TL9C8rgl5NSiroGZ1ynsxO.zoUu6UaWcsz2EKLm4lrmUiURzaa./q', 'CLIENT', TRUE)
ON CONFLICT (email) DO UPDATE
SET password = EXCLUDED.password,
        role = EXCLUDED.role,
        active = EXCLUDED.active;
-- Make users inserts idempotent: skip if same email exists

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
INSERT INTO admins (id, notes)
SELECT u.id, 'Super admin'
FROM users u
WHERE u.email = 'admin1@system.com'
  AND NOT EXISTS (SELECT 1 FROM admins a WHERE a.id = u.id);

-- Warehouses
CREATE TABLE IF NOT EXISTS warehouses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    code VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 1
);

INSERT INTO
    warehouses (code, name, active, priority)
VALUES (
        'MAIN',
        'Main Warehouse',
        TRUE,
        1
    ),
    (
        'BACKUP',
        'Backup Warehouse',
        TRUE,
        2
    ),
    (
        'EAST',
        'East Warehouse',
        TRUE,
        3
    )
ON CONFLICT (code) DO UPDATE SET
  name = EXCLUDED.name,
  active = EXCLUDED.active,
  priority = EXCLUDED.priority;

-- Ensure we don't create duplicate warehouses by making code unique
CREATE UNIQUE INDEX IF NOT EXISTS idx_warehouses_code ON warehouses(code);
-- Make warehouses insert idempotent via unique index (above)

-- Managers
CREATE TABLE IF NOT EXISTS managers (
    id UUID PRIMARY KEY,
    warehouse_id UUID,
    CONSTRAINT fk_manager_user FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_manager_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE SET NULL
);

-- Only create manager rows for existing users
INSERT INTO managers (id, warehouse_id)
SELECT u.id, w.id
FROM users u
JOIN warehouses w ON w.code = 'MAIN'
WHERE u.email = 'manager1@system.com'
  AND NOT EXISTS (SELECT 1 FROM managers m WHERE m.id = u.id);

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
    )
ON CONFLICT (name) DO UPDATE SET
  contact_info = EXCLUDED.contact_info;

-- Make supplier names unique to allow idempotent inserts
CREATE UNIQUE INDEX IF NOT EXISTS idx_suppliers_name ON suppliers(name);

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
    )
ON CONFLICT (code) DO UPDATE SET
  name = EXCLUDED.name,
  email = EXCLUDED.email,
  phone = EXCLUDED.phone,
  max_daily_shipments = EXCLUDED.max_daily_shipments,
  shipping_rate = EXCLUDED.shipping_rate,
  status = EXCLUDED.status;

-- Make carrier code unique so repeated runs don't create duplicates
CREATE UNIQUE INDEX IF NOT EXISTS idx_carriers_code ON carriers(code);

-- Products
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    sku VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    unit_price NUMERIC(10, 2),
    profit NUMERIC(10, 2) DEFAULT 1.00,
    active BOOLEAN DEFAULT TRUE,
    image VARCHAR(500)
);

INSERT INTO
    products (
        sku,
        name,
        category,
        unit_price,
        profit,
        active,
        image
    )
VALUES (
        'SKU-A',
        'Product A',
        'General',
        100.00,
        1.10,
        TRUE,
        'https://via.placeholder.com/300x300?text=Product+A'
    ),
    (
        'SKU-B',
        'Product B',
        'Electronics',
        250.00,
        1.25,
        TRUE,
        'https://via.placeholder.com/300x300?text=Product+B'
    ),
    (
        'SKU-C',
        'Product C',
        'Accessories',
        75.00,
        1.05,
        TRUE,
        'https://via.placeholder.com/300x300?text=Product+C'
    )
ON CONFLICT (sku) DO UPDATE SET
  name = EXCLUDED.name,
  category = EXCLUDED.category,
  unit_price = EXCLUDED.unit_price,
  profit = EXCLUDED.profit,
  active = EXCLUDED.active,
  image = EXCLUDED.image;

-- Make product SKU unique to allow idempotent inserts
CREATE UNIQUE INDEX IF NOT EXISTS idx_products_sku ON products(sku);

-- Migration: ensure product 'profit' and 'image' columns exist for older DBs
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='products' AND column_name='profit'
    ) THEN
        ALTER TABLE products ADD COLUMN profit NUMERIC(10,2) DEFAULT 1.00;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='products' AND column_name='image'
    ) THEN
        ALTER TABLE products ADD COLUMN image VARCHAR(500);
    END IF;
    
    -- Ajouter la colonne priority aux warehouses si elle n'existe pas
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='warehouses' AND column_name='priority'
    ) THEN
        ALTER TABLE warehouses ADD COLUMN priority INTEGER DEFAULT 1;
    END IF;
END $$;

-- Populate profit and image values for seeded SKUs if null
UPDATE products SET 
  profit = 1.10,
  image = 'https://via.placeholder.com/300x300?text=Product+A'
WHERE sku = 'SKU-A' AND (profit IS NULL OR profit = 0 OR image IS NULL);

UPDATE products SET 
  profit = 1.25,
  image = 'https://via.placeholder.com/300x300?text=Product+B'
WHERE sku = 'SKU-B' AND (profit IS NULL OR profit = 0 OR image IS NULL);

UPDATE products SET 
  profit = 1.05,
  image = 'https://via.placeholder.com/300x300?text=Product+C'
WHERE sku = 'SKU-C' AND (profit IS NULL OR profit = 0 OR image IS NULL);

-- Update warehouses priority if null
UPDATE warehouses SET priority = 1 WHERE priority IS NULL;

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

-- Add unique constraint for inventories
CREATE UNIQUE INDEX IF NOT EXISTS idx_inventories_product_warehouse ON inventories(product_id, warehouse_id);

-- Inventories: insert per (product,warehouse) using subselects
INSERT INTO inventories (product_id, warehouse_id, qty_on_hand, qty_reserved)
VALUES
  ((SELECT id FROM products WHERE sku = 'SKU-A'), (SELECT id FROM warehouses WHERE code = 'MAIN'), 100, 5),
  ((SELECT id FROM products WHERE sku = 'SKU-B'), (SELECT id FROM warehouses WHERE code = 'BACKUP'), 50, 10),
  ((SELECT id FROM products WHERE sku = 'SKU-C'), (SELECT id FROM warehouses WHERE code = 'EAST'), 200, 0)
ON CONFLICT (product_id, warehouse_id) DO UPDATE SET
  qty_on_hand = EXCLUDED.qty_on_hand,
  qty_reserved = EXCLUDED.qty_reserved;

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

-- =================================================
-- Advanced Logistics Tables
-- =================================================

-- Reservations
CREATE TABLE IF NOT EXISTS reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sales_order_id UUID NOT NULL,
    inventory_id UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    reserved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT fk_reservation_order FOREIGN KEY (sales_order_id) REFERENCES sales_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_inventory FOREIGN KEY (inventory_id) REFERENCES inventories(id) ON DELETE CASCADE
);

-- Back Orders
CREATE TABLE IF NOT EXISTS back_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    original_order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    warehouse_id UUID,
    quantity_needed INTEGER NOT NULL CHECK (quantity_needed > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fulfilled_at TIMESTAMP,
    is_fulfilled BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_backorder_order FOREIGN KEY (original_order_id) REFERENCES sales_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_backorder_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_backorder_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE SET NULL
);

-- Shipment Slots
CREATE TABLE IF NOT EXISTS shipment_slots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL,
    slot_date DATE NOT NULL,
    max_capacity INTEGER NOT NULL DEFAULT 100,
    current_usage INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_slot_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
    UNIQUE(warehouse_id, slot_date)
);

-- Sample data for Reservations
INSERT INTO reservations (sales_order_id, inventory_id, quantity, reserved_at, expires_at, is_active)
VALUES
  ((SELECT id FROM sales_orders ORDER BY id LIMIT 1), 
   (SELECT id FROM inventories WHERE product_id = (SELECT id FROM products WHERE sku='SKU-A') LIMIT 1), 
   5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '24 hours', true),
  ((SELECT id FROM sales_orders ORDER BY id OFFSET 1 LIMIT 1), 
   (SELECT id FROM inventories WHERE product_id = (SELECT id FROM products WHERE sku='SKU-B') LIMIT 1), 
   2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '24 hours', true)
ON CONFLICT DO NOTHING;

-- Sample data for Back Orders
INSERT INTO back_orders (original_order_id, product_id, warehouse_id, quantity_needed, created_at, is_fulfilled)
VALUES
  ((SELECT id FROM sales_orders ORDER BY id OFFSET 2 LIMIT 1), 
   (SELECT id FROM products WHERE sku='SKU-C'), 
   (SELECT id FROM warehouses WHERE code='EAST'), 
   5, CURRENT_TIMESTAMP, false)
ON CONFLICT DO NOTHING;

-- Sample data for Shipment Slots
INSERT INTO shipment_slots (warehouse_id, slot_date, max_capacity, current_usage)
VALUES
  ((SELECT id FROM warehouses WHERE code='MAIN'), CURRENT_DATE, 100, 25),
  ((SELECT id FROM warehouses WHERE code='MAIN'), CURRENT_DATE + INTERVAL '1 day', 100, 0),
  ((SELECT id FROM warehouses WHERE code='BACKUP'), CURRENT_DATE, 80, 15),
  ((SELECT id FROM warehouses WHERE code='BACKUP'), CURRENT_DATE + INTERVAL '1 day', 80, 0),
  ((SELECT id FROM warehouses WHERE code='EAST'), CURRENT_DATE, 60, 10),
  ((SELECT id FROM warehouses WHERE code='EAST'), CURRENT_DATE + INTERVAL '1 day', 60, 0)
ON CONFLICT (warehouse_id, slot_date) DO NOTHING;

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_reservations_expires_at ON reservations(expires_at);
CREATE INDEX IF NOT EXISTS idx_reservations_sales_order ON reservations(sales_order_id);
CREATE INDEX IF NOT EXISTS idx_reservations_inventory ON reservations(inventory_id);
CREATE INDEX IF NOT EXISTS idx_back_orders_product_warehouse ON back_orders(product_id, warehouse_id);
CREATE INDEX IF NOT EXISTS idx_back_orders_fulfilled ON back_orders(is_fulfilled);
CREATE INDEX IF NOT EXISTS idx_shipment_slots_warehouse_date ON shipment_slots(warehouse_id, slot_date);

-- =================================================
-- Migration: manager -> warehouses (one-to-many)
-- Safe idempotent approach WITHOUT dropping existing tables.
-- This will add a `manager_id` column to `warehouses` if missing,
-- populate it from `managers.warehouse_id`, add an index and a FK if needed.
-- It preserves the existing `managers.warehouse_id` column for backward compatibility.
-- =================================================
DO $$
BEGIN
    -- Add manager_id column to warehouses if it doesn't exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'warehouses' AND column_name = 'manager_id'
    ) THEN
        ALTER TABLE warehouses ADD COLUMN manager_id UUID;
    END IF;

    -- Add priority column to warehouses if it doesn't exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'warehouses' AND column_name = 'priority'
    ) THEN
        ALTER TABLE warehouses ADD COLUMN priority INTEGER DEFAULT 1;
    END IF;

    -- Update priority for existing warehouses
    UPDATE warehouses SET priority = 1 WHERE priority IS NULL;

    -- Populate warehouses.manager_id from managers.warehouse_id where applicable
    -- Only update rows where manager_id is currently null to avoid overwriting existing values
    UPDATE warehouses w
    SET manager_id = m.id
    FROM managers m
    WHERE m.warehouse_id IS NOT NULL
      AND w.id = m.warehouse_id
      AND (w.manager_id IS NULL OR w.manager_id <> m.id);

    -- Create an index on warehouses.manager_id for faster joins (idempotent)
    PERFORM 1 FROM pg_class WHERE relname = 'idx_warehouses_manager_id';
    IF NOT FOUND THEN
        CREATE INDEX idx_warehouses_manager_id ON warehouses(manager_id);
    END IF;

    -- Add a foreign key constraint if it doesn't already exist
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE t.relname = 'warehouses' AND c.conname = 'fk_warehouses_manager'
    ) THEN
        ALTER TABLE warehouses ADD CONSTRAINT fk_warehouses_manager FOREIGN KEY (manager_id) REFERENCES managers (id) ON DELETE SET NULL;
    END IF;

END$$;

COMMIT;