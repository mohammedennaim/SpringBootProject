# Guide : Voir les logs de l'application dans Kibana

## 🚀 Accès rapide

1. **Ouvrir Kibana dans le navigateur** :
   ```
   http://localhost:5601
   ```

2. **Attendre le chargement** (peut prendre 1-2 minutes si c'est la première fois)

---

## 📋 Étape 1 : Créer un Index Pattern

1. Dans Kibana, cliquez sur le menu **☰** (en haut à gauche)

2. Allez dans **Management** → **Stack Management**

3. Dans la section **Kibana**, cliquez sur **Index Patterns**

4. Cliquez sur **Create index pattern**

5. Dans le champ **Index pattern name**, saisissez :
   ```
   logisticsflow-*
   ```

6. Cliquez sur **Next step**

7. Dans **Time field**, sélectionnez **@timestamp** (ou **timestamp** si disponible)

8. Cliquez sur **Create index pattern**

✅ **Votre index pattern est maintenant créé !**

---

## 🔍 Étape 2 : Visualiser les logs

### Option A : Discover (Recherche)

1. Cliquez sur le menu **☰** → **Discover**

2. Dans le menu déroulant en haut (à côté de la barre de recherche), sélectionnez **logisticsflow-***

3. Vous verrez tous les logs de votre application !

4. **Filtrer les logs** :
   - Cliquez sur un champ dans la liste à gauche pour l'ajouter
   - Utilisez la barre de recherche en haut pour chercher du texte

### Option B : Logs (Vue spécialisée)

1. Cliquez sur le menu **☰** → **Observability** → **Logs**

2. Dans **Stream**, vous verrez les logs en temps réel

---

## 🔎 Étape 3 : Rechercher dans les logs

### Recherche simple

Dans la barre de recherche en haut de Discover, vous pouvez utiliser :

```
ERROR                          # Chercher tous les logs d'erreur
message : "database"          # Chercher "database" dans le champ message
level : ERROR                 # Filtrer par niveau de log
logger : "com.example"        # Filtrer par logger
```

### Recherche avancée (KQL - Kibana Query Language)

```
level:ERROR AND message:*database*     # Erreurs contenant "database"
level:INFO OR level:WARN               # Logs INFO ou WARN
timestamp > now()-1h                   # Logs de la dernière heure
logger:*Service*                       # Logs des classes Service
```

### Filtres par champ

1. Cliquez sur un champ dans la liste de gauche (ex: `level`, `logger`, `message`)
2. Cliquez sur **+** pour ajouter un filtre
3. Ou cliquez sur **-** pour exclure

---

## 📊 Champs utiles dans vos logs

Vos logs contiennent les champs suivants :

- **@timestamp** : Date et heure du log
- **message** : Le message du log
- **level** : Niveau (DEBUG, INFO, WARN, ERROR)
- **logger** : Nom de la classe logger
- **thread** : Nom du thread
- **application** : Nom de l'application
- **stack_trace** : Stack trace en cas d'erreur

---

## ⚡ Astuces rapides

### Voir les logs en temps réel

1. Dans **Discover**, cliquez sur **Auto-refresh** (en haut à droite)
2. Sélectionnez un intervalle (ex: **10 seconds**)

### Sauvegarder une recherche

1. Après avoir configuré vos filtres, cliquez sur **Save**
2. Donnez un nom à votre recherche
3. Vous pourrez la retrouver dans **Saved Objects**

### Créer un dashboard

1. Après avoir créé une recherche, allez dans **Visualize Library**
2. Créez des visualisations (graphiques, tableaux, etc.)
3. Ajoutez-les à un **Dashboard**

---

## 🛠️ Dépannage

### Je ne vois aucun log

1. Vérifiez que l'application tourne :
   ```bash
   docker-compose ps app-dev
   ```

2. Vérifiez que Logstash reçoit les logs :
   ```bash
   docker-compose logs logstash
   ```

3. Vérifiez que les index existent dans Elasticsearch :
   ```bash
   curl http://localhost:9200/_cat/indices?v | grep logisticsflow
   ```

4. Dans Kibana, vérifiez que l'index pattern `logisticsflow-*` existe et que la plage de dates couvre vos logs

### Les logs n'apparaissent pas en temps réel

- Cliquez sur **Auto-refresh** dans Discover
- Vérifiez la plage de dates en haut (cliquez sur l'horloge)

### "No results match your search criteria"

- Vérifiez la plage de dates sélectionnée
- Vérifiez que l'index pattern `logisticsflow-*` est sélectionné
- Essayez d'étendre la plage de dates

---

## 🎯 Exemples de recherches courantes

### Tous les logs d'erreur
```
level:ERROR
```

### Logs d'une classe spécifique
```
logger:*UserService*
```

### Logs contenant un mot-clé
```
message:*authentication*
```

### Logs de la dernière heure
```
timestamp > now()-1h
```

### Logs d'erreur avec stack trace
```
level:ERROR AND _exists_:stack_trace
```

---

## 📝 Configuration actuelle

- **Elasticsearch** : `http://localhost:9200`
- **Kibana** : `http://localhost:5601`
- **Logstash** : Port `5000`
- **Index pattern** : `logisticsflow-*`
- **Format des logs** : JSON (via Logstash)

---

Bon monitoring ! 🚀

