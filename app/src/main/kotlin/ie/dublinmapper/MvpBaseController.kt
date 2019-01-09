package ie.dublinmapper

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.hannesdorfmann.mosby3.mvp.conductor.MvpController
import ie.dublinmapper.di.ApplicationComponent

abstract class MvpBaseController<V : MvpView, P : MvpPresenter<V>>(args: Bundle) : MvpController<V, P>(args) {

    @LayoutRes
    protected abstract fun layoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(layoutId(), container, false)
    }

    fun requireApplicationComponent(): ApplicationComponent {
        return (requireActivity().application as DublinMapperApplication).applicationComponent
    }

    fun requireActivity(): Activity {
        return activity ?: throw IllegalStateException("Controller[$this] not attached to an activity.")
    }

    fun requireContext(): Context {
        return applicationContext ?: throw IllegalStateException("Controller[$this] not attached to a context.")
    }

}
