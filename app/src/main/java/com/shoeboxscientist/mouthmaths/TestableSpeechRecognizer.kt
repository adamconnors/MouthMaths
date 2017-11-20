package com.shoeboxscientist.mouthmaths

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Simple wrapper for the SpeechRecognizer to make it easier to inject events
 */
interface TestableSpeechRecognizer {
    fun startListening()
    fun destroy()
}

class FakeSpeechRecognizer(val ctx: Activity,
                           val callback: SpeechListener,
                           val fakeAnswers: () -> ArrayList<String>) : TestableSpeechRecognizer {
    override fun destroy() {
        // Nothing to do here
    }

    override fun startListening() {
        launch(CommonPool) {
            delay(1000)
            val b = Bundle()
            b.putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, fakeAnswers.invoke())
            ctx.runOnUiThread({ callback.onResults(b) })
        }
    }
}

class RealSpeechRecognizer(val ctx: Activity,
                           val callback: SpeechListener) : TestableSpeechRecognizer {

    var recognizer: SpeechRecognizer? = null

    override fun startListening() {
        val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, ctx.packageName)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)

        recognizer = SpeechRecognizer.createSpeechRecognizer(ctx)
        recognizer!!.setRecognitionListener(callback)
        recognizer!!.startListening(recognizerIntent)
    }

    override fun destroy() {
        recognizer!!.destroy()
    }

}
