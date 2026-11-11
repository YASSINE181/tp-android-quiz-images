# TP Android — Quiz en Images (Kotlin & XML)

Application Android de quiz illustré développée en **Kotlin** avec une interface utilisateur déclarative en **XML**, conformément aux exigences du TP.

---

## 📋 Table des matières
1. [Présentation du projet](#-présentation-du-projet)
2. [Contraintes et objectifs respectés](#-contraintes-et-objectifs-respectés)
3. [Architecture du projet](#-architecture-du-projet)
4. [Comment ouvrir et exécuter l'application](#-comment-ouvrir-et-exécuter-lapplication)
5. [Scénarios de test et captures d'écran](#-scénarios-de-test-et-captures-décran)
6. [Compte-rendu détaillé](#-compte-rendu-détaillé)

---

## 🎯 Présentation du projet
L'application propose un quiz composé de **cinq questions illustrées** sur le thème des **formes géométriques** (triangle, cercle, carré, étoile, rectangle).
Pour chaque question :
- Une illustration vectorielle nette et sans déformation est présentée.
- Trois boutons de réponse distincts sont proposés, avec une alternance de l'emplacement de la bonne réponse.
- L'utilisateur sélectionne son choix, qui est validé immédiatement :
  - **Bonne réponse :** +1 point au score, progression +1, affichage d'un message positif en vert.
  - **Mauvaise réponse :** score inchangé, progression +1, affichage de la correction.
  - Les 3 boutons sont désactivés pour empêcher les clics multiples ou l'attribution de points supplémentaires.
- Un bouton de navigation permet de passer à la question suivante, ou d'afficher le résultat final sur la 5ᵉ question.
- L'écran final présente le score final sur 5, le pourcentage de réussite, un message personnalisé et un bouton **Rejouer** réinitialisant la partie.

---

## ✅ Contraintes et objectifs respectés

| Exigence du TP | Statut | Détails de réalisation |
| :--- | :---: | :--- |
| **Langage Kotlin** | ✅ | Toute la logique applicative est écrite en Kotlin (`MainActivity.kt`, `Question.kt`). |
| **Activité unique (`MainActivity`)** | ✅ | Une seule activité, sans aucun fragment ni changement d'activité. |
| **Interface XML pure** | ✅ | Interface définie dans `res/layout/activity_main.xml` (pas de Jetpack Compose). |
| **Tableau de 5 questions statiques** | ✅ | Structure `data class Question` déclarée et instanciée dans une liste Kotlin. |
| **Images dans `res/drawable`** | ✅ | Drawables vectoriels XML haute définition (`triangle.xml`, `cercle.xml`, `carre.xml`, `etoile.xml`, `rectangle.xml`). |
| **Exactement 3 boutons de choix** | ✅ | `btn_choix1`, `btn_choix2`, `btn_choix3`. |
| **Score visible en haut à droite** | ✅ | `txt_score` fixé dans l'en-tête, toujours visible. |
| **ProgressBar horizontale** | ✅ | `progress_quiz` de 0 à 5 mesurant le nombre de questions répondues. |
| **Défilement pour petits écrans** | ✅ | `ScrollView` englobant le contenu sous le bandeau fixe d'en-tête. |
| **Affichage du résultat final** | ✅ | Dans la même activité, choix masqués et score final avec bouton **Rejouer**. |
| **Conservation d'état (Rotation)** | ✅ | Implémenté avec `onSaveInstanceState` et restauration dans `onCreate`. |
| **Autonome (hors ligne)** | ✅ | Aucune base de données, ni serveur, ni téléchargement réseau requis. |

---

## 🏗 Architecture du projet

```
tp3Créer un quiz en images/
├── app/
│   ├── build.gradle                          # Configuration Gradle du module Android
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml               # Déclaration de MainActivity comme lanceur
│       ├── java/com/example/quizimages/
│       │   ├── Question.kt                   # Modèle de données data class Question
│       │   └── MainActivity.kt               # Activité unique et logique complète du quiz
│       └── res/
│           ├── drawable/                     # Formes vectorielles et styles graphiques
│           │   ├── triangle.xml
│           │   ├── cercle.xml
│           │   ├── carre.xml
│           │   ├── etoile.xml
│           │   ├── rectangle.xml
│           │   ├── bg_card.xml
│           │   ├── bg_header.xml
│           │   ├── bg_choice_btn.xml
│           │   ├── bg_choice_btn_correct.xml
│           │   └── bg_choice_btn_incorrect.xml
│           ├── layout/
│           │   └── activity_main.xml         # Maquette XML complète (en-tête fixe + ScrollView)
│           └── values/
│               ├── colors.xml                # Palette de couleurs
│               ├── strings.xml               # Textes et gabarits de chaînes
│               └── themes.xml                # Thème Material
├── captures/                                 # Dossier des captures d'écran obligatoires
│   ├── 01_question_initiale.svg
│   ├── 02_bonne_reponse.svg
│   ├── 03_mauvaise_reponse.svg
│   ├── 04_resultat_final.svg
│   └── README.md
├── compte-rendu/                             # Dossier du compte-rendu de TP
│   ├── COMPTE_RENDU.md                       # Rapport complet au format Markdown
│   └── COMPTE_RENDU.html                     # Version HTML prête pour impression / PDF
├── build.gradle                              # Fichier Gradle racine
├── settings.gradle                           # Déclaration du projet et du module :app
├── gradle.properties
├── gradlew & gradlew.bat                     # Wrapper Gradle
└── .gitignore                                # Exclusion de build/, .gradle/, local.properties
```

---

## 🚀 Comment ouvrir et exécuter l'application

### Prérequis
- **Android Studio** (Hedgehog, Iguana, Jellyfish ou version plus récente).
- **JDK 17 ou supérieur** (intégré par défaut dans Android Studio).
- Un appareil Android physique (Android 7.0 / API 24 minimum) avec débogage USB activé ou un émulateur Android (AVD).

### Étapes
1. Cloner ou télécharger le dépôt :
   ```bash
   git clone <URL_DU_DEPOT_GITHUB>
   ```
2. Ouvrir **Android Studio**.
3. Cliquer sur **File > Open...** et sélectionner le dossier racine du projet.
4. Laisser Gradle synchroniser les dépendances (Sync Project with Gradle Files).
5. Sélectionner votre émulateur ou appareil connecté dans le menu des cibles en haut.
6. Cliquer sur le bouton vert **Run ▶** (ou raccourci `Shift + F10`).

---

## 📸 Scénarios de test et captures d'écran

Toutes les captures obligatoires sont disponibles dans le dossier [`captures/`](captures/) :

| Situation | Description | Aperçu |
| :--- | :--- | :---: |
| **1. Lancement** | Première question (triangle), 3 choix, score `0 / 5`, progression `0 / 5`. | [Voir capture](captures/01_question_initiale.svg) |
| **2. Bonne réponse** | Choix correct validé : score incrémenté à `1 / 5`, barre avancée à 1, choix désactivés, feedback vert. | [Voir capture](captures/02_bonne_reponse.svg) |
| **3. Mauvaise réponse** | Choix incorrect sélectionné : score inchangé à `1 / 5`, progression avancée à `2 / 5`, correction rouge. | [Voir capture](captures/03_mauvaise_reponse.svg) |
| **4. Fin du jeu** | Progression à `5 / 5`, score final affiché, choix masqués, bouton **Rejouer** actif. | [Voir capture](captures/04_resultat_final.svg) |

---

## 📄 Compte-rendu détaillé
Le compte-rendu complet du travail réalisé, répondant aux 5 questions théoriques de l'énoncé et détaillant les tests effectués, est disponible dans le dossier [`compte-rendu/`](compte-rendu/) :
- [Rapport Markdown](compte-rendu/COMPTE_RENDU.md)
- [Rapport HTML stylé (compatible impression PDF)](compte-rendu/COMPTE_RENDU.html)
