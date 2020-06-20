package io.dublink

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.dublink.domain.internet.InternetStatus
import io.dublink.ui.R
import javax.inject.Inject
import timber.log.Timber

abstract class DubLinkFragment(
    @LayoutRes private val layoutId: Int
) : Fragment(layoutId), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as DubLinkFragmentViewModel }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
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
        return inflater.inflate(layoutId, container, false)
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
        viewModel.dispatch(DubLinkFragmentAction.SubscribeToInternetStatusChanges)
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

    private fun renderState(state: DubLinkFragmentState) {
        if (state.internetStatusChangeEvent != null && state.internetStatusChangeEvent.isRecent()) {
            when (state.internetStatusChangeEvent.internetStatusChange) {
                InternetStatus.ONLINE -> {
                    val snackBar = attachSnackBarToView("Back online!", Snackbar.LENGTH_LONG)
                    snackBar?.setActionTextColor(requireContext().getColor(R.color.color_on_success))
                    snackBar?.setTextColor(requireContext().getColor(R.color.color_on_success))
                    snackBar?.setBackgroundTint(requireContext().getColor(R.color.color_success))
                    snackBar?.show()
                    onInternetRestored()
//                viewModel.dispatch(Action.QueryPurchases)
//                viewModel.dispatch(Action.PreloadData)
                }
                InternetStatus.OFFLINE -> {
                    val snackBar = attachSnackBarToView("Offline", Snackbar.LENGTH_INDEFINITE)
//                snackBar = Snackbar.make(activity_root, "Offline \uD83D\uDE14", Snackbar.LENGTH_INDEFINITE)
                    snackBar?.setAction("Dismiss") {
                        snackBar.dismiss()
                    }
                    snackBar?.setActionTextColor(requireContext().getColor(R.color.color_on_error))
                    snackBar?.setTextColor(requireContext().getColor(R.color.color_on_error))
                    snackBar?.setBackgroundTint(requireContext().getColor(R.color.color_error))
                    snackBar?.show()
                }
            }
        }
    }

    protected open fun attachSnackBarToView(text: String, length: Int): Snackbar? = null

    protected open fun onInternetRestored() {}

    override fun androidInjector() = androidInjector
}
