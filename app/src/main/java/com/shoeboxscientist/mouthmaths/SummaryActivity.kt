package com.shoeboxscientist.mouthmaths

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_summary.*

class SummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
    }

    override fun onStart() {
        super.onStart()
        val rounds = intent.getIntExtra("rounds", 1)
        val wrongAnswers = intent.getIntExtra("wronganswers", 0)

        roundsCountView.text = Integer.toString(rounds)
        dooverCountView.text = Integer.toString(wrongAnswers)

        if (wrongAnswers == 0) {
            commentView.text = "Perfect !"
        }

        val correctSound = MediaPlayer.create(this, R.raw.applause)
        correctSound.start()

        val appear = AnimationUtils.loadAnimation(this, R.anim.appear)
        commentView.startAnimation(appear)

        // Back to tables selection page
        okay.setOnClickListener { finish() }

    }
}
