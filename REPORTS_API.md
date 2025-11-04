# 📊 API de Rapports et Statistiques - Documentation

## Vue d'ensemble

L'API `/api/reports` fournit des rapports détaillés et des statistiques sur différents aspects du système logistique :
- **Commandes** : Taux de livraison, revenus, backorders
- **Inventaire** : États de stock, ruptures, rotation
- **Expéditions** : Performance par transporteur, délais
- **Mouvements** : Historique filtré des mouvements de stock

## Endpoints disponibles

### 1. 📦 Statistiques des Commandes
```http
GET /api/reports/orders?fromDate=2024-10-01&toDate=2024-11-04
```

**Paramètres :**
- `fromDate` (optionnel) : Date de début (format: yyyy-MM-dd)
- `toDate` (optionnel) : Date de fin (format: yyyy-MM-dd)

**Réponse :**
```json
{
  "totalOrders": 150,
  "pendingOrders": 25,
  "processingOrders": 30,
  "shippedOrders": 45,
  "deliveredOrders": 40,
  "cancelledOrders": 10,
  "deliveryRate": 26.67,
  "backorderRate": 16.67,
  "cancellationRate": 6.67,
  "totalRevenue": 125000.00,
  "averageOrderValue": 833.33,
  "pendingRevenue": 18500.00,
  "averageProcessingTimeHours": 24.5,
  "averageShippingTimeHours": 48.2,
  "fromDate": "2024-10-01",
  "toDate": "2024-11-04"
}
```

---

### 2. 📋 Rapport d'Inventaire
```http
GET /api/reports/inventory?warehouseId=550e8400-e29b-41d4-a716-446655440000
```

**Paramètres :**
- `warehouseId` (optionnel) : ID de l'entrepôt (si non fourni, tous les entrepôts)

**Réponse :**
```json
{
  "totalProducts": 250,
  "activeProducts": 230,
  "inactiveProducts": 20,
  "outOfStockProducts": 15,
  "lowStockProducts": 35,
  "overstockedProducts": 25,
  "totalInventoryValue": 890000.00,
  "lowStockValue": 45000.00,
  "overstockValue": 125000.00,
  "stockTurnoverRate": 4.2,
  "stockoutRate": 6.0,
  "fillRate": 94.0,
  "topSellingProducts": [
    {
      "productSku": "PROD-001",
      "productName": "Produit A",
      "totalStock": 150,
      "availableStock": 140,
      "unitPrice": 25.00,
      "totalValue": 3750.00,
      "turnoverRate": 8.5
    }
  ],
  "criticalStockProducts": [
    {
      "productSku": "PROD-010",
      "productName": "Produit J",
      "totalStock": 5,
      "availableStock": 5,
      "unitPrice": 85.00,
      "totalValue": 425.00,
      "turnoverRate": 12.0
    }
  ],
  "warehouseStockSummaries": [
    {
      "warehouseName": "Entrepôt Principal",
      "totalProducts": 180,
      "totalStock": 15000,
      "availableStock": 13500,
      "totalValue": 650000.00,
      "outOfStockCount": 8
    }
  ]
}
```

---

### 3. 🚚 Statistiques d'Expéditions
```http
GET /api/reports/shipments?fromDate=2024-10-01&toDate=2024-11-04&carrierId=550e8400-e29b-41d4-a716-446655440001
```

**Paramètres :**
- `fromDate` (optionnel) : Date de début
- `toDate` (optionnel) : Date de fin  
- `carrierId` (optionnel) : ID du transporteur

**Réponse :**
```json
{
  "totalShipments": 120,
  "plannedShipments": 15,
  "inTransitShipments": 25,
  "deliveredShipments": 70,
  "delayedShipments": 8,
  "cancelledShipments": 2,
  "onTimeDeliveryRate": 51.67,
  "delayRate": 6.67,
  "cancellationRate": 1.67,
  "averageShippingTimeHours": 36.5,
  "averageDelayHours": 12.8,
  "carrierPerformances": [
    {
      "carrierCode": "DHL001",
      "carrierName": "DHL Express",
      "totalShipments": 45,
      "deliveredShipments": 42,
      "delayedShipments": 3,
      "onTimeRate": 93.3,
      "averageDeliveryTimeHours": 24.5,
      "currentCapacityUsed": 85,
      "maxDailyCapacity": 100
    }
  ],
  "shipmentsPerPeriod": {
    "2024-11-01": 15,
    "2024-11-02": 18,
    "2024-11-03": 12,
    "2024-11-04": 20
  },
  "topDestinations": [
    {
      "destinationCity": "Casablanca",
      "destinationCountry": "Maroc",
      "totalShipments": 35,
      "averageDeliveryTimeHours": 24.5,
      "onTimeRate": 92.0
    }
  ]
}
```

---

### 4. 📈 Historique des Mouvements
```http
GET /api/reports/movements?fromDateTime=2024-11-01T00:00:00&toDateTime=2024-11-04T23:59:59&warehouseId=550e8400-e29b-41d4-a716-446655440000&movementType=INBOUND&limit=50
```

