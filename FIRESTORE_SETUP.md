# Configuration Firestore pour TimeFlow

## 📋 Vue d'ensemble

TimeFlow utilise Firebase Firestore pour stocker les données des utilisateurs et leurs activités. Ce document explique comment configurer Firestore dans la console Firebase.

## 🔥 Étapes de configuration

### 1. Accéder à la Console Firebase

1. Allez sur [https://console.firebase.google.com/](https://console.firebase.google.com/)
2. Sélectionnez votre projet `timeflow-45760` (ou créez-en un nouveau)
3. Dans le menu de gauche, cliquez sur **"Firestore Database"**

### 2. Créer la base de données

1. Si c'est la première fois, cliquez sur **"Créer une base de données"**
2. Choisissez le mode :
   - **Mode production** (recommandé pour la production)
   - **Mode test** (pour le développement - règles moins strictes)
3. Sélectionnez l'emplacement de votre base de données (ex: `europe-west1`)

### 3. Structure des données

TimeFlow utilise la structure suivante dans Firestore :

```
users/
  └── {userId}/
      ├── name: string
      ├── email: string
      ├── createdAt: timestamp
      └── activities/ (sous-collection)
          └── {activityId}/
              ├── title: string
              ├── duration: number (en minutes)
              ├── description: string (optionnel)
              └── createdAt: timestamp
```

### 4. Configurer les règles de sécurité

Dans l'onglet **"Règles"** de Firestore, configurez les règles suivantes :

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Règle pour les documents utilisateurs
    match /users/{userId} {
      // L'utilisateur peut lire et écrire uniquement son propre document
      allow read, write: if request.auth != null && request.auth.uid == userId;
      
      // Règle pour les activités de l'utilisateur
      match /activities/{activityId} {
        // L'utilisateur peut lire et écrire uniquement ses propres activités
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

**Important :** Ces règles garantissent que :
- Seuls les utilisateurs authentifiés peuvent accéder aux données
- Chaque utilisateur ne peut accéder qu'à ses propres données
- Les activités sont protégées de la même manière

### 5. Créer un index (si nécessaire)

Si vous utilisez des requêtes avec `orderBy`, vous devrez peut-être créer un index :

1. Allez dans l'onglet **"Index"**
2. Si une erreur apparaît lors de l'exécution de l'application, Firebase vous proposera de créer l'index automatiquement
3. Cliquez sur le lien pour créer l'index

Pour l'historique (tri par date décroissante), l'index suivant est nécessaire :
- Collection: `users/{userId}/activities`
- Champs: `createdAt` (Descending)

### 6. Activer l'authentification

1. Dans la console Firebase, allez dans **"Authentication"**
2. Cliquez sur **"Commencer"** si ce n'est pas déjà fait
3. Activez la méthode **"Email/Password"**
4. Activez l'option **"Email/Password"** dans les méthodes de connexion

## 📊 Vérification

### Tester la connexion

1. Lancez l'application
2. Créez un compte avec un email et un mot de passe
3. Connectez-vous
4. Ajoutez une activité

### Vérifier dans Firestore

1. Retournez dans la console Firebase
2. Allez dans **Firestore Database**
3. Vous devriez voir :
   - Une collection `users`
   - Un document avec l'ID de l'utilisateur (UID)
   - Une sous-collection `activities` avec les activités créées

## 🔒 Sécurité

### Bonnes pratiques

1. **Règles de sécurité** : Toujours configurer des règles strictes
2. **Validation** : L'application valide déjà les données côté client, mais Firestore offre une couche de sécurité supplémentaire
3. **Authentification** : Assurez-vous que l'authentification est activée

### Règles recommandées pour la production

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow create: if request.auth != null && request.auth.uid == userId;
      allow update, delete: if request.auth != null && request.auth.uid == userId;
      
      match /activities/{activityId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

## 🐛 Dépannage

### Erreur "Missing or insufficient permissions"

- Vérifiez que les règles de sécurité sont correctement configurées
- Vérifiez que l'utilisateur est bien authentifié

### Erreur "Index required"

- Allez dans l'onglet "Index" de Firestore
- Créez l'index manquant (Firebase vous donnera un lien direct)

### Données non visibles

- Vérifiez que vous êtes connecté avec le bon compte
- Vérifiez les règles de sécurité
- Vérifiez que les données sont bien créées (onglet "Données" dans Firestore)

## 📝 Notes importantes

1. **Mode test vs Production** : En mode test, les règles sont plus permissives (30 jours). Passez en mode production pour la sécurité.

2. **Coûts** : Firestore a un plan gratuit généreux, mais surveillez votre utilisation si vous avez beaucoup d'utilisateurs.

3. **Backup** : Pensez à activer les sauvegardes automatiques dans Firebase.

## ✅ Checklist de configuration

- [ ] Base de données Firestore créée
- [ ] Règles de sécurité configurées
- [ ] Authentification Email/Password activée
- [ ] Index créé (si nécessaire)
- [ ] Test de création de compte réussi
- [ ] Test d'ajout d'activité réussi
- [ ] Vérification des données dans la console

---

**Besoin d'aide ?** Consultez la [documentation officielle de Firestore](https://firebase.google.com/docs/firestore)

