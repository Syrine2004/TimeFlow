# TimeFlow - Documentation du Projet

## 1. Thématique du Projet

**TimeFlow** est une application mobile Android de gestion du temps et de productivité. Elle permet aux utilisateurs de :
- Enregistrer leurs activités quotidiennes avec le temps passé
- Visualiser leur historique d'activités
- Analyser leur répartition du temps (études, loisirs, sport, etc.)
- Recevoir des conseils personnalisés basés sur leurs données
- Gérer leur profil utilisateur

L'application utilise **Firebase Authentication** pour l'authentification et **Firestore** pour le stockage des données.

---

## 2. Rôles des Membres de l'Équipe (6 membres)

### Membre 1 : **Authentification et Sécurité**
- **Interfaces développées** : LoginActivity, RegisterActivity
- **Responsabilités** :
  - Implémentation de l'authentification Firebase
  - Validation des formulaires (email, mot de passe)
  - Création de profils utilisateurs dans Firestore
  - Gestion de la navigation entre login et register

### Membre 2 : **Interface Principale et Navigation**
- **Interfaces développées** : HomeActivity
- **Responsabilités** :
  - Affichage du tableau de bord principal
  - Calcul et affichage des statistiques du jour
  - Navigation vers les autres sections
  - Message de bienvenue dynamique selon l'heure

### Membre 3 : **Gestion des Activités**
- **Interfaces développées** : AddTimeActivity, EditTimeActivity
- **Responsabilités** :
  - Formulaire d'ajout d'activité (titre, heures, minutes, description)
  - Modification d'activités existantes
  - Validation des données saisies
  - Sauvegarde dans Firestore

### Membre 4 : **Historique et Liste**
- **Interfaces développées** : HistoryActivity, HistoryAdapter
- **Responsabilités** :
  - Affichage de la liste des activités (RecyclerView)
  - Tri par date (plus récent en premier)
  - Actions de modification et suppression
  - Formatage de l'affichage (heures/minutes, dates)

### Membre 5 : **Analyse et Statistiques**
- **Interfaces développées** : AnalysisActivity, SimplePieChart, SimpleBarChart
- **Responsabilités** :
  - Calcul des statistiques globales et journalières
  - Graphique circulaire de répartition du temps
  - Catégorisation automatique des activités
  - Génération de conseils personnalisés

### Membre 6 : **Profil et Design**
- **Interfaces développées** : ProfileActivity
- **Responsabilités** :
  - Affichage du profil utilisateur
  - Génération d'avatar avec initiale
  - Fonction de déconnexion
  - Design global de l'application (thème noir/orange)

---

## 3. Fonctionnement des Principales Interfaces

### 3.1 LoginActivity
**Objectif** : Authentifier l'utilisateur existant

**Fonctionnalités** :
- Saisie email et mot de passe
- Validation du format email
- Connexion via Firebase Auth
- Redirection vers HomeActivity en cas de succès
- Lien vers RegisterActivity pour créer un compte

**Widgets utilisés** :
- `TextInputLayout` / `TextInputEditText` : Champs de saisie
- `MaterialButton` : Bouton de connexion
- `TextView` : Lien vers l'inscription

**Logique** :
```java
FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
```

---

### 3.2 RegisterActivity
**Objectif** : Créer un nouveau compte utilisateur

**Fonctionnalités** :
- Formulaire d'inscription (nom, email, mot de passe, confirmation)
- Validation des champs (email valide, mot de passe ≥ 6 caractères, correspondance)
- Création du compte Firebase
- Création du profil dans Firestore avec timestamp
- Redirection vers LoginActivity après inscription

**Widgets utilisés** :
- `TextInputLayout` / `TextInputEditText` : Champs de formulaire
- `MaterialButton` : Bouton d'inscription

**Logique** :
```java
// 1. Créer l'utilisateur Firebase
auth.createUserWithEmailAndPassword(email, password)

// 2. Créer le profil dans Firestore
db.collection("users").document(uid).set(userData)
```

---

### 3.3 HomeActivity
**Objectif** : Tableau de bord principal avec statistiques du jour

**Fonctionnalités** :
- Affichage du message de bienvenue (Bonjour/Bon après-midi/Bonsoir)
- Calcul du temps total du jour
- Nombre d'activités du jour
- Bouton pour ajouter une activité
- Navigation vers Historique, Analyse, Profil

**Widgets utilisés** :
- `MaterialCardView` : Carte principale
- `TextView` : Affichage des statistiques
- `MaterialButton` : Bouton d'ajout
- `LinearLayout` : Organisation des éléments

**Logique** :
```java
// Requête Firestore pour les activités d'aujourd'hui
db.collection("users").document(uid)
  .collection("activities")
  .get()
  // Filtrer par date >= début du jour
  // Calculer total minutes et nombre d'activités
```

