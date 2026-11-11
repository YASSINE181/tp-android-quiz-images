package com.example.quizimages

/**
 * Modèle de données représentant une question du quiz illustré.
 *
 * @param texte Le texte de la question posée à l'utilisateur.
 * @param imageResId L'identifiant de la ressource drawable associée à la question.
 * @param choix La liste ou tableau contenant exactement 3 choix de réponses possibles.
 * @param bonneReponseIndex L'indice (0, 1 ou 2) correspondant à la bonne réponse dans la liste des choix.
 * @param descriptionImage Description textuelle pour l'accessibilité (contentDescription) de l'image.
 */
data class Question(
    val texte: String,
    val imageResId: Int,
    val choix: List<String>,
    val bonneReponseIndex: Int,
    val descriptionImage: String
)
