package com.shoeboxscientist.mouthmaths

import android.os.Bundle
import android.speech.RecognitionListener
import android.util.Log

/**
 * Empty implementation of the RecognitionListener interface so the activity can just override the few
 * that it uses.
 */
open class SpeechListener() : RecognitionListener {

    override fun onBeginningOfSpeech() {
        Log.d("adam", "onBeginningofSpeech");
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.d("adam", "onBufferReceived");

    }

    override fun onEndOfSpeech() {
        Log.d("adam", "onEndOfSpeech");

    }

    override fun onError(errorCode: Int) {
        Log.d("adam", "onError: $errorCode");

    }

    override fun onEvent(arg0: Int, arg1: Bundle) {
        Log.d("adam", "onEvent $arg0");

    }

    override fun onPartialResults(arg0: Bundle) {
        Log.d("adam", "onPartialResults");

    }

    override fun onReadyForSpeech(arg0: Bundle) {
        Log.d("adam", "On ready for speech");
    }

    override fun onResults(results: Bundle) {
    }

    override fun onRmsChanged(rmsdB: Float) {}
}
