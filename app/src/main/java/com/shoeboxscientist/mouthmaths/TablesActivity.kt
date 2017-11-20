package com.shoeboxscientist.mouthmaths

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_tables.*;
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import android.media.MediaPlayer
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView


class TablesActivity : AppCompatActivity() {

    val USE_TEST_RECOGNIZER = true

    var recognizer: TestableSpeechRecognizer? = null
    var tablesEngine: TablesEngine? = null
    var correctVanishingAnimation: Animation? = null
    var wrongVanishingAnimation: Animation? = null

    // Delay between each question
    private val NEXT_QUESTION_DELAY = 1000L

    val questions = ArrayList<Question>()
    val correctAnswers = ArrayList<Question>()
    val wrongAnswers = ArrayList<Question>()

    var correctSound: MediaPlayer? = null
    var wrongSound: MediaPlayer? = null
    var nextRoundSound: MediaPlayer? = null

    var currentQuestion: Question? = null

    var roundNumber = 1
    var wrongAnswerCount = 0

    val sometimesRightFunction = {
        if (Math.random() > 0.5) {
            arrayListOf(currentQuestion!!.getAnswer().toString())
        } else {
            arrayListOf("")
        }
    }

    val answerTracker = MilestoneTracker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)

        questionView.text = ""
        spinnerView.visibility = View.GONE
        wrongView.visibility = View.GONE
        correctView.visibility = View.GONE

        correctSound = MediaPlayer.create(this, R.raw.correct)
        wrongSound = MediaPlayer.create(this, R.raw.wrong)
        nextRoundSound = MediaPlayer.create(this, R.raw.bell)
        correctVanishingAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.disappear)!!
        wrongVanishingAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.disappear)!!
    }

    override fun onStart() {
        super.onStart()
        tablesEngine = TablesEngine(
                howMany = intent.getIntExtra("howMany", 0),
                whichTables = intent.getIntegerArrayListExtra("whichTables"))

        // Set up all our lists of questions and answers
        questions.clear()
        correctAnswers.clear()
        wrongAnswers.clear()

        // Generate all our questions
        questions.addAll(tablesEngine!!.generateTablesList())

        // Set up the icons for the questions
        initialiseCounterViews()

        // Use fake recognizer that always gets it right!
        if (USE_TEST_RECOGNIZER) {
            recognizer = FakeSpeechRecognizer(this, Callbacks(), sometimesRightFunction)
        } else {
            recognizer = RealSpeechRecognizer(this, Callbacks())
        }

        showNextQuestion()
    }

    // Clear and populate counter views, used between each round and at start
    private fun initialiseCounterViews() {
        questionIconView.removeAllViews()
        for (q in questions) {
            val img = layoutInflater.inflate(R.layout.question_icon_layout, null)
            questionIconView.addView(img)
        }

        correctIconView.removeAllViews()
        for (q in correctAnswers) {
            val img = layoutInflater.inflate(R.layout.question_icon_layout, null) as ImageView
            img.setImageResource(R.drawable.green_chip)
            correctIconView.addView(img)
        }

        wrongIconView.removeAllViews()
        for (q in wrongAnswers) {
            val img = layoutInflater.inflate(R.layout.question_icon_layout, null) as ImageView
            img.setImageResource(R.drawable.red_chip)
            wrongIconView.addView(img)
        }
    }

    // Incremental update to counter views used for each question.
    private fun updateCounterViews(correct: Boolean) {
        val img: ImageView = questionIconView.getChildAt(questionIconView.childCount - 1) as ImageView
        questionIconView.removeViewAt(questionIconView.childCount - 1)
        if (correct) {
            img.setImageResource(R.drawable.green_chip)
            correctIconView.addView(img)
        } else {
            img.setImageResource(R.drawable.red_chip)
            wrongIconView.addView(img)
        }
    }


    override fun onStop() {
        super.onStop()
        recognizer!!.destroy()
    }

    private inner class Callbacks() : SpeechListener() {
        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            process(matches)
        }

        override fun onEndOfSpeech() {
            Log.d("adam", "onEndOfSpeech")
            this@TablesActivity.spinnerView.visibility = View.GONE
        }

        override fun onReadyForSpeech(arg0: Bundle) {
            Log.d("adam", "Ready for speech")
        }

        override fun onError(errorCode: Int) {
            Log.d("adam", "Error (probably timeout)!")
            process(null)
        }

        private fun process(matches: ArrayList<String>?) {
            Log.d("adam", "Got answers: $matches")
            this@TablesActivity.spinnerView.visibility = View.GONE

            // Update view with answer and text
            questionView.text = "${currentQuestion!!.lhs} x ${currentQuestion!!.rhs} = ${currentQuestion!!.getAnswer()}"
            incomingText.text = matches.toString()

            // Check answer
            if (tablesEngine!!.checkAnswer(currentQuestion!!, matches)) {
                Log.d("adam", "Correct!")
                answerTracker.correct()
                correctAnswers.add(currentQuestion!!)
                this@TablesActivity.correctView.visibility = View.VISIBLE
                correctVanishingAnimation!!.reset()
                this@TablesActivity.correctView.startAnimation(correctVanishingAnimation)
                if (correctSound!!.isPlaying) correctSound!!.stop()
                correctSound!!.start()
            } else {
                Log.d("adam", "Wrong!")
                answerTracker.wrong()
                wrongAnswers.add(currentQuestion!!)
                this@TablesActivity.wrongView.visibility = View.VISIBLE
                wrongVanishingAnimation!!.reset()
                this@TablesActivity.wrongView.startAnimation(wrongVanishingAnimation)
                if (wrongSound!!.isPlaying) wrongSound!!.stop()
                wrongSound!!.start()
            }

            updateCounterViews(tablesEngine!!.checkAnswer(currentQuestion!!, matches))
            launch(CommonPool) {
                delay(NEXT_QUESTION_DELAY)
                runOnUiThread({
                    showNextQuestion()
                })
            }
        }
    }

    // TODO: Neater way to do this?
    private fun showNextQuestion() {
        questionView.visibility = View.INVISIBLE
        if (questions.isEmpty()) {
            if (!wrongAnswers.isEmpty()) {
                // End of round, put incorrect questions in incoming queue.
                roundNumber++;
                wrongAnswerCount += wrongAnswers.size
                questions.addAll(wrongAnswers);
                wrongAnswers.clear()
                initialiseCounterViews()
                showOptionalNextRoundTransition()
            } else {
                // End of test, show completion summary.
                Log.d("adam", "No more questions. Finishing.")

                // Launch summary activity
                val summaryActivity = Intent(this, SummaryActivity::class.java)
                summaryActivity.putExtra("rounds", roundNumber)
                summaryActivity.putExtra("wronganswers", wrongAnswerCount)
                startActivity(summaryActivity)

                finish()
                return
            }
        } else {
            if (answerTracker.hasOptionalAnimation()) {
                showOptionalAnimation(answerTracker.getOptionalAnimation())
            } else {
                showNextQuestionAfterOptionalAnimations()
            }
        }
    }

    private fun showOptionalAnimation(message: String) {
        val disappear = AnimationUtils.loadAnimation(applicationContext, R.anim.disappear)!!;
        messageView.visibility = View.VISIBLE
        messageView.text = message

        disappear.setListener {
            onAnimationEnd {
                messageView.visibility = View.GONE
                showNextQuestionAfterOptionalAnimations()
            }
        }
        messageView.startAnimation(disappear);
    }

    private fun showOptionalNextRoundTransition() {
        messageView.visibility = View.VISIBLE
        messageView.text = "Round $roundNumber"

        val show = AnimationUtils.loadAnimation(applicationContext,
                R.anim.transientdisplay)!!;

        show.setListener {
            onAnimationEnd {
                messageView.visibility = View.GONE
                showNextQuestionAfterOptionalAnimations()
            }
        }

        nextRoundSound!!.start()
        messageView.startAnimation(show);
    }

    private fun showNextQuestionAfterOptionalAnimations() {
        questionView.visibility = View.VISIBLE

        val qs = questions.removeAt(0)
        currentQuestion = qs

        spinnerView.visibility = View.GONE
        wrongView.visibility = View.GONE
        correctView.visibility = View.GONE

        questionView.text = "${qs.lhs} x ${qs.rhs} = ?"
        incomingText.text = ""
        spinnerView.visibility = View.VISIBLE

        recognizer!!.startListening()
    }

    inline fun Animation.setListener(
            func: AnimationListenerWrapper.() -> Unit) {
        val listener = AnimationListenerWrapper()
        listener.func()
        setAnimationListener(listener)
    }
}

