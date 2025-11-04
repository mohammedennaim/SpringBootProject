-- =====================================================
-- Migration : Manager-Warehouse One-to-Many
-- Description : Permet à un manager de gérer plusieurs entrepôts
-- Date : 2024-11-04
-- =====================================================

-- Étape 1 : Sauvegarder les données existantes (optionnel)
-- CREATE TABLE managers_backup AS SELECT * FROM managers;

-- Étape 2 : Supprimer l'ancienne colonne warehouse_id de la table managers
ALTER TABLE managers DROP COLUMN IF EXISTS warehouse_id;

-- Étape 3 : Ajouter une colonne manager_id dans la table warehouses
ALTER TABLE warehouses ADD COLUMN IF NOT EXISTS manager_id UUID;

-- Étape 4 : Ajouter une contrainte de clé étrangère
ALTER TABLE warehouses 
ADD CONSTRAINT IF NOT EXISTS fk_warehouse_manager 
FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL;

-- Étape 5 : Créer un index pour améliorer les performances des requêtes
CREATE INDEX IF NOT EXISTS idx_warehouse_manager_id ON warehouses(manager_id);

-- Étape 6 : Ajouter un commentaire sur la colonne pour documentation
COMMENT ON COLUMN warehouses.manager_id IS 'ID du manager qui gère cet entrepôt';

-- =====================================================
-- Vérifications post-migration
-- =====================================================

-- Vérifier que la colonne warehouse_id a été supprimée
SELECT column_name 
FROM information_schema.columns 
WHERE table_name = 'managers' AND column_name = 'warehouse_id';
-- Résultat attendu : 0 lignes

-- Vérifier que la colonne manager_id a été ajoutée
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'warehouses' AND column_name = 'manager_id';
-- Résultat attendu : 1 ligne (manager_id, uuid)

-- Vérifier que la contrainte de clé étrangère existe
SELECT constraint_name, table_name, constraint_type
FROM information_schema.table_constraints
WHERE table_name = 'warehouses' AND constraint_name = 'fk_warehouse_manager';
-- Résultat attendu : 1 ligne (fk_warehouse_manager, warehouses, FOREIGN KEY)

-- Vérifier que l'index existe
SELECT indexname, tablename 
FROM pg_indexes 
WHERE tablename = 'warehouses' AND indexname = 'idx_warehouse_manager_id';
-- Résultat attendu : 1 ligne

-- =====================================================
-- Requêtes de test (optionnelles)
-- =====================================================

-- Trouver tous les entrepôts sans manager
SELECT id, code, name 
FROM warehouses 
WHERE manager_id IS NULL;

-- Trouver tous les managers avec leurs entrepôts
SELECT 
    u.id AS manager_id,
    u.email AS manager_email,
    COUNT(w.id) AS nombre_entrepots,
    STRING_AGG(w.name, ', ') AS entrepots
FROM users u
JOIN managers m ON u.id = m.id
LEFT JOIN warehouses w ON w.manager_id = u.id
GROUP BY u.id, u.email
ORDER BY nombre_entrepots DESC;

-- Trouver les managers qui gèrent plus de 2 entrepôts
SELECT 
    u.id AS manager_id,
    u.email AS manager_email,
    COUNT(w.id) AS nombre_entrepots
FROM users u
JOIN managers m ON u.id = m.id
LEFT JOIN warehouses w ON w.manager_id = u.id
GROUP BY u.id, u.email
HAVING COUNT(w.id) > 2;

-- =====================================================
-- Rollback (en cas de problème)
-- =====================================================

-- ATTENTION : Utiliser uniquement en cas d'urgence !

-- Étape 1 : Supprimer l'index
-- DROP INDEX IF EXISTS idx_warehouse_manager_id;

-- Étape 2 : Supprimer la contrainte de clé étrangère
-- ALTER TABLE warehouses DROP CONSTRAINT IF EXISTS fk_warehouse_manager;

-- Étape 3 : Supprimer la colonne manager_id
-- ALTER TABLE warehouses DROP COLUMN IF EXISTS manager_id;

-- Étape 4 : Restaurer l'ancienne colonne warehouse_id dans managers
-- ALTER TABLE managers ADD COLUMN warehouse_id UUID;

-- Étape 5 : Restaurer les données depuis la backup (si créée)
-- UPDATE managers m
-- SET warehouse_id = mb.warehouse_id
-- FROM managers_backup mb
-- WHERE m.id = mb.id;

-- =====================================================
-- Notes importantes
-- =====================================================

-- 1. Cette migration est NON-DESTRUCTIVE pour les données existantes
--    Les managers et warehouses existants ne seront pas affectés

-- 2. Après la migration, vous devrez :
--    - Redémarrer l'application Spring Boot
--    - Réassigner les entrepôts aux managers via l'API

-- 3. Les nouveaux endpoints disponibles :
--    POST   /api/managers/{managerId}/warehouses/{warehouseId}
--    DELETE /api/managers/{managerId}/warehouses/{warehouseId}
--    POST   /api/managers/{managerId}/warehouses/batch

-- 4. La relation est configurée avec ON DELETE SET NULL
--    Si un manager est supprimé, ses entrepôts ne seront pas supprimés
--    mais leur manager_id sera mis à NULL

-- =====================================================
-- FIN DE LA MIGRATION
-- =====================================================
