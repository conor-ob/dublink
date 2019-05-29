package ie.dublinmapper.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.hannesdorfmann.mosby3.mvp.conductor.MvpController
import timber.log.Timber

abstract class MvpBaseController<V : MvpView, P : MvpPresenter<V>>(args: Bundle) : MvpController<V, P>(args) {

    protected abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)
        Timber.d("${javaClass.simpleName} onChangeStarted $changeType")
    }

    override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        Timber.d("${javaClass.simpleName} onChangeEnded $changeType")
        super.onChangeEnded(changeHandler, changeType)
    }

}
