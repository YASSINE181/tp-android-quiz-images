# Compte Rendu de Travaux Pratiques
## TP Android : Créer un Quiz en Images

**Matière :** Développement d'Applications Mobiles Android  
**Technologie :** Kotlin & Interface déclarative XML  
**Thème retenu :** Les Formes Géométriques Régulières  
**Auteur(s) :** Ghada  
**Date :** Novembre 2026  

---

## 1. Introduction et Choix du Thème

Le présent travail pratique consiste à concevoir et développer une application Android complète de quiz illustré composée de **cinq questions**. L'utilisateur observe une image pour chaque question et doit choisir la bonne réponse parmi trois propositions. L'application calcule et affiche le score en temps réel, indique la progression dans le quiz et propose un écran récapitulatif à la fin avec la possibilité de rejouer.

### Thème retenu : Les Formes Géométriques
Le thème choisi est celui des **formes géométriques fondamentales**. Ce thème permet de concevoir des questions nécessitant une observation attentive des caractéristiques visuelles (nombre de côtés, d'angles, de sommets ou de branches) :
1. **Triangle équilatéral** : observation du nombre de côtés et sommets (`res/drawable/triangle.xml`).
2. **Cercle** : observation de l'absence totale de sommet et d'angle (`res/drawable/cercle.xml`).
3. **Carré** : identification des quatre angles droits et quatre côtés de même longueur (`res/drawable/carre.xml`).
4. **Étoile régulière à 5 branches** : comptage précis des branches pointues (`res/drawable/etoile.xml`).
5. **Rectangle** : reconnaissance de la forme aux côtés opposés parallèles et égaux 2 à 2 (`res/drawable/rectangle.xml`).

Les illustrations ont été réalisées sous forme de **drawables vectoriels XML** haute définition. Ce format garantit un rendu net et fidèle sur toutes les densités d'écran (hdpi, xhdpi, xxhdpi, xxxhdpi) sans aucune pixellisation ni distorsion, tout en conservant une empreinte mémoire minimale et une autonomie complète sans téléchargement externe.

---

## 2. Présentation de l'Interface et des Widgets Utilisés

L'interface graphique a été conçue exclusivement dans le fichier de layout XML `res/layout/activity_main.xml`, conformément à la contrainte d'éviter Jetpack Compose et les fragments.

```
+-----------------------------------------------------------+
| [Titre: Quiz en Images]                  [Score : 0 / 5]  |  <- Bandeau Fixe
| [==================== Barre de progression =============] |
| Progression : 0 / 5 questions répondues                   |
+-----------------------------------------------------------+
| ~ ~ ~ ~ ~ ~ ~ ~ ~ Zone Scrollable (ScrollView) ~ ~ ~ ~ ~  |
|                                                           |
| Question 1 sur 5                                          |
| +-------------------------------------------------------+ |
| |                                                       | |
| |                    [ Image ]                          | |  <- CardView /
| |                                                       | |     ImageView
| +-------------------------------------------------------+ |
|                                                           |
| Combien de côtés possède cette figure géométrique ?       |  <- TextView
|                                                           |
| [  A. 4 côtés                                         ]   |  <- Button (btn_choix1)
| [  B. 3 côtés                                         ]   |  <- Button (btn_choix2)
| [  C. 5 côtés                                         ]   |  <- Button (btn_choix3)
|                                                           |
| [ ✓ Bonne réponse ! Bravo ! (+1 point)                ]   |  <- Feedback (txt_feedback)
|                                                           |
| [              Question suivante →                    ]   |  <- Button (btn_suivant)
|                                                           |
+-----------------------------------------------------------+
```

### Détail des widgets et identifiants utilisés :
- **`layout_header` (`LinearLayout`)** : bandeau d'en-tête supérieur fixé en haut de l'écran, avec une ombre portée (`elevation="4dp"`).
  - **`txt_score` (`TextView`)** : étiquette stylisée en haut à droite affichant le score en temps réel (`Score : X / 5`). Elle reste visible en permanence, même lorsque l'utilisateur fait défiler la page.
  - **`progress_quiz` (`ProgressBar`)** : barre horizontale de style `?android:attr/progressBarStyleHorizontal` avec `max="5"` et `progress="0"`, indiquant le nombre de questions répondues.
  - **`txt_progression_detail` (`TextView`)** : libellé textuel explicitant l'avancement (`Progression : X / 5 questions répondues`).
- **`scroll_view_content` (`ScrollView`)** : permet un défilement vertical fluide sur les petits écrans (`fillViewport="true"`), assurant que l'ensemble des boutons de choix et boutons d'action demeurent accessibles sans risque de troncature.
- **`layout_question_container` (`LinearLayout`)** : conteneur de la phase de questionnement.
  - **`txt_numero_question` (`TextView`)** : indique l'avancement textuel (ex : `Question 2 sur 5`).
  - **`img_question` (`ImageView`)** : affiche la forme géométrique courante avec `adjustViewBounds="true"` et `scaleType="fitCenter"` pour éviter toute déformation d'image.
  - **`txt_question` (`TextView`)** : intitulé de la question posée, avec une taille de police lisible (17sp) et une graisse accentuée.
  - **`btn_choix1`, `btn_choix2`, `btn_choix3` (`Button`)** : boutons distincts présentant les trois options de réponses possibles.
  - **`txt_feedback` (`TextView`)** : bandeau d'information affiché uniquement après validation, indiquant le verdict par un texte lisible et un code couleur accessible.
  - **`btn_suivant` (`Button`)** : bouton d'action apparaissant après avoir répondu, libellé `Question suivante` pour les questions 1 à 4, et `Voir le résultat` à la question 5.
- **`layout_resultat_container` (`LinearLayout`)** : conteneur final (initialement masqué `View.GONE`), affiché au terme du quiz dans la même activité.
  - **`txt_score_final` (`TextView`)** : présente le score final récapitulatif.
  - **`txt_pourcentage` (`TextView`)** : pourcentage de réussite associé.
  - **`txt_appreciation` (`TextView`)** : message personnalisé d'encouragement ou de félicitations.
  - **`btn_rejouer` (`Button`)** : bouton permettant de relancer une nouvelle partie à zéro.

---

## 3. Structure du Modèle et Association des Questions

La structure des questions est modélisée par une **data class Kotlin** dans le fichier `Question.kt` :

```kotlin
data class Question(
    val texte: String,
    val imageResId: Int,
    val choix: List<String>,
    val bonneReponseIndex: Int,
    val descriptionImage: String
)
```

Dans `MainActivity.kt`, une collection statique immuable de 5 questions est déclarée :

```kotlin
private val questions: List<Question> = listOf(
    Question(
        texte = "Combien de côtés possède cette figure géométrique ?",
        imageResId = R.drawable.triangle,
        choix = listOf("4 côtés", "3 côtés", "5 côtés"),
        bonneReponseIndex = 1, // Bouton 2
        descriptionImage = "Un triangle rouge équilatéral à trois côtés"
    ),
    Question(
        texte = "Quelle est la particularité remarquable de cette figure ?",
        imageResId = R.drawable.cercle,
        choix = listOf(
            "Elle n'a aucun sommet ni angle",
            "Elle possède quatre angles droits",
            "Elle a trois côtés égaux"
        ),
        bonneReponseIndex = 0, // Bouton 1
        descriptionImage = "Un cercle bleu sans aucun sommet ni angle"
    ),
    Question(
        texte = "Cette figure possède 4 côtés égaux et 4 angles droits : de quoi s'agit-il ?",
        imageResId = R.drawable.carre,
        choix = listOf("Un losange quelconque", "Un rectangle allongé", "Un carré"),
        bonneReponseIndex = 2, // Bouton 3
        descriptionImage = "Un carré vert régulier avec quatre côtés égaux"
    ),
    Question(
        texte = "Combien de branches pointues possède cette étoile régulière ?",
        imageResId = R.drawable.etoile,
        choix = listOf("6 branches", "5 branches", "8 branches"),
        bonneReponseIndex = 1, // Bouton 2
        descriptionImage = "Une étoile dorée régulière à cinq branches"
    ),
    Question(
        texte = "Cette figure a des côtés opposés parallèles et égaux 2 à 2 avec 4 angles droits :",
        imageResId = R.drawable.rectangle,
        choix = listOf("Un rectangle", "Un trapèze quelconque", "Un hexagone"),
        bonneReponseIndex = 0, // Bouton 1
        descriptionImage = "Un rectangle violet avec des côtés opposés de même longueur"
    )
)
```

### Points remarquables :
- **Alternance de la bonne réponse** : les indices de bonne réponse sont successivement `1`, `0`, `2`, `1`, `0`. Ainsi, la bonne réponse n'est jamais systématiquement au même emplacement.
- **Accessibilité (`contentDescription`)** : le champ `descriptionImage` est réinjecté dynamiquement sur l'`ImageView` à chaque question (`imgQuestion.contentDescription = questionCourante.descriptionImage`), assurant la compatibilité avec les lecteurs d'écran (TalkBack).

---

## 4. Gestion des Événements, Calcul du Score et Progression

### 4.1. Validation immédiate lors du clic
Dès que l'utilisateur clique sur l'un des trois boutons de choix, la méthode `validerReponse(indexChoisi: Int)` est déclenchée :
1. **Protection contre le multiclic** :
   ```kotlin
   if (reponseValidee || partieTerminee) return
   reponseValidee = true
   ```
   Tous les boutons de choix sont instantanément désactivés (`bouton.isEnabled = false`).
2. **Calcul du score et de la progression** :
   - Si `indexChoisi == questionCourante.bonneReponseIndex` :
     - `score` est incrémenté de 1.
     - Le bouton cliqué reçoit un style vert avec préfixe `✓`.
     - Le message de retour affiche : `✓ Bonne réponse ! Bravo ! (+1 point)`.
   - Si `indexChoisi != questionCourante.bonneReponseIndex` :
     - `score` reste rigoureusement inchangé.
     - Le bouton cliqué reçoit un style rouge avec préfixe `✗`.
     - Le bouton de la bonne réponse est coloré en vert pour indiquer la réponse exacte.
     - Le message de retour affiche l'explication et rappelle la bonne réponse.
   - **Dans les deux cas**, la progression avance d'une unité : `val nbReponsesDonnees = indiceCourant + 1`, répercuté sur la barre (`progressQuiz.progress = nbReponsesDonnees`).
3. **Mise à disposition de la navigation** :
   Le bouton `btnSuivant` devient visible. Si `indiceCourant == questions.size - 1`, son texte est configuré à `"Voir le résultat"`, sinon `"Question suivante"`.

### 4.2. Passage à la question suivante et fin de partie
- Au clic sur `btnSuivant`, si l'on n'est pas à la dernière question :
  - `indiceCourant` est incrémenté.
  - Les variables de contrôle sont réinitialisées (`reponseValidee = false`, `reponseChoisieIndex = -1`).
  - La méthode `afficherQuestion()` est appelée pour recharger les widgets et réactiver les 3 boutons.
- Si l'on était à la 5ᵉ question :
  - La méthode `afficherResultatFinal()` masque `layout_question_container` et affiche `layout_resultat_container`.
  - La progression finale est fixée à `5 / 5`.
  - Le score final, le pourcentage et l'appréciation sont calculés et affichés.

### 4.3. Réinitialisation complète (Rejouer)
Au clic sur `btnRejouer` :
- `indiceCourant`, `score`, `reponseChoisieIndex` sont réinitialisés à 0 et -1.
- `partieTerminee` et `reponseValidee` repassent à `false`.
- La vue de résultat est masquée et la première question est réaffichée.

### 4.4. Gestion de la rotation d'écran (`onSaveInstanceState`)
Pour garantir une expérience utilisateur irréprochable sans perte de données lors de la rotation de l'appareil (portrait ↔ paysage), les données clés sont sauvegardées dans le `Bundle` :
```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(KEY_INDICE, indiceCourant)
    outState.putInt(KEY_SCORE, score)
    outState.putBoolean(KEY_VALIDEE, reponseValidee)
    outState.putInt(KEY_CHOIX_INDEX, reponseChoisieIndex)
    outState.putBoolean(KEY_TERMINEE, partieTerminee)
}
```
Dans `onCreate(savedInstanceState: Bundle?)`, ces informations sont restaurées, permettant de retrouver immédiatement la question exacte, l'état validé ou l'écran final sans réinitialisation involontaire.

---

## 5. Réponses aux Questions Posées dans l'Énoncé

### Question 1 : Comment représentez-vous une question et sa bonne réponse ?
> **Réponse :**  
> Une question est modélisée par une classe de données Kotlin (`data class Question`) contenant :
> - Le texte de la question (`String`),
> - L'identifiant de la ressource drawable (`Int`),
> - La liste des 3 choix possibles (`List<String>`),
> - L'indice de la bonne réponse (`bonneReponseIndex: Int`), correspondant à l'index `0`, `1` ou `2` dans la liste des choix,
> - La description de l'image (`descriptionImage: String`) pour l'accessibilité.
>
> La bonne réponse est ainsi représentée par son indice numérique dans la liste, ce qui permet une comparaison directe et immédiate avec l'indice du bouton cliqué par l'utilisateur (`indexChoisi == questionCourante.bonneReponseIndex`).

### Question 2 : Comment le clic sur un bouton permet-il de savoir quel choix a été sélectionné ?
> **Réponse :**  
> Dans la méthode `configurerEcouteurs()`, chaque bouton de choix se voit assigner un écouteur de clic (`OnClickListener`) qui transmet son indice respectif en paramètre à la fonction commune `validerReponse(index: Int)` :
> - `btnChoix1.setOnClickListener { validerReponse(0) }`
> - `btnChoix2.setOnClickListener { validerReponse(1) }`
> - `btnChoix3.setOnClickListener { validerReponse(2) }`
>
> Ce paramètre entier identifie sans ambiguïté le choix retenu, qui est ensuite stocké dans la variable d'état `reponseChoisieIndex`.

### Question 3 : Comment empêchez-vous l'attribution de plusieurs points pour une question ?
> **Réponse :**  
> Deux mécanismes complémentaires garantissent qu'une question ne rapporte qu'un seul point :
> 1. **Désactivation matérielle des vues** : dès qu'un clic est détecté, la boucle `for (bouton in boutonsChoix) { bouton.isEnabled = false }` désactive immédiatement les trois boutons de choix, rendant tout clic physique supplémentaire inopérant.
> 2. **Verrou logique au niveau du code** : la variable booléenne `reponseValidee` est passée à `true`. Au début de `validerReponse`, une clause de garde vérifie `if (reponseValidee || partieTerminee) return`. Tout appel ultérieur est immédiatement ignoré.

### Question 4 : Quelle différence faites-vous entre le score et la progression ?
> **Réponse :**  
> - **Le score** mesure la **performance** de l'utilisateur : il ne comptabilise que les **bonnes réponses** données. Il commence à 0 et n'augmente d'une unité (`score++`) que si l'utilisateur a choisi la réponse correcte. En cas d'erreur, il reste strictement invariant.
> - **La progression** mesure l'**avancement temporel** dans le quiz : elle comptabilise le nombre total de questions auxquelles l'utilisateur a répondu (bonnes ou mauvaises). Elle commence à 0 et augmente systématiquement de 1 après chaque validation (`nbReponsesDonnees = indiceCourant + 1`), atteignant 5 sur 5 dès que la cinquième question est validée.

### Question 5 : Comment détectez-vous la dernière question et affichez-vous le résultat ?
> **Réponse :**  
> La détection s'effectue en comparant l'indice courant avec la taille du tableau de questions : `indiceCourant == questions.size - 1` (soit l'indice 4 pour un quiz de 5 éléments).
> - Lors de la validation de la réponse à cette 5ᵉ question, le bouton d'action affiche le texte `"Voir le résultat"` au lieu de `"Question suivante"`.
> - Au clic sur ce bouton, `passerQuestionSuivante()` détecte que la limite est atteinte (`indiceCourant >= questions.size - 1`) et appelle `afficherResultatFinal()`.
> - Cette méthode passe `layoutQuestionContainer.visibility = View.GONE`, passe `layoutResultatContainer.visibility = View.VISIBLE`, calcule les pourcentages et affiche le bouton `Rejouer`, le tout dans la même activité sans fragment ni redirection.

---

## 6. Scénarios de Test et Résultats Obtenus

Conformément à la grille de vérification de l'énoncé (page 4), les tests suivants ont été exécutés et validés :

| N° | Scénario de test | Résultat attendu | Résultat obtenu | Verdict |
| :-: | :--- | :--- | :--- | :-: |
| **1** | Lancer une nouvelle partie | 1ère question affichée, 3 choix actifs, score `0 / 5`, progression `0 / 5`. | Conforme | **OK** |
| **2** | Donner une bonne réponse | Score passe à `1 / 5`, progression passe à `1 / 5`, feedback vert. | Conforme | **OK** |
| **3** | Donner une mauvaise réponse | Score reste inchangé (`1 / 5`), progression passe à `2 / 5`, feedback rouge. | Conforme | **OK** |
| **4** | Appuyer plusieurs fois sur un choix | Aucun point additionnel, boutons désactivés dès le premier appui. | Conforme | **OK** |
| **5** | Passer à la question suivante | Image, énoncé et 3 choix mis à jour ; score conservé ; boutons réactivés. | Conforme | **OK** |
| **6** | Répondre juste aux 5 questions | Score final `5 / 5`, progression `5 / 5`, message d'excellence. | Conforme | **OK** |
| **7** | Répondre faux aux 5 questions | Score final `0 / 5`, progression `5 / 5`, message d'encouragement. | Conforme | **OK** |
| **8** | Mélanger réponses justes et fausses | Score final exactement égal au nombre de réussites (ex : 3/5 ou 4/5). | Conforme | **OK** |
| **9** | Utiliser le bouton Rejouer | Retour à la question 1, score et progression réinitialisés à 0. | Conforme | **OK** |
| **10** | Test sur écran de petite taille | Défilement fluide via `ScrollView`, le score reste visible en haut. | Conforme | **OK** |
| **11** | Rotation d'écran (Bonus) | Restauration parfaite de la question, du score et de l'état répondu. | Conforme | **OK** |

---

## 7. Difficultés Rencontrées et Solutions Apportées

1. **Conservation de l'état lors de la rotation de l'appareil :**  
   *Problème :* Par défaut, la rotation recrée l'activité Android (`onDestroy` puis `onCreate`), ce qui réinitialisait le quiz à la question 1 avec un score à 0.  
   *Solution :* Sauvegarde explicite des variables (`indiceCourant`, `score`, `reponseValidee`, `reponseChoisieIndex`, `partieTerminee`) dans `onSaveInstanceState` et restauration ordonnée dans `onCreate`.
2. **Accessibilité et lisibilité sans dépendre uniquement de la couleur :**  
   *Problème :* L'énoncé stipule que la couleur ne doit pas être le seul vecteur d'information entre une bonne et une mauvaise réponse.  
   *Solution :* Utilisation systématique de symboles textuels univoques (`✓` pour une réponse juste, `✗` pour une réponse fausse) ainsi que d'une explication textuelle explicite dans le bandeau de feedback.
3. **Maintien du score visible lors du défilement :**  
   *Problème :* Sur un écran compact, faire défiler la question pouvait masquer le score en haut.  
   *Solution :* Découpage du layout en deux blocs : un en-tête horizontal fixe avec élévation et ombrage, et un `ScrollView` occupant l'espace restant (`layout_weight="1"`).

---

## 8. Conclusion

L'ensemble des objectifs pédagogiques et des contraintes du TP a été scrupuleusement respecté :
- Une application fluide, intuitive et robuste développée en Kotlin.
- Une ergonomie soignée avec des formes vectorielles nettes et élégantes.
- Un respect strict de la séparation entre logique de progression et calcul du score.
- Un dépôt structuré comprenant le code source, le Gradle Wrapper, les captures d'écran et le présent compte-rendu exhaustif.
