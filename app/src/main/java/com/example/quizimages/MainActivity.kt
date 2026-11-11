package com.example.quizimages

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Activité principale du Quiz en Images.
 * Gère le cycle de vie du quiz, l'affichage des 5 questions,
 * la validation des réponses, la mise à jour du score et de la progression,
 * ainsi que la restauration de l'état lors de la rotation d'écran.
 */
class MainActivity : AppCompatActivity() {

    // ==========================================
    // WIDGETS DE L'INTERFACE (déclarés en XML)
    // ==========================================
    private lateinit var txtScore: TextView
    private lateinit var progressQuiz: ProgressBar
    private lateinit var txtProgressionDetail: TextView

    private lateinit var layoutQuestionContainer: LinearLayout
    private lateinit var txtNumeroQuestion: TextView
    private lateinit var imgQuestion: ImageView
    private lateinit var txtQuestion: TextView
    private lateinit var btnChoix1: Button
    private lateinit var btnChoix2: Button
    private lateinit var btnChoix3: Button
    private lateinit var txtFeedback: TextView
    private lateinit var btnSuivant: Button

    private lateinit var layoutResultatContainer: LinearLayout
    private lateinit var txtScoreFinal: TextView
    private lateinit var txtPourcentage: TextView
    private lateinit var txtAppreciation: TextView
    private lateinit var btnRejouer: Button

    // ==========================================
    // TABLEAU STATIQUE DE 5 QUESTIONS EN KOTLIN
    // ==========================================
    private val questions: List<Question> = listOf(
        Question(
            texte = "Combien de côtés possède cette figure géométrique ?",
            imageResId = R.drawable.triangle,
            choix = listOf("4 côtés", "3 côtés", "5 côtés"),
            bonneReponseIndex = 1,
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
            bonneReponseIndex = 0,
            descriptionImage = "Un cercle bleu sans aucun sommet ni angle"
        ),
        Question(
            texte = "Cette figure possède 4 côtés égaux et 4 angles droits : de quoi s'agit-il ?",
            imageResId = R.drawable.carre,
            choix = listOf("Un losange quelconque", "Un rectangle allongé", "Un carré"),
            bonneReponseIndex = 2,
            descriptionImage = "Un carré vert régulier avec quatre côtés égaux"
        ),
        Question(
            texte = "Combien de branches pointues possède cette étoile régulière ?",
            imageResId = R.drawable.etoile,
            choix = listOf("6 branches", "5 branches", "8 branches"),
            bonneReponseIndex = 1,
            descriptionImage = "Une étoile dorée régulière à cinq branches"
        ),
        Question(
            texte = "Cette figure a des côtés opposés parallèles et égaux 2 à 2 avec 4 angles droits :",
            imageResId = R.drawable.rectangle,
            choix = listOf("Un rectangle", "Un trapèze quelconque", "Un hexagone"),
            bonneReponseIndex = 0,
            descriptionImage = "Un rectangle violet avec des côtés opposés de même longueur"
        )
    )

    // ==========================================
    // VARIABLES D'ÉTAT DU JEU
    // ==========================================
    private var indiceCourant: Int = 0
    private var score: Int = 0
    private var reponseValidee: Boolean = false
    private var reponseChoisieIndex: Int = -1
    private var partieTerminee: Boolean = false

    // Clés pour la sauvegarde lors de la rotation d'écran
    companion object {
        private const val KEY_INDICE = "CLE_INDICE_COURANT"
        private const val KEY_SCORE = "CLE_SCORE"
        private const val KEY_VALIDEE = "CLE_REPONSE_VALIDEE"
        private const val KEY_CHOIX_INDEX = "CLE_CHOIX_INDEX"
        private const val KEY_TERMINEE = "CLE_PARTIE_TERMINEE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialiserVues()
        configurerEcouteurs()

        // Restauration d'état après rotation (Amélioration facultative)
        if (savedInstanceState != null) {
            indiceCourant = savedInstanceState.getInt(KEY_INDICE, 0)
            score = savedInstanceState.getInt(KEY_SCORE, 0)
            reponseValidee = savedInstanceState.getBoolean(KEY_VALIDEE, false)
            reponseChoisieIndex = savedInstanceState.getInt(KEY_CHOIX_INDEX, -1)
            partieTerminee = savedInstanceState.getBoolean(KEY_TERMINEE, false)

            if (partieTerminee) {
                afficherResultatFinal()
            } else {
                afficherQuestion()
                if (reponseValidee && reponseChoisieIndex != -1) {
                    restaurerEtatReponse(reponseChoisieIndex)
                }
            }
        } else {
            afficherQuestion()
        }
    }

