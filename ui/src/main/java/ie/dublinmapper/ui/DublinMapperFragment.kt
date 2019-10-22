package ie.dublinmapper.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import ie.dublinmapper.util.EnabledServiceManager
import timber.log.Timber
import javax.inject.Inject

abstract class DublinMapperFragment(
    @LayoutRes private val layoutId: Int
) : Fragment(layoutId), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var enabledServiceManager: EnabledServiceManager

    protected abstract fun styleId(): Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        setupStatusBar()
        val contextThemeWrapper = ContextThemeWrapper(requireActivity(), styleId())
        val themeInflater = inflater.cloneInContext(contextThemeWrapper)
        return themeInflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
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
