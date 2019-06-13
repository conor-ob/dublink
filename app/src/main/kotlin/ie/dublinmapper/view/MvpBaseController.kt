package ie.dublinmapper.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.hannesdorfmann.mosby3.mvp.conductor.MvpController
import ie.dublinmapper.R
import ie.dublinmapper.util.requireActivity
import timber.log.Timber

abstract class MvpBaseController<V : MvpView, P : MvpPresenter<V>>(args: Bundle) : MvpController<V, P>(args) {

    protected abstract val styleId: Int

    protected abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(layoutId, container, false)
        setupStatusBar()
        return view
    }

    private fun setupStatusBar() {
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val attributes = requireActivity().obtainStyledAttributes(styleId, R.styleable.ThemeAttributes)
        val statusBarColour = attributes.getColor(R.styleable.ThemeAttributes_colorPrimaryDark, 0)
        window.statusBarColor = statusBarColour
        attributes.recycle()
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
