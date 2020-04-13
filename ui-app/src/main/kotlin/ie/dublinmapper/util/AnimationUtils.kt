package ie.dublinmapper.util

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable

object AnimationUtils {

    private const val HALF_SECOND = 500L
    private const val TRANSPARENT = 0f
    private const val OPAQUE = 1f

//    fun fadeInMarker(marker: Marker) {
//        ObjectAnimator.ofFloat(marker, "alpha", TRANSPARENT, OPAQUE).setDuration(HALF_SECOND).start()
//    }
//
//    fun fadeOutMarker(marker: Marker) {
//        val animator = ObjectAnimator.ofFloat(marker, "alpha", OPAQUE, TRANSPARENT)
//        animator.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationEnd(animator: Animator) {
//                marker.remove()
//            }
//
//            override fun onAnimationStart(animator: Animator) {}
//            override fun onAnimationCancel(animator: Animator) {}
//            override fun onAnimationRepeat(animator: Animator) {}
//        })
//        animator.setDuration(HALF_SECOND).start()
//    }

    fun fadeViewColour(drawable: Drawable, fromColour: Int, toColour: Int) {
        val colorFade = ObjectAnimator.ofObject(drawable, "tint", ArgbEvaluator(), fromColour, toColour)
        colorFade.duration = HALF_SECOND
        colorFade.start()
    }
}
