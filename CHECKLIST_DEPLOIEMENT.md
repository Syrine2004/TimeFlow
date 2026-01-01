# Checklist de Déploiement - TimeFlow

## ✅ Checklist Complète

### Phase 1 : Préparation (PC Source)

- [ ] Vérifier que le projet compile sans erreurs
- [ ] Tester toutes les fonctionnalités de l'application
- [ ] Vérifier que Firebase est configuré et fonctionne
- [ ] Créer une archive ZIP du projet (ou utiliser Git)
- [ ] Vérifier la taille de l'archive (ne devrait pas être trop volumineuse)

### Phase 2 : Transfert

- [ ] Copier l'archive sur le nouveau PC (USB, Cloud, Email, etc.)
- [ ] Extraire l'archive dans un dossier accessible
- [ ] Vérifier que tous les fichiers sont présents

### Phase 3 : Configuration (PC Destination)

- [ ] Installer Android Studio (si pas déjà fait)
- [ ] Installer JDK (généralement inclus avec Android Studio)
- [ ] Installer SDK Android (via Android Studio)
- [ ] Ouvrir le projet dans Android Studio
- [ ] Attendre la synchronisation Gradle

### Phase 4 : Configuration Firebase

- [ ] Accéder à Firebase Console
- [ ] Télécharger `google-services.json`
- [ ] Placer `google-services.json` dans le dossier `app/`
- [ ] Vérifier que le plugin Google Services est dans `build.gradle.kts`

### Phase 5 : Compilation

- [ ] File → Sync Project with Gradle Files
- [ ] Vérifier qu'il n'y a pas d'erreurs
- [ ] Build → Clean Project
- [ ] Build → Rebuild Project
- [ ] Vérifier qu'il n'y a pas d'erreurs de compilation

### Phase 6 : Test

- [ ] Créer/lancer un émulateur Android
- [ ] Ou connecter un appareil physique
- [ ] Run → Run 'app'
- [ ] Vérifier que l'application s'installe
- [ ] Tester la connexion (Login/Register)
- [ ] Tester l'ajout d'activité
- [ ] Tester l'historique
- [ ] Tester l'analyse
- [ ] Tester le profil

### Phase 7 : Vérification Finale

- [ ] Toutes les fonctionnalités fonctionnent
- [ ] Pas d'erreurs dans Logcat
- [ ] Les données se sauvegardent dans Firestore
- [ ] L'authentification fonctionne
- [ ] L'application est stable

---

## 🔧 Configuration Minimale Requise

### PC Source
- Android Studio (version récente)
- Projet fonctionnel
- Accès Internet

### PC Destination
- **OS** : Windows 10/11, macOS, ou Linux
- **RAM** : Minimum 8 GB (16 GB recommandé)
- **Espace disque** : Minimum 10 GB libres
- **Android Studio** : Version 2022.3 ou supérieure
- **JDK** : Version 17 ou supérieure
- **SDK Android** : API Level 33 (Android 13) minimum
- **Connexion Internet** : Pour télécharger les dépendances

---

## 📦 Fichiers Essentiels à Vérifier

### Fichiers Obligatoires
- [ ] `app/src/main/java/` (tous les fichiers .java)
- [ ] `app/src/main/res/` (tous les layouts et ressources)
- [ ] `app/src/main/AndroidManifest.xml`
- [ ] `app/build.gradle.kts`
- [ ] `build.gradle.kts` (racine)
- [ ] `settings.gradle.kts`
- [ ] `gradle.properties`
- [ ] `gradlew` et `gradlew.bat`

### Fichiers à Ajouter Après Import
- [ ] `app/google-services.json` (téléchargé depuis Firebase)
- [ ] `local.properties` (généré automatiquement)

### Fichiers Optionnels (seront régénérés)
- `.gradle/` (peut être exclu)
- `.idea/` (peut être exclu)
- `build/` (peut être exclu)
- `app/build/` (peut être exclu)

---

## ⚠️ Problèmes Courants et Solutions Rapides

| Problème | Solution Rapide |
|----------|----------------|
| SDK not found | File → Project Structure → SDK Location |
| google-services.json missing | Télécharger depuis Firebase Console |
| Gradle sync failed | File → Invalidate Caches / Restart |
| JAVA_HOME error | Vérifier JDK dans Project Structure |
| Build failed | Build → Clean Project puis Rebuild |
| App crashes on start | Vérifier Logcat pour les erreurs |
| Firebase connection failed | Vérifier google-services.json et Internet |

---

## 📞 Support

Si vous rencontrez des problèmes non résolus :

1. Consulter `GUIDE_DEPLOIEMENT.md` pour les détails
2. Vérifier les logs dans Logcat (View → Tool Windows → Logcat)
3. Vérifier Firebase Console que le projet est actif
4. Vérifier la documentation Android Studio officielle

---

**Dernière mise à jour** : 2024

