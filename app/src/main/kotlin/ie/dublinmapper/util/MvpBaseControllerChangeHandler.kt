package ie.dublinmapper.util

import android.animation.Animator
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.MvpBaseController

abstract class MvpBaseControllerChangeHandler(
    private val controller: MvpBaseController<MvpView, MvpPresenter<MvpView>>?,
    duration: Long,
    removesFromViewOnPush: Boolean
) : AnimatorChangeHandler(duration, removesFromViewOnPush) {

    constructor() : this(null, DEFAULT_ANIMATION_DURATION, true)

    open fun addAnimationListener(isPush: Boolean, animator: Animator): Animator {
        animator.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator?) {
                if (!isPush) controller?.onStartPopChangeHandler()
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (isPush) controller?.onEndPushChangeHandler()
            }

            override fun onAnimationCancel(animation: Animator?) { }

            override fun onAnimationRepeat(animation: Animator?) { }

        })
        return animator
    }

}
