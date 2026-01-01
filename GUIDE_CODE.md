# TimeFlow - Guide Complet du Code

## Table des Matières
1. [Structure du Projet](#structure-du-projet)
2. [Explication Détaillée du Code](#explication-détaillée-du-code)
3. [Flux de Données](#flux-de-données)
4. [Points Techniques Importants](#points-techniques-importants)

---

## Structure du Projet

```
app/src/main/
├── java/com/example/timeflow/
│   ├── activities/
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   ├── HomeActivity.java
│   │   ├── AddTimeActivity.java
│   │   ├── EditTimeActivity.java
│   │   ├── HistoryActivity.java
│   │   ├── HistoryAdapter.java
│   │   ├── AnalysisActivity.java
│   │   └── ProfileActivity.java
│   ├── models/
│   │   └── ActivityModel.java
│   └── views/
│       ├── SimplePieChart.java
│       └── SimpleBarChart.java
├── res/
│   ├── layout/ (fichiers XML des interfaces)
│   ├── values/
│   │   ├── colors.xml
│   │   └── strings.xml
│   └── drawable/ (icônes et backgrounds)
└── AndroidManifest.xml
```

---

## Explication Détaillée du Code

### 1. LoginActivity.java

**Rôle** : Authentification de l'utilisateur

**Méthodes principales** :

```java
private void login() {
    // 1. Récupération des valeurs
    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString().trim();
    
    // 2. Validation
    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        etEmail.setError("Email invalide");
        return;
    }
    
    // 3. Authentification Firebase
    auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener(result -> {
            // Succès : redirection vers HomeActivity
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        })
        .addOnFailureListener(e -> {
            // Erreur : affichage message
            Toast.makeText(this, "Erreur : " + e.getMessage(), ...).show();
        });
}
```

**Explication** :
- `Patterns.EMAIL_ADDRESS` : Classe Android pour valider le format email
- `signInWithEmailAndPassword()` : Méthode Firebase Auth pour se connecter
- `addOnSuccessListener()` : Callback exécuté si la connexion réussit
- `finish()` : Ferme l'activité actuelle pour éviter de revenir en arrière

---

### 2. RegisterActivity.java

**Rôle** : Création de compte utilisateur

**Méthodes principales** :

```java
private void register() {
    // 1. Validation des champs
    if (password.length() < 6) {
        etPassword.setError("Minimum 6 caractères");
        return;
    }
    
    if (!password.equals(confirmPassword)) {
        etConfirmPassword.setError("Les mots de passe ne correspondent pas");
        return;
    }
    
    // 2. Création du compte Firebase
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener(result -> {
            String uid = result.getUser().getUid();
            
            // 3. Création du profil dans Firestore
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("createdAt", FieldValue.serverTimestamp());
            
            db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Compte créé !", ...).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
        });
}
```

**Explication** :
- `createUserWithEmailAndPassword()` : Crée un utilisateur dans Firebase Auth
- `FieldValue.serverTimestamp()` : Ajoute automatiquement la date/heure du serveur
- `db.collection("users").document(uid).set(user)` : Crée un document dans Firestore
- Structure Firestore : `users/{userId}` avec les données du profil

---

### 3. HomeActivity.java

**Rôle** : Tableau de bord avec statistiques du jour

**Méthodes principales** :

```java
private void loadStats() {
    String uid = auth.getCurrentUser().getUid();
    
    // Calcul du début de la journée (00:00:00)
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    Date startOfDay = calendar.getTime();
    Timestamp startOfDayTimestamp = new Timestamp(startOfDay);
    
    // Requête Firestore
    db.collection("users")
        .document(uid)
        .collection("activities")
        .get()
        .addOnSuccessListener(query -> {
            long todayMinutes = 0;
            int todayCount = 0;
            
            // Parcourir toutes les activités
            for (QueryDocumentSnapshot doc : query) {
                Long duration = doc.getLong("duration");
                Timestamp createdAt = doc.getTimestamp("createdAt");
                
                // Filtrer seulement celles d'aujourd'hui
                if (duration != null && createdAt != null && 
                    createdAt.compareTo(startOfDayTimestamp) >= 0) {
                    todayMinutes += duration;
                    todayCount++;
                }
            }
            
            // Formater et afficher
            if (todayMinutes >= 60) {
                long hours = todayMinutes / 60;
                long minutes = todayMinutes % 60;
                tvTotalTime.setText(hours + "h " + minutes + "min");
            } else {
                tvTotalTime.setText(todayMinutes + " min");
            }
            
            tvActivityCount.setText(String.valueOf(todayCount));
        });
}
```

**Explication** :
- `Calendar.getInstance()` : Obtient la date/heure actuelle
- `Timestamp` : Type Firestore pour les dates
- `compareTo()` : Compare deux timestamps (>= 0 signifie après ou égal)
- Conversion minutes → heures/minutes pour l'affichage

---

### 4. AddTimeActivity.java

**Rôle** : Ajouter une nouvelle activité

**Méthodes principales** :

```java
private void saveActivity() {
    // 1. Récupération des valeurs
    String title = etTitle.getText().toString().trim();
    String hoursStr = etHours.getText().toString().trim();
    String minutesStr = etMinutes.getText().toString().trim();
    
    // 2. Validation
    if (TextUtils.isEmpty(title)) {
        etTitle.setError("Nom requis");
        return;
    }
    
    int hours = Integer.parseInt(hoursStr);
    int minutes = Integer.parseInt(minutesStr);
    
    if (hours < 0 || minutes < 0 || minutes >= 60) {
        etHours.setError("Durée invalide");
        return;
    }
    
    // 3. Conversion en minutes totales
    int duration = (hours * 60) + minutes;
    
    // 4. Préparation des données
    Map<String, Object> activity = new HashMap<>();
    activity.put("title", title);
    activity.put("duration", duration);
    activity.put("description", description);
    activity.put("createdAt", FieldValue.serverTimestamp());
    
    // 5. Sauvegarde dans Firestore
    db.collection("users")
        .document(uid)
        .collection("activities")
        .add(activity)  // .add() génère automatiquement un ID unique
        .addOnSuccessListener(doc -> {
            Toast.makeText(this, "Activité ajoutée", ...).show();
            finish(); // Retour à l'écran précédent
        });
}
```

**Explication** :
- `TextUtils.isEmpty()` : Vérifie si une chaîne est vide ou null
- `Integer.parseInt()` : Convertit String en int (peut lever NumberFormatException)
- `.add()` : Ajoute un document avec ID auto-généré
- `finish()` : Ferme l'activité et retourne à HomeActivity

---

### 5. EditTimeActivity.java

**Rôle** : Modifier une activité existante

**Différences avec AddTimeActivity** :
- Reçoit les données via `Intent` (activityId, title, duration, description)
- Pré-remplit les champs avec les valeurs existantes
- Utilise `.update()` au lieu de `.add()` pour modifier le document

```java
// Récupération des données depuis l'Intent
activityId = getIntent().getStringExtra("activityId");
String title = getIntent().getStringExtra("title");
long duration = getIntent().getLongExtra("duration", 0);

// Pré-remplissage
etTitle.setText(title);
long hours = duration / 60;
long minutes = duration % 60;
etHours.setText(String.valueOf(hours));
etMinutes.setText(String.valueOf(minutes));

// Mise à jour (pas création)
db.collection("users")
    .document(uid)
    .collection("activities")
    .document(activityId)  // ID spécifique
    .update(updates)  // .update() modifie seulement les champs spécifiés
```

**Explication** :
- `getIntent().getStringExtra()` : Récupère les données passées depuis HistoryActivity
- `.document(activityId).update()` : Met à jour un document existant
- Conversion inverse : minutes totales → heures + minutes pour l'affichage

---

### 6. HistoryActivity.java

**Rôle** : Afficher l'historique des activités

**Méthodes principales** :

```java
private void loadData() {
    String uid = FirebaseAuth.getInstance().getUid();
    
    // Requête avec tri par date décroissante
    FirebaseFirestore.getInstance()
        .collection("users")
        .document(uid)
        .collection("activities")
        .orderBy("createdAt", Query.Direction.DESCENDING)  // Plus récent en premier
        .get()
        .addOnSuccessListener(snapshot -> {
            list.clear();
            
            for (var doc : snapshot) {
                ActivityModel activity = doc.toObject(ActivityModel.class);
                activity.setDocumentId(doc.getId());  // Stocker l'ID pour modification/suppression
                list.add(activity);
            }
            
            adapter.notifyDataSetChanged();  // Rafraîchir la liste
        });
}

private void deleteActivity(ActivityModel activity) {
    // Dialogue de confirmation
    new AlertDialog.Builder(this)
        .setTitle("Supprimer l'activité")
        .setMessage("Êtes-vous sûr ?")
        .setPositiveButton("SUPPRIMER", (dialog, which) -> {
            // Suppression dans Firestore
            db.collection("users")
                .document(uid)
                .collection("activities")
                .document(activity.getDocumentId())
                .delete()
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Activité supprimée", ...).show();
                    loadData();  // Recharger la liste
                });
        })
        .setNegativeButton("ANNULER", null)
        .show();
}
```

**Explication** :
- `.orderBy("createdAt", DESCENDING)` : Trie les résultats (plus récent en premier)
- `doc.toObject(ActivityModel.class)` : Convertit le document Firestore en objet Java
- `doc.getId()` : Récupère l'ID du document (nécessaire pour update/delete)
- `AlertDialog` : Dialogue de confirmation Android standard
- `.delete()` : Supprime un document Firestore

---

### 7. HistoryAdapter.java

**Rôle** : Adapter pour afficher les activités dans le RecyclerView

**Structure** :

```java
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final List<ActivityModel> list;
    private OnItemClickListener listener;  // Interface pour les clics
    
    // Interface pour communiquer avec HistoryActivity
    public interface OnItemClickListener {
        void onEditClick(ActivityModel activity);
        void onDeleteClick(ActivityModel activity);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ActivityModel a = list.get(position);
        
        // Affichage des données
        h.tvTitle.setText(a.getTitle());
        
        // Formatage durée
        long duration = a.getDuration();
        if (duration >= 60) {
            long hours = duration / 60;
            long minutes = duration % 60;
            h.tvDuration.setText("⏱️ " + hours + "h " + minutes + "min");
        } else {
            h.tvDuration.setText("⏱️ " + duration + " min");
        }
        
        // Listeners pour les boutons
        h.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(a);  // Appelle HistoryActivity
            }
        });
    }
    
    // ViewHolder : contient les références aux vues
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDuration, tvDescription, tvDate;
        ImageButton btnEdit, btnDelete;
        
        ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            // ... autres initialisations
        }
    }
}
```

**Explication** :
- `RecyclerView.Adapter` : Classe de base pour les adapters de liste
- `ViewHolder` : Pattern pour réutiliser les vues (améliore les performances)
- `onBindViewHolder()` : Appelé pour chaque élément de la liste
- Interface `OnItemClickListener` : Permet à HistoryActivity de réagir aux clics

---

### 8. AnalysisActivity.java

**Rôle** : Analyser et visualiser les données

**Méthodes principales** :

```java
private String categorizeActivity(String title) {
    if (title == null) return "Autre";
    
    String lower = title.toLowerCase();
    
    // Détection par mots-clés
    if (lower.contains("étude") || lower.contains("cours") || 
        lower.contains("devoir")) {
        return "Études";
    } else if (lower.contains("jeu") || lower.contains("film") || 
               lower.contains("série")) {
        return "Loisirs";
    } else if (lower.contains("sport") || lower.contains("gym")) {
        return "Sport";
    }
    return "Autre";
}

private void displayChart(Map<String, Long> categoryTime) {
    List<SimplePieChart.SliceData> slices = new ArrayList<>();
    
    // Calcul du total
    long total = 0;
    for (Long value : categoryTime.values()) {
        total += value;
    }
    
    // Création des slices pour le graphique
    if (categoryTime.get("Études") > 0) {
        slices.add(new SimplePieChart.SliceData(
            "Études",
            categoryTime.get("Études"),
            colorEtudes,
            Color.WHITE
        ));
    }
    // ... autres catégories
    
    pieChart.setData(slices);  // Affiche le graphique
}

private void generateAdvice(Map<String, Long> categoryTime, long totalMinutes) {
    float etudesPercent = (etudesTime / (float) total) * 100;
    float loisirsPercent = (loisirsTime / (float) total) * 100;
    
    StringBuilder advice = new StringBuilder();
    
    // Génération de conseils selon les pourcentages
    if (etudesPercent > 70) {
        advice.append("• Vous consacrez beaucoup de temps aux études...\n\n");
    }
    
    if (loisirsPercent > 50) {
        advice.append("• Vous avez beaucoup de temps de loisir...\n\n");
    }
    
    tvAdvice.setText(advice.toString());
}
```

**Explication** :
- Catégorisation par mots-clés : Analyse le titre pour déterminer la catégorie
- Calcul de pourcentages : `(valeur / total) * 100`
- `SimplePieChart.SliceData` : Structure pour chaque portion du graphique
- Génération de conseils : Logique conditionnelle basée sur les ratios

---

### 9. ProfileActivity.java

**Rôle** : Afficher et gérer le profil

**Méthodes principales** :

```java
private void loadProfile() {
    String uid = auth.getCurrentUser().getUid();
    
    db.collection("users")
        .document(uid)
        .get()  // .get() pour un seul document
        .addOnSuccessListener(doc -> {
            String name = doc.getString("name");
            String email = doc.getString("email");
            
            tvName.setText(name);
            tvEmail.setText(email);
            
            // Génération de l'avatar avec initiale
            if (name != null && !name.isEmpty()) {
                String initial = String.valueOf(name.charAt(0)).toUpperCase();
                tvAvatar.setText(initial);
            } else if (email != null && !email.isEmpty()) {
                String initial = String.valueOf(email.charAt(0)).toUpperCase();
                tvAvatar.setText(initial);
            }
            
            // Formatage de la date
            Date date = doc.getDate("createdAt");
            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                tvCreatedAt.setText("Compte créé le " + sdf.format(date));
            }
        });
}

private void logout() {
    auth.signOut();  // Déconnexion Firebase
    startActivity(new Intent(this, LoginActivity.class));
    finishAffinity();  // Ferme toutes les activités de l'app
}
```

**Explication** :
- `.get()` : Récupère un seul document (pas une collection)
- `doc.getString("name")` : Récupère un champ String du document
- `name.charAt(0)` : Prend le premier caractère pour l'initiale
- `SimpleDateFormat` : Formate une date selon un pattern
- `finishAffinity()` : Ferme toutes les activités de la tâche (empêche retour)

---

### 10. ActivityModel.java

**Rôle** : Modèle de données pour une activité

```java
public class ActivityModel {
    private String title;
    private long duration;
    private String description;
    private Timestamp createdAt;
    private String documentId;  // ID Firestore pour modification/suppression
    
    // Constructeur vide requis par Firestore
    public ActivityModel() {}
    
    // Getters et Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    // ... autres getters/setters
}
```

**Explication** :
- Constructeur vide : Nécessaire pour Firestore (utilise la réflexion)
- Getters/Setters : Firestore les utilise pour mapper les données
- `Timestamp` : Type Firestore pour les dates
- `documentId` : Stocke l'ID du document pour les opérations CRUD

---

### 11. SimplePieChart.java

**Rôle** : Vue personnalisée pour afficher un graphique circulaire

**Méthodes principales** :

```java
@Override
protected void onDraw(Canvas canvas) {
    // Calcul des dimensions
    int width = getWidth();
    int height = getHeight();
    int size = Math.min(width, height) - 40;
    int centerX = width / 2;
    int centerY = height / 2;
    int radius = size / 2;
    
    RectF rect = new RectF(centerX - radius, centerY - radius, 
                          centerX + radius, centerY + radius);
    
    // Calcul du total
    float total = 0;
    for (SliceData slice : data) {
        total += slice.value;
    }
    
    // Dessin des arcs
    float startAngle = -90;  // Commence en haut
    
    for (SliceData slice : data) {
        float sweepAngle = (slice.value / total) * 360;  // Angle en degrés
        
        slicePaint.setColor(slice.color);
        canvas.drawArc(rect, startAngle, sweepAngle, true, slicePaint);
        
        startAngle += sweepAngle;  // Angle suivant
    }
}
```

**Explication** :
- `onDraw(Canvas)` : Méthode appelée pour dessiner la vue
- `Canvas` : Classe Android pour dessiner des formes
- `drawArc()` : Dessine un arc de cercle (portion du graphique)
- Calcul d'angle : `(valeur / total) * 360` donne l'angle en degrés
- `RectF` : Rectangle flottant pour définir les limites du cercle

---

## Flux de Données

### 1. Authentification
```
LoginActivity → Firebase Auth → HomeActivity
RegisterActivity → Firebase Auth → Firestore (profil) → LoginActivity
```

### 2. Ajout d'Activité
```
AddTimeActivity → Validation → Firestore (activities) → HomeActivity (recharge stats)
```

### 3. Modification/Suppression
```
HistoryActivity → HistoryAdapter → EditTimeActivity/Delete → Firestore → Recharge liste
```

### 4. Analyse
```
AnalysisActivity → Firestore (toutes activités) → Catégorisation → Graphique + Conseils
```

---

## Points Techniques Importants

### 1. Firebase Firestore
- **Collections** : `users/{userId}/activities/{activityId}`
- **Types de données** : String, Long, Timestamp, Map
- **Requêtes** : `.get()`, `.add()`, `.update()`, `.delete()`, `.orderBy()`
- **Callbacks** : `addOnSuccessListener()`, `addOnFailureListener()`

### 2. RecyclerView
- **Adapter** : Gère la création et le binding des vues
- **ViewHolder** : Réutilise les vues pour performance
- **notifyDataSetChanged()** : Rafraîchit la liste

### 3. Validation
- **Email** : `Patterns.EMAIL_ADDRESS.matcher(email).matches()`
- **Mot de passe** : Longueur minimale (6 caractères)
- **Durée** : Heures ≥ 0, Minutes 0-59

### 4. Formatage
- **Date** : `SimpleDateFormat("dd MMM yyyy", Locale.getDefault())`
- **Durée** : Conversion minutes → heures + minutes
- **Pourcentages** : `(valeur / total) * 100`

### 5. Navigation
- **Intent** : Passe des données entre activités
- **finish()** : Ferme l'activité actuelle
- **finishAffinity()** : Ferme toutes les activités

---

## Conseils pour la Soutenance

### Points à Expliquer :
1. **Architecture** : Structure MVC (Model-View-Controller)
2. **Firebase** : Pourquoi Firebase ? (Authentification + Base de données)
3. **RecyclerView** : Performance et réutilisation des vues
4. **Validation** : Sécurité des données utilisateur
5. **Graphiques** : Vue personnalisée avec Canvas

### Démonstration :
1. Créer un compte
2. Ajouter plusieurs activités (études, loisirs, sport)
3. Voir l'historique
4. Modifier une activité
5. Voir l'analyse avec graphique
6. Vérifier les conseils personnalisés

### Questions Possibles :
- **Pourquoi Firebase ?** : Solution cloud, authentification intégrée, temps réel
- **Performance ?** : RecyclerView pour les listes, pagination possible
- **Sécurité ?** : Validation côté client, règles Firestore côté serveur
- **Évolutions ?** : Export données, notifications, statistiques avancées

---

**Bon courage pour votre soutenance ! 🚀**

