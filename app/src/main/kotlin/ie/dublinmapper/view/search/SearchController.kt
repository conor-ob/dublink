package ie.dublinmapper.view.search

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
import androidx.core.content.ContextCompat
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.util.hideKeyboard
import ie.dublinmapper.util.showKeyboard

class SearchController(
    args: Bundle
) : MvpBaseController<SearchView, SearchPresenter>(args), SearchView {

    override fun layoutId() = R.layout.view_search

    override fun createPresenter(): SearchPresenter {
        return requireApplicationComponent().searchPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupLayout(view)
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.grey_400)
    }

    override fun onDetach(view: View) {
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark)
        super.onDetach(view)
    }

    private fun setupLayout(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { router.handleBack() }
        val searchQueryView: EditText = view.findViewById(R.id.search_query)
        searchQueryView.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                requireContext().showKeyboard(query)
            } else {
                requireContext().hideKeyboard(query)
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
                EditorInfo.IME_ACTION_SEARCH -> requireContext().hideKeyboard(searchQueryView)
            }
            true
        }
        searchQueryView.requestFocus() //TODO shouldn't need to do this manually
        clearSearch.setOnClickListener { searchQueryView.setText("") }
    }

}
