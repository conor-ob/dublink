package ie.dublinmapper.view.search

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.util.*
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchController(
    args: Bundle
) : MvpBaseController<SearchView, SearchPresenter>(args), SearchView {

    private lateinit var searchQueryView: EditText

    override val layoutId = R.layout.view_search

    override fun createPresenter(): SearchPresenter {
        return getApplicationComponent().searchPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupLayout(view)
        return view
    }

    //TODO add test for keyboard showing
    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.onViewAttached()
    }

    @SuppressLint("RestrictedApi")
    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)
        if (changeType == ControllerChangeType.POP_EXIT) {
            hideKeyboard(searchQueryView)

            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), getColour(R.color.grey_400), getColour(R.color.primary_dark))
            colorAnimation.duration = 600L
            colorAnimation.start()

            val window = requireActivity().window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            colorAnimation.addUpdateListener { animator ->
                val color = animator.animatedValue as Int

                // change status, navigation, and action bar color
                window.statusBarColor = color
//            window.navigationBarColor = color
//            supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
            }


//        searchQueryView.clearFocus()
//        val window = requireActivity().window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeEnded(changeHandler, changeType)
        if (changeType == ControllerChangeType.PUSH_ENTER) {
            searchQueryView.requestFocus()
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), getColour(R.color.primary_dark), getColour(R.color.grey_400))
            colorAnimation.duration = 500L
            colorAnimation.start()

            val window = requireActivity().window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            colorAnimation.addUpdateListener { animator ->
                val color = animator.animatedValue as Int

                // change status, navigation, and action bar color
                window.statusBarColor = color
//            window.navigationBarColor = color
//            supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
            }


//        searchQueryView.requestFocus()
//        val window = requireActivity().window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.grey_400)
        }
    }

    override fun onDetach(view: View) {
        presenter.onViewDetached()
        super.onDetach(view)
    }

    private fun setupLayout(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { router.handleBack() }
        searchQueryView = view.findViewById(R.id.search_query)
        searchQueryView.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                showKeyboard(query)
            } else {
                hideKeyboard(query)
            }
        }
        val clearSearch: ImageView = view.findViewById(R.id.clear_search)

        val subscription = Observable.create<String> { subscriber ->

            searchQueryView.addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    clearSearch.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
                    subscriber.onNext(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun afterTextChanged(s: Editable?) { }

            })
        }
            .debounce(400L, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .filter { it.length > 1 }
            .doOnNext { presenter.onQueryTextSubmit(it) }
            .subscribe()

        searchQueryView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> hideKeyboard(searchQueryView)
            }
            true
        }
        clearSearch.setOnClickListener { searchQueryView.setText("") }
    }

    override fun showSearchResults(searchResults: List<ServiceLocation>) {
        for (result in searchResults) {
            Timber.d(result.toString())
        }
    }

}