**Paramètres :**
- `fromDateTime` (optionnel) : Date/heure de début (format: yyyy-MM-ddTHH:mm:ss)
- `toDateTime` (optionnel) : Date/heure de fin
- `warehouseId` (optionnel) : ID de l'entrepôt
- `movementType` (optionnel) : Type de mouvement (`INBOUND`, `OUTBOUND`, `ADJUSTMENT`, `TRANSFER`)
- `limit` : Nombre max de mouvements détaillés (défaut: 100, max: 1000)

**Réponse :**
```json
{
  "totalMovements": 450,
  "inboundMovements": 180,
  "outboundMovements": 200,
  "adjustmentMovements": 50,
  "transferMovements": 20,
  "totalInboundValue": 125000.00,
  "totalOutboundValue": 98000.00,
  "netMovementValue": 27000.00,
  "recentMovements": [
    {
      "timestamp": "2024-11-04T14:30:00",
      "productSku": "PROD-001",
      "productName": "Produit A",
      "warehouseName": "Entrepôt Principal",
      "movementType": "INBOUND",
      "quantity": 50,
      "unitPrice": 25.00,
      "totalValue": 1250.00,
      "reason": "Réception fournisseur",
      "reference": "REF-1001"
    }
  ],
  "movementsByType": [
    {
      "movementType": "INBOUND",
      "count": 180,
      "totalQuantity": 4500,
      "totalValue": 125000.00
    }
  ],
  "movementsByWarehouse": [
    {
      "warehouseName": "Entrepôt Principal",
      "totalMovements": 300,
      "inboundQuantity": 3200,
      "outboundQuantity": 2800,
      "netValue": 25000.00
    }
  ],
  "topMovedProducts": [
    {
      "productSku": "PROD-001",
      "productName": "Produit A",
      "totalQuantityMoved": 350,
      "movementCount": 45,
      "totalValue": 8750.00
    }
  ]
}
```

## Sécurité

🔒 **Tous les endpoints nécessitent l'authentification JWT et le rôle `ADMIN` ou `WAREHOUSE_MANAGER`**

**Headers requis :**
```http
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

## Codes de réponse

| Code | Description |
|------|-------------|
| `200` | Rapport généré avec succès |
| `400` | Paramètres invalides (dates, limit, etc.) |
| `401` | Non authentifié (token manquant/invalide) |
| `403` | Accès interdit (rôle insuffisant) |
| `404` | Ressource non trouvée (entrepôt, transporteur) |
| `500` | Erreur serveur |

## Exemples d'utilisation

### cURL Examples

```bash
# Rapport des commandes des 30 derniers jours
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8090/api/reports/orders"

# Inventaire d'un entrepôt spécifique
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8090/api/reports/inventory?warehouseId=550e8400-e29b-41d4-a716-446655440000"

# Expéditions sur une période
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8090/api/reports/shipments?fromDate=2024-10-01&toDate=2024-11-04"

# Mouvements entrants des 7 derniers jours
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8090/api/reports/movements?movementType=INBOUND&limit=50"
```

### JavaScript (Fetch)

```javascript
// Configuration de base
const API_BASE = 'http://localhost:8090/api';
const token = localStorage.getItem('jwt_token');

const headers = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
};

// Rapport des commandes
const orderReport = await fetch(`${API_BASE}/reports/orders?fromDate=2024-10-01`, 
  { headers })
  .then(res => res.json());

// Rapport d'inventaire
const inventoryReport = await fetch(`${API_BASE}/reports/inventory`, 
  { headers })
  .then(res => res.json());

// Rapport d'expéditions
const shipmentReport = await fetch(`${API_BASE}/reports/shipments?fromDate=2024-11-01&toDate=2024-11-04`, 
  { headers })
  .then(res => res.json());

// Mouvements de stock
const movementReport = await fetch(`${API_BASE}/reports/movements?movementType=OUTBOUND&limit=100`, 
  { headers })
  .then(res => res.json());
```

## Métriques clés

### 📊 Commandes
- **Taux de livraison** : % de commandes livrées avec succès
- **Taux de backorder** : % de commandes en attente 
- **Valeur moyenne** : Revenu moyen par commande
- **Temps de traitement** : Durée moyenne du processing

### 📦 Inventaire  
- **Rotation** : Fréquence de renouvellement du stock
- **Taux de rupture** : % de produits en rupture
- **Taux de service** : Capacité à satisfaire la demande
- **Valeur totale** : Valeur financière du stock

### 🚚 Expéditions
- **Livraison à temps** : % d'expéditions livrées dans les délais
- **Performance transporteur** : Comparaison des prestataires
- **Temps moyen** : Durée de livraison moyenne
- **Capacité utilisée** : Utilisation de la capacité transporteurs

### 📈 Mouvements
- **Volume** : Quantités entrantes/sortantes
- **Valeur** : Impact financier des mouvements
- **Fréquence** : Nombre de transactions par période
- **Répartition** : Distribution par type/entrepôt

---

*Documentation générée pour Digital Logistics API v1.0*  
*Endpoints sécurisés - Authentification JWT requise*