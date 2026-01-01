# Guide de Déploiement - TimeFlow

## 📋 Table des Matières
1. [Prérequis](#prérequis)
2. [Export du Projet](#export-du-projet)
3. [Import sur un Nouveau PC](#import-sur-un-nouveau-pc)
4. [Configuration Firebase](#configuration-firebase)
5. [Configuration Android Studio](#configuration-android-studio)
6. [Compilation et Exécution](#compilation-et-exécution)
7. [Résolution des Problèmes](#résolution-des-problèmes)

---

## 1. Prérequis

### Sur le PC Source (où se trouve actuellement le projet)
- ✅ Android Studio installé
- ✅ Projet TimeFlow fonctionnel
- ✅ Accès à Firebase Console

### Sur le PC Destination (nouveau PC)
- ✅ Android Studio installé (version récente recommandée)
- ✅ JDK installé (Java Development Kit)
- ✅ SDK Android installé
- ✅ Connexion Internet

---

## 2. Export du Projet

### Option A : Export via Android Studio (Recommandé)

1. **Ouvrir le projet** dans Android Studio
2. **File → Export Project**
   - Ou simplement copier le dossier du projet
3. **Vérifier les fichiers à inclure** :
   ```
   TimeFlow/
   ├── app/
   ├── gradle/
   ├── build.gradle.kts
   ├── settings.gradle.kts
   ├── gradlew
   ├── gradlew.bat
   └── .gitignore
   ```

### Option B : Créer une Archive ZIP

1. **Fermer Android Studio**
2. **Naviguer vers le dossier du projet** :
   ```
   C:\Users\syrine\AndroidStudioProjects\TimeFlow
   ```
3. **Créer une archive ZIP** de tout le dossier
4. **Vérifier la taille** (ne devrait pas dépasser 50-100 MB)

### Fichiers à Exclure (optionnel, pour réduire la taille)
- `.gradle/` (sera régénéré)
- `.idea/` (sera régénéré)
- `build/` (sera régénéré)
- `app/build/` (sera régénéré)
- `local.properties` (contient des chemins spécifiques au PC)

**Note** : Ces dossiers peuvent être exclus car ils seront régénérés automatiquement.

---

## 3. Import sur un Nouveau PC

### Étape 1 : Transférer le Projet

1. **Copier l'archive ZIP** sur le nouveau PC (USB, Cloud, Email, etc.)
2. **Extraire l'archive** dans un dossier accessible :
   ```
   C:\Users\[NomUtilisateur]\AndroidStudioProjects\TimeFlow
   ```

### Étape 2 : Ouvrir dans Android Studio

1. **Lancer Android Studio**
2. **File → Open**
3. **Sélectionner le dossier** `TimeFlow`
4. **Cliquer sur "OK"**

### Étape 3 : Synchronisation Gradle

Android Studio va automatiquement :
- Détecter le projet Gradle
- Télécharger les dépendances
- Synchroniser les fichiers

**Temps estimé** : 5-15 minutes (selon la connexion Internet)

---

## 4. Configuration Firebase

### Étape 1 : Accéder à Firebase Console

1. **Aller sur** : https://console.firebase.google.com/
2. **Se connecter** avec votre compte Google
3. **Sélectionner le projet** TimeFlow

### Étape 2 : Télécharger google-services.json

1. **Dans Firebase Console** :
   - Cliquer sur l'icône ⚙️ (Paramètres du projet)
   - Aller dans "Vos applications"
   - Sélectionner l'application Android
   - Cliquer sur "Télécharger google-services.json"

### Étape 3 : Placer le Fichier

1. **Copier** `google-services.json`
2. **Coller** dans le dossier :
   ```
   app/
   └── google-services.json
   ```

**Important** : Ce fichier doit être dans `app/` (pas dans `app/src/main/`)

### Étape 4 : Vérifier la Configuration

Ouvrir `app/build.gradle.kts` et vérifier :

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")  // ← Doit être présent
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
}
```

---

## 5. Configuration Android Studio

### Étape 1 : Vérifier le SDK Android

1. **File → Project Structure**
2. **SDK Location** : Vérifier que le SDK Android est configuré
3. **Compile SDK Version** : Minimum API 33 (Android 13)

### Étape 2 : Vérifier le JDK

1. **File → Project Structure → SDK Location**
2. **JDK Location** : Vérifier que JDK est configuré
   - Par défaut : `C:\Program Files\Android\Android Studio\jbr`

### Étape 3 : Synchroniser Gradle

1. **File → Sync Project with Gradle Files**
2. **Attendre** la fin de la synchronisation
3. **Vérifier** qu'il n'y a pas d'erreurs

### Étape 4 : Vérifier les Dépendances

Ouvrir `app/build.gradle.kts` et vérifier que toutes les dépendances sont présentes :

```kotlin
dependencies {
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
}
```

---

## 6. Compilation et Exécution

### Étape 1 : Nettoyer le Projet

1. **Build → Clean Project**
2. **Attendre** la fin du nettoyage

### Étape 2 : Reconstruire le Projet

1. **Build → Rebuild Project**
2. **Vérifier** qu'il n'y a pas d'erreurs de compilation

### Étape 3 : Exécuter l'Application

#### Option A : Sur un Émulateur

1. **Tools → Device Manager**
2. **Créer un émulateur** (si nécessaire) :
   - API Level : 33 ou supérieur
   - Taille d'écran : Recommandé (ex: Pixel 5)
3. **Lancer l'émulateur**
4. **Run → Run 'app'** (ou F10)

#### Option B : Sur un Appareil Physique

1. **Activer le mode développeur** sur votre téléphone :
   - Paramètres → À propos du téléphone
   - Appuyer 7 fois sur "Numéro de build"
2. **Activer le débogage USB** :
   - Paramètres → Options développeur → Débogage USB
3. **Connecter le téléphone** via USB
4. **Autoriser le débogage** (popup sur le téléphone)
5. **Run → Run 'app'**

---

## 7. Résolution des Problèmes

### Problème 1 : Erreur "SDK location not found"

**Solution** :
1. **File → Project Structure → SDK Location**
2. **Spécifier le chemin** du SDK Android :
   ```
   C:\Users\[NomUtilisateur]\AppData\Local\Android\Sdk
   ```
3. **Synchroniser** le projet

### Problème 2 : Erreur "google-services.json not found"

**Solution** :
1. **Vérifier** que `google-services.json` est dans `app/` (pas `app/src/main/`)
2. **Télécharger** le fichier depuis Firebase Console
3. **Reconstruire** le projet

### Problème 3 : Erreur "JAVA_HOME is not set"

**Solution** :
1. **Vérifier** que JDK est installé
2. **Dans Android Studio** :
   - File → Project Structure → SDK Location
   - Spécifier le JDK : `C:\Program Files\Android\Android Studio\jbr`
3. **Ou définir la variable d'environnement** JAVA_HOME

### Problème 4 : Erreur de compilation Gradle

**Solution** :
1. **File → Invalidate Caches / Restart**
2. **Sélectionner** "Invalidate and Restart"
3. **Attendre** le redémarrage
4. **Synchroniser** le projet

### Problème 5 : Dépendances non trouvées

**Solution** :
1. **File → Settings → Build, Execution, Deployment → Gradle**
2. **Vérifier** "Use Gradle from" : 'gradle-wrapper.properties' file
3. **File → Sync Project with Gradle Files**

### Problème 6 : Erreur "Package name does not match"

**Solution** :
1. **Vérifier** le package name dans :
   - `AndroidManifest.xml` : `package="com.example.timeflow"`
   - `google-services.json` : doit correspondre
2. **Vérifier** dans Firebase Console que le package name est correct

### Problème 7 : L'application ne se connecte pas à Firebase

**Solution** :
1. **Vérifier** la connexion Internet
2. **Vérifier** que `google-services.json` est à jour
3. **Vérifier** les règles Firestore dans Firebase Console
4. **Vérifier** que Firebase est activé dans le projet Firebase

---

## 8. Checklist de Vérification

Avant de considérer le déploiement réussi, vérifier :

- [ ] Le projet s'ouvre sans erreurs dans Android Studio
- [ ] La synchronisation Gradle se termine sans erreurs
- [ ] Le fichier `google-services.json` est présent dans `app/`
- [ ] Toutes les dépendances sont téléchargées
- [ ] Le projet compile sans erreurs (Build → Rebuild Project)
- [ ] L'application s'installe sur l'émulateur/appareil
- [ ] L'authentification Firebase fonctionne
- [ ] Les données se sauvegardent dans Firestore
- [ ] Toutes les fonctionnalités sont opérationnelles

---

## 9. Configuration Alternative : Utiliser Git

### Si vous utilisez Git (Recommandé pour le travail en équipe)

1. **Créer un dépôt Git** (GitHub, GitLab, etc.)
2. **Ajouter les fichiers** :
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin [URL_DU_DEPOT]
   git push -u origin main
   ```
3. **Sur le nouveau PC** :
   ```bash
   git clone [URL_DU_DEPOT]
   cd TimeFlow
   ```
4. **Ouvrir dans Android Studio** et suivre les étapes 4-6

**Avantages** :
- ✅ Versionning du code
- ✅ Partage facile entre membres
- ✅ Historique des modifications
- ✅ Pas besoin de copier manuellement

---

## 10. Informations Importantes

### Fichiers Sensibles (NE PAS PARTAGER)

- `google-services.json` : Contient des clés API (peut être partagé dans l'équipe)
- `local.properties` : Contient des chemins spécifiques au PC (ne pas partager)
- Clés de signature : Ne jamais partager

### Fichiers à Toujours Inclure

- ✅ Tous les fichiers `.java` dans `app/src/main/java/`
- ✅ Tous les fichiers XML dans `app/src/main/res/`
- ✅ `AndroidManifest.xml`
- ✅ `build.gradle.kts` (app et projet)
- ✅ `settings.gradle.kts`
- ✅ `gradle.properties`
- ✅ `google-services.json`

---

## 11. Commandes Utiles

### Via Terminal (dans le dossier du projet)

```bash
# Nettoyer le projet
.\gradlew.bat clean

# Compiler le projet
.\gradlew.bat assembleDebug

# Installer sur un appareil connecté
.\gradlew.bat installDebug

# Voir les dépendances
.\gradlew.bat dependencies
```

---

## 12. Support et Aide

### En cas de problème persistant :

1. **Vérifier les logs** :
   - View → Tool Windows → Logcat
   - Filtrer par "Error" ou "Exception"

2. **Vérifier la documentation** :
   - `DOCUMENTATION.md`
   - `GUIDE_CODE.md`

3. **Vérifier Firebase Console** :
   - Que le projet est actif
   - Que les règles Firestore permettent l'accès

---

## 📝 Résumé Rapide

1. **Exporter** : Copier le dossier du projet ou créer une archive ZIP
2. **Transférer** : Sur le nouveau PC (USB, Cloud, etc.)
3. **Importer** : Ouvrir dans Android Studio (File → Open)
4. **Configurer Firebase** : Télécharger et placer `google-services.json`
5. **Synchroniser** : File → Sync Project with Gradle Files
6. **Compiler** : Build → Rebuild Project
7. **Exécuter** : Run → Run 'app'

---

**Temps estimé total** : 30-60 minutes (selon la connexion Internet pour télécharger les dépendances)

**Bon déploiement ! 🚀**