    /**
     * Liaison des variables avec les vues déclarées dans activity_main.xml.
     */
    private fun initialiserVues() {
        txtScore = findViewById(R.id.txt_score)
        progressQuiz = findViewById(R.id.progress_quiz)
        txtProgressionDetail = findViewById(R.id.txt_progression_detail)

        layoutQuestionContainer = findViewById(R.id.layout_question_container)
        txtNumeroQuestion = findViewById(R.id.txt_numero_question)
        imgQuestion = findViewById(R.id.img_question)
        txtQuestion = findViewById(R.id.txt_question)
        btnChoix1 = findViewById(R.id.btn_choix1)
        btnChoix2 = findViewById(R.id.btn_choix2)
        btnChoix3 = findViewById(R.id.btn_choix3)
        txtFeedback = findViewById(R.id.txt_feedback)
        btnSuivant = findViewById(R.id.btn_suivant)

        layoutResultatContainer = findViewById(R.id.layout_resultat_container)
        txtScoreFinal = findViewById(R.id.txt_score_final)
        txtPourcentage = findViewById(R.id.txt_pourcentage)
        txtAppreciation = findViewById(R.id.txt_appreciation)
        btnRejouer = findViewById(R.id.btn_rejouer)
    }

    /**
     * Association des gestionnaires de clic aux différents boutons.
     */
    private fun configurerEcouteurs() {
        btnChoix1.setOnClickListener { validerReponse(0) }
        btnChoix2.setOnClickListener { validerReponse(1) }
        btnChoix3.setOnClickListener { validerReponse(2) }

        btnSuivant.setOnClickListener { passerQuestionSuivante() }
        btnRejouer.setOnClickListener { reinitialiserQuiz() }
    }

    /**
     * Affiche la question courante et prépare les boutons de choix.
     */
    private fun afficherQuestion() {
        if (indiceCourant < 0 || indiceCourant >= questions.size) return

        val questionCourante = questions[indiceCourant]

        // Mise à jour de l'en-tête (score & progression)
        actualiserBandeau()

        // Affichage des informations de la question
        txtNumeroQuestion.text = getString(R.string.question_numero_format, indiceCourant + 1, questions.size)
        txtQuestion.text = questionCourante.texte
        imgQuestion.setImageResource(questionCourante.imageResId)
        imgQuestion.contentDescription = questionCourante.descriptionImage

        // Remplissage des trois choix
        val boutonsChoix = listOf(btnChoix1, btnChoix2, btnChoix3)
        for (i in boutonsChoix.indices) {
            val bouton = boutonsChoix[i]
            bouton.text = questionCourante.choix[i]
            bouton.isEnabled = true
            reinitialiserStyleBouton(bouton)
        }

        // Réinitialisation de la zone de retour et masquage du bouton suivant
        txtFeedback.visibility = View.GONE
        btnSuivant.visibility = View.GONE

        // Visibilité des conteneurs
        layoutQuestionContainer.visibility = View.VISIBLE
        layoutResultatContainer.visibility = View.GONE
    }

    /**
     * Valide immédiatement la réponse après le clic de l'utilisateur.
     * Désactive les trois boutons pour éviter des points multiples.
     */
    private fun validerReponse(indexChoisi: Int) {
        if (reponseValidee || partieTerminee) return

        reponseValidee = true
        reponseChoisieIndex = indexChoisi

        val questionCourante = questions[indiceCourant]
        val boutonsChoix = listOf(btnChoix1, btnChoix2, btnChoix3)

        // Désactiver immédiatement les trois boutons de choix
        for (bouton in boutonsChoix) {
            bouton.isEnabled = false
        }

        // Vérification de la réponse
        val estCorrect = (indexChoisi == questionCourante.bonneReponseIndex)

        if (estCorrect) {
            score++
            afficherFeedback(estCorrect = true, questionCourante.choix[questionCourante.bonneReponseIndex])
            marquerBoutonCorrect(boutonsChoix[indexChoisi])
        } else {
            afficherFeedback(estCorrect = false, questionCourante.choix[questionCourante.bonneReponseIndex])
            marquerBoutonIncorrect(boutonsChoix[indexChoisi])
            marquerBoutonCorrect(boutonsChoix[questionCourante.bonneReponseIndex])
        }

        // Actualisation du bandeau (le score augmente si correct, la progression avance dans tous les cas)
        actualiserBandeauApresReponse()

        // Affichage et configuration du bouton de navigation
        if (indiceCourant == questions.size - 1) {
            btnSuivant.text = getString(R.string.btn_voir_resultat)
        } else {
            btnSuivant.text = getString(R.string.btn_suivant)
        }
        btnSuivant.visibility = View.VISIBLE
    }

    /**
     * Restaure l'état visuel d'une question déjà répondue (utile lors d'une rotation d'écran).
     */
    private fun restaurerEtatReponse(indexChoisi: Int) {
        val questionCourante = questions[indiceCourant]
        val boutonsChoix = listOf(btnChoix1, btnChoix2, btnChoix3)

        for (bouton in boutonsChoix) {
            bouton.isEnabled = false
        }

        val estCorrect = (indexChoisi == questionCourante.bonneReponseIndex)
        if (estCorrect) {
            afficherFeedback(true, questionCourante.choix[questionCourante.bonneReponseIndex])
            marquerBoutonCorrect(boutonsChoix[indexChoisi])
        } else {
            afficherFeedback(false, questionCourante.choix[questionCourante.bonneReponseIndex])
            marquerBoutonIncorrect(boutonsChoix[indexChoisi])
            marquerBoutonCorrect(boutonsChoix[questionCourante.bonneReponseIndex])
        }

        actualiserBandeauApresReponse()

        if (indiceCourant == questions.size - 1) {
            btnSuivant.text = getString(R.string.btn_voir_resultat)
        } else {
            btnSuivant.text = getString(R.string.btn_suivant)
        }
        btnSuivant.visibility = View.VISIBLE
    }

