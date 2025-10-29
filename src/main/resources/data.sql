
BEGIN;

CREATE TABLE IF NOT EXISTS suppliers (
	id integer PRIMARY KEY,
	name varchar(255) NOT NULL,
	contact_info text
);

CREATE TABLE IF NOT EXISTS products (
	id integer PRIMARY KEY,
	sku varchar(100),
	name varchar(255),
	category varchar(100),
	original_price numeric(12,2),
	unit_price numeric(12,2)
);

CREATE TABLE IF NOT EXISTS clients (
	id integer PRIMARY KEY,
	name varchar(255) NOT NULL,
	contact_info text,
	active boolean DEFAULT true
);

CREATE TABLE IF NOT EXISTS users (
	id uuid PRIMARY KEY,
	email varchar(255) UNIQUE,
	password_hash varchar(255),
	role varchar(50),
	active boolean DEFAULT true
);

CREATE TABLE IF NOT EXISTS warehouses (
	id integer PRIMARY KEY,
	code varchar(50),
	name varchar(255),
	active boolean DEFAULT true
);

CREATE TABLE IF NOT EXISTS carriers (
	id integer PRIMARY KEY,
	code varchar(50),
	name varchar(255),
	contact_email varchar(255),
	contact_phone varchar(50),
	base_shipping_rate numeric(12,2),
	max_capacity_daily_shipments integer,
	status varchar(50)
);

CREATE TABLE IF NOT EXISTS purchase_orders (
	id integer PRIMARY KEY,
	supplier_id integer REFERENCES suppliers(id),
	status varchar(50),
	created_at timestamp,
	expected_delivery timestamp
);

CREATE TABLE IF NOT EXISTS purchase_order_lines (
	id integer PRIMARY KEY,
	purchase_order_id integer REFERENCES purchase_orders(id),
	product_id integer REFERENCES products(id),
	quantity integer,
	unit_price numeric(12,2)
);

CREATE TABLE IF NOT EXISTS sales_orders (
	id integer PRIMARY KEY,
	client_id integer REFERENCES clients(id),
	status varchar(50),
	created_at timestamp,
	shipped_at timestamp,
	delivered_at timestamp
);

CREATE TABLE IF NOT EXISTS sales_order_lines (
	id integer PRIMARY KEY,
	sales_order_id integer REFERENCES sales_orders(id),
	product_id integer REFERENCES products(id),
	quantity integer,
	unit_price numeric(12,2),
	backorder boolean DEFAULT false
);

CREATE TABLE IF NOT EXISTS shipments (
	id integer PRIMARY KEY,
	tracking_number varchar(100),
	status varchar(50),
	planned_date timestamp,
	shipped_at timestamp,
	delivered_at timestamp
);

CREATE TABLE IF NOT EXISTS inventories (
	id integer PRIMARY KEY,
	warehouse_id integer REFERENCES warehouses(id),
	product_id integer REFERENCES products(id),
	qty_on_hand integer,
	qty_reserved integer
);

CREATE TABLE IF NOT EXISTS inventory_movements (
	id integer PRIMARY KEY,
	movement_type varchar(50),
	quantity integer,
	occurred_at timestamp,
	reference_document varchar(255),
	description text
);

INSERT INTO suppliers (id, name, contact_info) VALUES
 (1, 'Alpha Supplies', 'alpha@supplies.example | +33123456789'),
 (2, 'Beta Components', 'contact@beta.example | +33111222333'),
 (3, 'Gamma Parts', 'sales@gamma.example | +33199887766');

INSERT INTO products (id, sku, name, category, original_price, unit_price) VALUES
 (1, 'P-001', 'Widget Basic', 'Widgets', 10.00, 8.50),
 (2, 'P-002', 'Widget Pro', 'Widgets', 20.00, 17.50),
 (3, 'P-003', 'Gadget X', 'Gadgets', 35.00, 30.00);

INSERT INTO clients (id, name, contact_info, active) VALUES
 (1, 'Client One SARL', 'client1@ex.com | +33100000001', true),
 (2, 'Client Deux SA', 'client2@ex.com | +33100000002', true),
 (3, 'Client Trois', 'client3@ex.com | +33100000003', false);

