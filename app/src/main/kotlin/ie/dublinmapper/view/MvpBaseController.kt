package ie.dublinmapper.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.view.ContextThemeWrapper
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.hannesdorfmann.mosby3.mvp.conductor.MvpController
import ie.dublinmapper.R
import ie.dublinmapper.util.requireActivity
import ie.dublinmapper.util.requireIntArg
import timber.log.Timber

abstract class MvpBaseController<V : MvpView, P : MvpPresenter<V>>(args: Bundle) : MvpController<V, P>(args) {

    protected abstract val layoutId: Int

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        super.onActivityPaused(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        super.onActivityStopped(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        setupStatusBar()
        val contextThemeWrapper = ContextThemeWrapper(requireActivity(), requireIntArg(STYLE_ID))
        val themeInflater = inflater.cloneInContext(contextThemeWrapper)
        return themeInflater.inflate(layoutId, container, false)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onDetach(view: View) {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        super.onDetach(view)
    }

    override fun onDestroyView(view: View) {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        super.onDestroyView(view)
    }

    override fun onDestroy() {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        super.onDestroy()
    }

    override fun onContextUnavailable() {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        super.onContextUnavailable()
    }

    private fun setupStatusBar() {
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val attributes = requireActivity().obtainStyledAttributes(args.getInt(STYLE_ID), R.styleable.ThemeAttributes)
        val primaryDark = attributes.getColor(R.styleable.ThemeAttributes_colorPrimaryDark, 0)
        window.statusBarColor = primaryDark
        window.navigationBarColor = primaryDark
        attributes.recycle()
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}(changeType = $changeType)")
    }

    override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}(changeType = $changeType)")
        super.onChangeEnded(changeHandler, changeType)
    }

    open class Builder(
        private val styleId: Int
    ) {

        fun buildArgs(): Bundle {
            return Bundle().apply {
                putInt(STYLE_ID, styleId)
            }
        }
    }

    companion object {

        const val STYLE_ID = "style_id"

    }

}
