package ie.dublinmapper.search

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.Navigator
import ie.dublinmapper.ui.DublinMapperFragment
import ie.dublinmapper.ui.viewModelProvider
import ie.dublinmapper.util.hideKeyboard
import ie.dublinmapper.util.showKeyboard
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.rtpi.api.ServiceLocation
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchFragment : DublinMapperFragment(R.layout.fragment_search) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as SearchViewModel }

    private lateinit var adapter: GroupAdapter<ViewHolder>
    private val subscriptions = CompositeDisposable()

    override fun styleId() = R.style.SearchTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        search_query.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                showKeyboard(query)
            } else {
                hideKeyboard(query)
            }
        }

        adapter = GroupAdapter()
        search_results.adapter = adapter
        search_results.setHasFixedSize(true)
        search_results.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemClickListener { item, _ ->
            (item.extras["serviceLocation"] as? ServiceLocation)?.let { serviceLocation ->
                (activity as Navigator).navigateSearchToLiveData(serviceLocation)
            }
        }

        subscriptions.add(
            Observable.create<String> { subscriber ->
                search_query.addTextChangedListener(object : TextWatcher {

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        clear_search.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
                        subscriber.onNext(s.toString())
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun afterTextChanged(s: Editable?) {}

                })
            }
                .debounce(400L, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter { it.length > 1 }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    viewModel.dispatch(Action.Search(it))
                }
                .subscribe()
        )

        search_query.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> hideKeyboard(search_query)
            }
            true
        }
        clear_search.setOnClickListener { search_query.setText("") }
    }

    private fun renderState(state: State) {
        Timber.d(state.toString())
        if (state.searchResults != null) {
            adapter.update(listOf(state.searchResults))
        }
    }

}
