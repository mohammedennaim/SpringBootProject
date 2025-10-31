package com.example.digitallogistics.config;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.digitallogistics.model.entity.Admin;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.entity.Manager;
import com.example.digitallogistics.model.entity.Product;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.model.entity.Inventory;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.repository.AdminRepository;
import com.example.digitallogistics.repository.ClientRepository;
import com.example.digitallogistics.repository.InventoryRepository;
import com.example.digitallogistics.repository.ManagerRepository;
import com.example.digitallogistics.repository.ProductRepository;
import com.example.digitallogistics.repository.SalesOrderRepository;
import com.example.digitallogistics.repository.UserRepository;
import com.example.digitallogistics.repository.WarehouseRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;
    private final ManagerRepository managerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(UserRepository userRepository,
                           ClientRepository clientRepository,
                           AdminRepository adminRepository,
                           ManagerRepository managerRepository,
                           WarehouseRepository warehouseRepository,
                           ProductRepository productRepository,
                           InventoryRepository inventoryRepository,
                           SalesOrderRepository salesOrderRepository,
                           PasswordEncoder passwordEncoder,
                           JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
        this.managerRepository = managerRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        seedClients();
        seedAdmin();
        seedWarehousesAndManager();
        seedProductsAndInventory();
        seedSalesOrderAndPayment();
    }

    private void seedClients() {
        // Seed clients that are referenced in data.sql
        String[][] seeds = new String[][]{
                {"client1@system.com", "pass123", "Client One", "client1@example.com"},
                {"client2@system.com", "pass456", "Client Two", "client2@example.com"},
                {"client3@system.com", "pass789", "Client Three", "client3@example.com"}
        };

        for (String[] s : seeds) {
            String email = s[0];
            String plain = s[1];
            String name = s[2];
            String contact = s[3];

            Optional<com.example.digitallogistics.model.entity.User> maybeUser = userRepository.findByEmail(email);
            if (maybeUser.isEmpty()) {
                // create new Client user (will insert into users + clients via JPA)
                Client c = new Client();
                c.setEmail(email);
                c.setPassword(passwordEncoder.encode(plain));
                c.setActive(true);
                c.setName(name);
                c.setContact(contact);
                clientRepository.save(c);
                continue;
            }

            com.example.digitallogistics.model.entity.User u = maybeUser.get();
            // ensure the parent User password is encoded
            boolean parentChanged = false;
            if (!isPasswordEncoded(u.getPassword())) {
                u.setPassword(passwordEncoder.encode(plain));
                parentChanged = true;
            }
            if (parentChanged) {
                userRepository.save(u);
            }

            // ensure client child row exists and has expected fields
            if (u instanceof Client) {
                Client existing = (Client) u;
                boolean changed = false;
                if (existing.getName() == null) { existing.setName(name); changed = true; }
                if (existing.getContact() == null) { existing.setContact(contact); changed = true; }
                if (!isPasswordEncoded(existing.getPassword())) { existing.setPassword(passwordEncoder.encode(plain)); changed = true; }
                if (changed) clientRepository.save(existing);
            } else {
                // if user exists but is not a Client, create client child with same id
                Client child = new Client();
                child.setId(u.getId());
                child.setName(name);
                child.setContact(contact);
                child.setActive(true);
                clientRepository.save(child);
            }
        }
    }

    private void seedAdmin() {
        Optional<com.example.digitallogistics.model.entity.User> maybe = userRepository.findByEmail("admin1@system.com");
        if (maybe.isEmpty()) {
            // fallback to older admin@example.com
            maybe = userRepository.findByEmail("admin@example.com");
        }
        if (maybe.isEmpty()) {
            Admin a = new Admin();
            a.setEmail("admin1@system.com");
            a.setPassword(passwordEncoder.encode("adminpass"));
            a.setActive(true);
            a.setNotes("Super admin");
            adminRepository.save(a);
        }
        else {
            maybe.ifPresent(u -> {
                if (u instanceof Admin) {
                    Admin existing = (Admin) u;
                    boolean changed = false;
                    if (!isPasswordEncoded(existing.getPassword())) {
                        existing.setPassword(passwordEncoder.encode("adminpass"));
                        changed = true;
                    }
                    if (existing.getNotes() == null) {
                        existing.setNotes("Super admin");
                        changed = true;
                    }
                    if (changed) {
                        adminRepository.save(existing);
                    }
                }
            });
        }
    }

    private void seedWarehousesAndManager() {
        // Ensure main warehouse exists and keep `main` effectively final by using orElseGet
        Warehouse main = warehouseRepository.findByCode("MAIN").stream().findFirst().orElseGet(() -> {
            Warehouse w = new Warehouse();
            w.setCode("MAIN");
            w.setName("Main Warehouse");
            w.setActive(true);
            return warehouseRepository.save(w);
        });

        // Ensure backup warehouse exists
        if (warehouseRepository.findByCode("BACKUP").isEmpty()) {
            Warehouse w = new Warehouse();
            w.setCode("BACKUP");
            w.setName("Backup Warehouse");
            w.setActive(true);
            warehouseRepository.save(w);
        }

        Optional<com.example.digitallogistics.model.entity.User> maybe = userRepository.findByEmail("manager1@system.com");
        if (maybe.isEmpty()) {
            maybe = userRepository.findByEmail("manager1@example.com");
        }
        if (maybe.isEmpty()) {
            Manager m = new Manager();
            m.setEmail("manager1@system.com");
            m.setPassword(passwordEncoder.encode("managerpass"));
            m.setActive(true);
            m.setWarehouseId(main.getId());
            managerRepository.save(m);
        } else {
            // ensure manager row has warehouse set
            maybe.ifPresent(u -> {
                if (u instanceof Manager) {
                    Manager mm = (Manager) u;
                    boolean changed = false;
                    if (!isPasswordEncoded(mm.getPassword())) {
                        mm.setPassword(passwordEncoder.encode("managerpass"));
                        changed = true;
                    }
                    if (mm.getWarehouseId() == null || !mm.getWarehouseId().equals(main.getId())) {
                        mm.setWarehouseId(main.getId());
                        changed = true;
                    }
                    if (changed) {
                        managerRepository.save(mm);
                    }
                }
            });
        }
    }

    private boolean isPasswordEncoded(String p) {
        if (p == null) return false;
        return p.startsWith("$2a$") || p.startsWith("$2b$") || p.startsWith("$2y$");
    }

    private void seedProductsAndInventory() {
        if (productRepository.findByNameContainingIgnoreCase("Product A").isEmpty()) {
            Product p = new Product();
            p.setSku("SKU-A");
            p.setName("Product A");
            p.setCategory("General");
            p.setUnitPrice(BigDecimal.valueOf(100.00));
            p.setActive(true);
            p = productRepository.save(p);

            Warehouse w = warehouseRepository.findByCode("MAIN").stream().findFirst().orElse(null);
            if (w != null) {
                Inventory inv = new Inventory();
                inv.setProduct(p);
                inv.setWarehouse(w);
                inv.setQtyOnHand(100);
                inv.setQtyReserved(0);
                inventoryRepository.save(inv);
            }
        }

        if (productRepository.findByNameContainingIgnoreCase("Product B").isEmpty()) {
            Product p = new Product();
            p.setSku("SKU-B");
            p.setName("Product B");
            p.setCategory("General");
            p.setUnitPrice(BigDecimal.valueOf(250.00));
            p.setActive(true);
            productRepository.save(p);
        }

        if (productRepository.findByNameContainingIgnoreCase("Product C").isEmpty()) {
            Product p = new Product();
            p.setSku("SKU-C");
            p.setName("Product C");
            p.setCategory("General");
            p.setUnitPrice(BigDecimal.valueOf(75.00));
            p.setActive(true);
            productRepository.save(p);
        }
    }

    private void seedSalesOrderAndPayment() {
        // create a simple order for an existing client (pick any existing client to avoid name mismatches)
        clientRepository.findAll().stream().findFirst().ifPresent(client -> {
            if (salesOrderRepository.findByClientId(client.getId()).isEmpty()) {
                SalesOrder so = new SalesOrder();
                so.setClient(client);
                so.setStatus(OrderStatus.CREATED);
                salesOrderRepository.save(so);
                // ensure JPA flushes the insert so the JDBC call can see the FK target
                salesOrderRepository.flush();

                // insert a payment row via JDBC (no Payment entity in project)
                // use BigDecimal for amount to be explicit
                jdbcTemplate.update(
                    "INSERT INTO payments (id, order_id, amount, paid_at) VALUES (gen_random_uuid(), ?, ?, CURRENT_TIMESTAMP) ON CONFLICT DO NOTHING",
                    so.getId(), java.math.BigDecimal.valueOf(100.00)
                );
            }
        });
    }
}
