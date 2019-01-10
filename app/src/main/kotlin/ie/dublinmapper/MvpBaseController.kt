package ie.dublinmapper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.hannesdorfmann.mosby3.mvp.conductor.MvpController

abstract class MvpBaseController<V : MvpView, P : MvpPresenter<V>>(args: Bundle) : MvpController<V, P>(args) {

    protected abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        onStartPushChangeHandler()
    }

    open fun onStartPushChangeHandler() { }

    open fun onEndPushChangeHandler() { }

    override fun onDetach(view: View) {
        onEndPopChangeHandler()
        super.onDetach(view)
    }

    open fun onStartPopChangeHandler() { }

    open fun onEndPopChangeHandler() { }

}
