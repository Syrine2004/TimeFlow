# Instructions pour Générer les PDFs

## Option 1 : Utiliser un convertisseur en ligne

1. Ouvrez les fichiers `DOCUMENTATION.md` et `GUIDE_CODE.md`
2. Copiez le contenu
3. Utilisez un convertisseur Markdown vers PDF en ligne :
   - https://www.markdowntopdf.com/
   - https://dillinger.io/ (Export → PDF)
   - https://stackedit.io/ (Export → PDF)

## Option 2 : Utiliser Pandoc (si installé)

```bash
pandoc DOCUMENTATION.md -o DOCUMENTATION.pdf
pandoc GUIDE_CODE.md -o GUIDE_CODE.pdf
```

## Option 3 : Utiliser VS Code

1. Installez l'extension "Markdown PDF" dans VS Code
2. Ouvrez `DOCUMENTATION.md` ou `GUIDE_CODE.md`
3. Clic droit → "Markdown PDF: Export (pdf)"

## Option 4 : Utiliser un éditeur Markdown

- Typora : File → Export → PDF
- Mark Text : File → Export → PDF
- Obsidian : File → Export → PDF

---

**Note** : Les fichiers Markdown sont déjà bien formatés et prêts pour la conversion en PDF.