    /**
     * Affiche le message de correction lisible avec texte explicite et couleur.
     */
    private fun afficherFeedback(estCorrect: Boolean, bonneReponseTexte: String) {
        txtFeedback.visibility = View.VISIBLE
        if (estCorrect) {
            txtFeedback.text = getString(R.string.feedback_correct)
            txtFeedback.setBackgroundResource(R.drawable.bg_feedback_correct)
            txtFeedback.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
        } else {
            txtFeedback.text = getString(R.string.feedback_incorrect, bonneReponseTexte)
            txtFeedback.setBackgroundResource(R.drawable.bg_feedback_incorrect)
            txtFeedback.setTextColor(ContextCompat.getColor(this, R.color.incorrect_red))
        }
    }

    /**
     * Met à jour le bandeau supérieur avant qu'une réponse ne soit validée.
     */
    private fun actualiserBandeau() {
        txtScore.text = getString(R.string.score_format, score, questions.size)
        progressQuiz.progress = indiceCourant
        txtProgressionDetail.text = getString(R.string.progression_format, indiceCourant, questions.size)
    }

    /**
     * Met à jour le score et la barre de progression immédiatement après la validation.
     */
    private fun actualiserBandeauApresReponse() {
        txtScore.text = getString(R.string.score_format, score, questions.size)
        val nbReponsesDonnees = indiceCourant + 1
        progressQuiz.progress = nbReponsesDonnees
        txtProgressionDetail.text = getString(R.string.progression_format, nbReponsesDonnees, questions.size)
    }

    /**
     * Passe à la question suivante ou affiche l'écran de fin si le quiz est terminé.
     */
    private fun passerQuestionSuivante() {
        if (indiceCourant < questions.size - 1) {
            indiceCourant++
            reponseValidee = false
            reponseChoisieIndex = -1
            afficherQuestion()
        } else {
            afficherResultatFinal()
        }
    }

    /**
     * Présente le score final dans la même activité en masquant la zone de quiz.
     */
    private fun afficherResultatFinal() {
        partieTerminee = true
        layoutQuestionContainer.visibility = View.GONE
        layoutResultatContainer.visibility = View.VISIBLE

        // Progression fixée à 5/5
        progressQuiz.progress = questions.size
        txtProgressionDetail.text = getString(R.string.progression_format, questions.size, questions.size)
        txtScore.text = getString(R.string.score_format, score, questions.size)

        // Affichage des scores finaux
        txtScoreFinal.text = getString(R.string.score_final_format, score, questions.size)
        val pourcentage = (score * 100) / questions.size
        txtPourcentage.text = getString(R.string.pourcentage_format, pourcentage)

        // Message personnalisé selon la performance
        txtAppreciation.text = when (score) {
            5 -> getString(R.string.appreciation_parfait)
            4 -> getString(R.string.appreciation_bien)
            3 -> getString(R.string.appreciation_moyen)
            else -> getString(R.string.appreciation_reessayer)
        }
    }

    /**
     * Réinitialise intégralement le quiz pour débuter une nouvelle partie.
     */
    private fun reinitialiserQuiz() {
        indiceCourant = 0
        score = 0
        reponseValidee = false
        reponseChoisieIndex = -1
        partieTerminee = false
        afficherQuestion()
    }

    // ==========================================
    // STYLISATION DYNAMIQUE DES BOUTONS DE CHOIX
    // ==========================================
    private fun reinitialiserStyleBouton(bouton: Button) {
        bouton.setBackgroundResource(R.drawable.bg_choice_btn)
        bouton.backgroundTintList = null
        bouton.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
    }

    private fun marquerBoutonCorrect(bouton: Button) {
        bouton.setBackgroundResource(R.drawable.bg_choice_btn_correct)
        bouton.backgroundTintList = null
        bouton.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
    }

    private fun marquerBoutonIncorrect(bouton: Button) {
        bouton.setBackgroundResource(R.drawable.bg_choice_btn_incorrect)
        bouton.backgroundTintList = null
        bouton.setTextColor(ContextCompat.getColor(this, R.color.incorrect_red))
    }

    // =======================================================
    // GESTION DU CYCLE DE VIE ET ROTATION D'ÉCRAN (FACULTATIF)
    // =======================================================
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDICE, indiceCourant)
        outState.putInt(KEY_SCORE, score)
        outState.putBoolean(KEY_VALIDEE, reponseValidee)
        outState.putInt(KEY_CHOIX_INDEX, reponseChoisieIndex)
        outState.putBoolean(KEY_TERMINEE, partieTerminee)
    }
}
