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
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import ie.dublinmapper.R
import ie.dublinmapper.util.hideKeyboard
import ie.dublinmapper.util.showKeyboard
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : DaggerFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        search_query.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                requireContext().showKeyboard(query)
            } else {
                requireContext().hideKeyboard(query)
            }
        }

        search_query.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clear_search.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

        })

        search_query.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> requireContext().hideKeyboard(search_query)
            }
            true
        }

        search_query.requestFocus() //TODO shouldn't need to do this manually
        clear_search.setOnClickListener { search_query.setText("") }
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.grey_500)
    }

    override fun onPause() {
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark)
        super.onPause()
    }

}
