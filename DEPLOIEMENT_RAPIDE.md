# Guide Rapide de Déploiement - TimeFlow

## 🚀 Déploiement en 5 Étapes

### 1️⃣ Exporter le Projet
```
Sur le PC source :
- Copier le dossier : C:\Users\syrine\AndroidStudioProjects\TimeFlow
- Créer une archive ZIP (optionnel)
```

### 2️⃣ Transférer
```
- Copier sur USB, Cloud (Google Drive, Dropbox), ou Email
- Transférer sur le nouveau PC
- Extraire dans : C:\Users\[Nom]\AndroidStudioProjects\TimeFlow
```

### 3️⃣ Ouvrir dans Android Studio
```
- Lancer Android Studio
- File → Open
- Sélectionner le dossier TimeFlow
- Attendre la synchronisation Gradle (5-15 min)
```

### 4️⃣ Configurer Firebase
```
1. Aller sur : https://console.firebase.google.com/
2. Sélectionner le projet TimeFlow
3. Paramètres ⚙️ → Vos applications → Android
4. Télécharger google-services.json
5. Placer dans : app/google-services.json
```

### 5️⃣ Compiler et Lancer
```
1. File → Sync Project with Gradle Files
2. Build → Rebuild Project
3. Run → Run 'app'
```

---

## ⚡ Commandes Rapides

```bash
# Dans le dossier du projet
.\gradlew.bat clean          # Nettoyer
.\gradlew.bat assembleDebug  # Compiler
.\gradlew.bat installDebug   # Installer
```

---

## ✅ Vérifications Essentielles

- [ ] `google-services.json` dans `app/`
- [ ] Synchronisation Gradle réussie
- [ ] Pas d'erreurs de compilation
- [ ] Application s'installe et démarre
- [ ] Connexion Firebase fonctionne

---

## 🔧 Problèmes Fréquents

| Erreur | Solution |
|--------|----------|
| SDK not found | Project Structure → SDK Location |
| google-services.json missing | Télécharger depuis Firebase |
| Gradle sync failed | Invalidate Caches / Restart |
| Build failed | Clean Project → Rebuild |

---

**Pour plus de détails** : Consultez `GUIDE_DEPLOIEMENT.md`

