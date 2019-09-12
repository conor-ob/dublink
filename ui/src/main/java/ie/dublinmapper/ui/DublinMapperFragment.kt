package ie.dublinmapper.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class DublinMapperFragment(
    @LayoutRes private val layoutId: Int
) : Fragment(layoutId), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected abstract fun styleId(): Int

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setupStatusBar()
        val contextThemeWrapper = ContextThemeWrapper(requireActivity(), styleId())
        val themeInflater = inflater.cloneInContext(contextThemeWrapper)
        return themeInflater.inflate(layoutId, container, false)
    }

    private fun setupStatusBar() {
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val attributes = requireActivity().obtainStyledAttributes(styleId(), R.styleable.ThemeAttributes)
        val primaryDark = attributes.getColor(R.styleable.ThemeAttributes_colorPrimaryDark, 0)
        window.statusBarColor = primaryDark
        window.navigationBarColor = primaryDark
        attributes.recycle()
    }

    override fun androidInjector() = androidInjector

}