INSERT INTO users (id, email, password_hash, role, active) VALUES
 ('3fa85f64-5717-4562-b3fc-2c963f66afa6', 'admin@example.com', '$2a$10$examplehashadmin', 'ADMIN', true),
 ('6fa85f64-5717-4562-b3fc-2c963f66afb7', 'warehouse@example.com', '$2a$10$examplehashwh', 'WAREHOUSE_MANAGER', true),
 ('9fa85f64-5717-4562-b3fc-2c963f66afc8', 'client@example.com', '$2a$10$examplehashclient', 'CLIENT', true);

-- Warehouses
INSERT INTO warehouses (id, code, name, active) VALUES
 (1, 'WH-01', 'Main Warehouse', true),
 (2, 'WH-02', 'Secondary Warehouse', true),
 (3, 'WH-03', 'Overflow', false);

-- Carriers
INSERT INTO carriers (id, code, name, contact_email, contact_phone, base_shipping_rate, max_capacity_daily_shipments, status) VALUES
 (1, 'CR-UPS', 'FastExpress', 'ops@fastexpress.example', '+33123459999', 5.50, 500, 'ACTIVE'),
 (2, 'CR-DHL', 'DHL Local', 'dhl@local.example', '+33123458888', 6.00, 300, 'INACTIVE'),
 (3, 'CR-SML', 'SmallCarrier', 'info@small.example', '+33123457777', 4.00, 150, 'SUSPENDED');

-- Purchase Orders
INSERT INTO purchase_orders (id, supplier_id, status, created_at, expected_delivery) VALUES
 (1, 1, 'CREATED', '2025-10-01T09:00:00', '2025-10-10T17:00:00'),
 (2, 2, 'APPROVED', '2025-09-20T10:30:00', '2025-09-27T16:00:00'),
 (3, 3, 'RECEIVED', '2025-08-15T08:15:00', '2025-08-25T12:00:00');

-- Purchase Order Lines
INSERT INTO purchase_order_lines (id, purchase_order_id, product_id, quantity, unit_price) VALUES
 (1, 1, 1, 100, 8.00),
 (2, 1, 3, 50, 29.50),
 (3, 2, 2, 200, 17.00);

-- Sales Orders
INSERT INTO sales_orders (id, client_id, status, created_at, shipped_at, delivered_at) VALUES
 (1, 1, 'CREATED', '2025-10-05T11:00:00', NULL, NULL),
 (2, 2, 'RESERVED', '2025-09-22T14:20:00', '2025-09-24T09:00:00', NULL),
 (3, 1, 'SHIPPED', '2025-08-10T07:45:00', '2025-08-12T12:00:00', '2025-08-14T15:30:00');

-- Sales Order Lines
INSERT INTO sales_order_lines (id, sales_order_id, product_id, quantity, unit_price, backorder) VALUES
 (1, 1, 1, 10, 8.50, false),
 (2, 2, 2, 5, 17.50, false),
 (3, 3, 3, 2, 30.00, false);

-- Shipments
INSERT INTO shipments (id, tracking_number, status, planned_date, shipped_at, delivered_at) VALUES
 (1, 'TRK0001', 'PLANNED', '2025-10-06T08:00:00', NULL, NULL),
 (2, 'TRK0002', 'IN_TRANSIT', '2025-09-23T09:00:00', '2025-09-24T09:30:00', NULL),
 (3, 'TRK0003', 'DELIVERED', '2025-08-11T06:00:00', '2025-08-12T10:00:00', '2025-08-14T15:00:00');

-- Inventories
INSERT INTO inventories (id, warehouse_id, product_id, qty_on_hand, qty_reserved) VALUES
 (1, 1, 1, 500, 20),
 (2, 1, 2, 200, 10),
 (3, 2, 3, 150, 5);

-- Inventory Movements
INSERT INTO inventory_movements (id, movement_type, quantity, occurred_at, reference_document, description) VALUES
 (1, 'INBOUND', 100, '2025-10-02T13:00:00', 'PO-1', 'Inbound from supplier Alpha'),
 (2, 'OUTBOUND', 10, '2025-10-05T11:30:00', 'SO-1', 'Picked for sales order 1'),
 (3, 'ADJUSTMENT', -5, '2025-09-30T09:15:00', 'INV-ADJ-2025-09', 'Stock correction');

COMMIT;
