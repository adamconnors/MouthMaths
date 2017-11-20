package com.shoeboxscientist.mouthmaths

import android.view.animation.Animation

/**
 * Created by adamconnors on 11/9/17.
 */
class AnimationListenerWrapper : Animation.AnimationListener {
    private var onAnimationRepeat: ((animation: Animation?) -> Unit)? = null
    private var onAnimationEnd: ((animation: Animation?) -> Unit)? = null
    private var onAnimationStart: ((animation: Animation?) -> Unit)? = null

    override fun onAnimationRepeat(animation: Animation?) {
        onAnimationRepeat?.invoke(animation)
    }

    fun onAnimationRepeat(func: (animation: Animation?) -> Unit) {
        onAnimationRepeat = func
    }

    override fun onAnimationEnd(animation: Animation?) {
        onAnimationEnd?.invoke(animation)
    }

    fun onAnimationEnd(func: (animation: Animation?) -> Unit) {
        onAnimationEnd = func
    }

    override fun onAnimationStart(animation: Animation?) {
        onAnimationStart?.invoke(animation)
    }

    fun onAnimationStart(func: (animation: Animation?) -> Unit) {
        onAnimationStart = func
    }
}