---

### 3.4 AddTimeActivity / EditTimeActivity
**Objectif** : Ajouter ou modifier une activité

**Fonctionnalités** :
- Saisie titre, heures, minutes, description
- Validation (titre requis, heures ≥ 0, minutes 0-59)
- Conversion heures+minutes en minutes totales
- Sauvegarde dans Firestore avec timestamp
- Pré-remplissage pour la modification

**Widgets utilisés** :
- `TextInputLayout` / `TextInputEditText` : Champs de saisie
- `MaterialButton` : Bouton d'enregistrement

**Logique** :
```java
// Calcul durée totale
int duration = (hours * 60) + minutes;

// Sauvegarde Firestore
Map<String, Object> activity = {
    "title": title,
    "duration": duration,
    "description": description,
    "createdAt": FieldValue.serverTimestamp()
}
```

---

### 3.5 HistoryActivity
**Objectif** : Afficher l'historique des activités

**Fonctionnalités** :
- Liste des activités avec RecyclerView
- Tri par date décroissante (plus récent en premier)
- Affichage formaté (Xh Ymin, date)
- Boutons modifier/supprimer sur chaque item
- Dialogue de confirmation pour suppression

**Widgets utilisés** :
- `RecyclerView` : Liste scrollable
- `HistoryAdapter` : Adapter personnalisé
- `MaterialCardView` : Carte pour chaque activité
- `ImageButton` : Boutons modifier/supprimer

**Logique** :
```java
// Requête Firestore triée
.orderBy("createdAt", Query.Direction.DESCENDING)

// Suppression
.document(activityId).delete()
```

---

### 3.6 AnalysisActivity
**Objectif** : Analyser la répartition du temps

**Fonctionnalités** :
- Statistiques du jour (temps, nombre d'activités)
- Statistiques globales (total, moyenne)
- Graphique circulaire de répartition (Études, Loisirs, Sport, Autre)
- Catégorisation automatique par mots-clés
- Conseils personnalisés selon les données

**Widgets utilisés** :
- `SimplePieChart` : Vue personnalisée pour graphique
- `MaterialCardView` : Cartes de statistiques
- `TextView` : Affichage des conseils

**Logique** :
```java
// Catégorisation
if (title.contains("étude") || title.contains("cours")) → "Études"
if (title.contains("jeu") || title.contains("film")) → "Loisirs"
// etc.

// Calcul pourcentages et génération conseils
```

---

### 3.7 ProfileActivity
**Objectif** : Gérer le profil utilisateur

**Fonctionnalités** :
- Affichage nom, email, date de création
- Avatar généré avec initiale du nom
- Bouton de déconnexion
- Chargement des données depuis Firestore

**Widgets utilisés** :
- `MaterialCardView` : Carte de profil
- `TextView` : Informations utilisateur
- `MaterialButton` : Bouton déconnexion

**Logique** :
```java
// Génération initiale
String initial = name.charAt(0).toUpperCase();

// Déconnexion
FirebaseAuth.getInstance().signOut();
```

---

## 4. Architecture Technique

### 4.1 Structure des Données Firestore
```
users/
  {userId}/
    name: string
    email: string
    createdAt: timestamp
    activities/
      {activityId}/
        title: string
        duration: number (minutes)
        description: string
        createdAt: timestamp
```

### 4.2 Modèles de Données
- **ActivityModel** : Représente une activité (title, duration, description, createdAt, documentId)

### 4.3 Vues Personnalisées
- **SimplePieChart** : Graphique circulaire pour la répartition
- **SimpleBarChart** : Graphique en barres (préparé pour usage futur)

### 4.4 Style Graphique
- **Thème** : Noir/orange avec fond sombre (#0A0A0A)
- **Couleurs principales** : Orange (#FF6B35), Noir, Blanc
- **Cartes** : Fond sombre (#151515) avec bordures subtiles
- **Boutons** : Fond blanc avec texte noir

---

## 5. Technologies Utilisées

- **Langage** : Java
- **Framework** : Android SDK
- **Authentification** : Firebase Authentication
- **Base de données** : Cloud Firestore
- **UI** : Material Design Components
- **Architecture** : Activités Android standard

---

## 6. Points Clés pour la Soutenance

### Fonctionnalités à Démontrer :
1. Création de compte et connexion
2. Ajout d'activité avec heures/minutes
3. Visualisation de l'historique
4. Modification et suppression d'activités
5. Analyse avec graphique et conseils
6. Gestion du profil

### Points Techniques à Expliquer :
- Utilisation de Firebase Auth et Firestore
- RecyclerView avec adapter personnalisé
- Vues personnalisées (graphiques)
- Validation des formulaires
- Gestion des dates et timestamps

---

**Date de création** : 2024
**Version** : 1.0
**Équipe** : 6 membres

