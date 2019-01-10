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
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.util.*

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
        searchQueryView.requestFocus()
    }

    @SuppressLint("RestrictedApi")
    override fun onEndPushChangeHandler() {
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
        super.onEndPushChangeHandler()
    }

    @SuppressLint("RestrictedApi")
    override fun onStartPopChangeHandler() {
        super.onStartPopChangeHandler()
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

    override fun onDetach(view: View) {
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
        searchQueryView.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clearSearch.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

        })
        searchQueryView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> hideKeyboard(searchQueryView)
            }
            true
        }
        clearSearch.setOnClickListener { searchQueryView.setText("") }
    }

}
