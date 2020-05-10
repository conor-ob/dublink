package io.dublink

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import java.util.concurrent.atomic.AtomicBoolean

abstract class LifecycleAwareViewModel<A : BaseAction, S : BaseState> : BaseViewModel<A, S>() {

    private val isActive = AtomicBoolean(true)

    protected fun isActive(): Boolean = isActive.get()

    open fun onResume() {
        isActive.set(true)
    }

    open fun onPause() {
        isActive.set(false)
    }
}
