package ie.dublinmapper.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.model.extractServiceLocation
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.viewModelProvider
import ie.dublinmapper.util.hideKeyboard
import ie.dublinmapper.util.showKeyboard
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.rtpi.api.Service
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.concurrent.TimeUnit

class SearchFragment : DublinMapperFragment(R.layout.fragment_search) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as SearchViewModel }

    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private val subscriptions = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        search_searchview.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                showKeyboard(query)
            } else {
                hideKeyboard(query)
            }
        }

        adapter = GroupAdapter()
        search_recyclerview.adapter = adapter
        search_recyclerview.setHasFixedSize(true)
        search_recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemClickListener { item, _ ->
            val serviceLocation = item.extractServiceLocation()
            if (serviceLocation != null) {
                (activity as DublinMapperNavigator).navigateToLiveData(serviceLocation)
            }
        }

        subscriptions.add(
            Observable.create<String> { subscriber ->
                search_searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        subscriber.onNext(query.toString())
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
//                        clear_search.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
                        subscriber.onNext(newText.toString())
                        return true
                    }
                })
//                search_query.addTextChangedListener(object : TextWatcher {
//
//                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                        clear_search.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
//                        subscriber.onNext(s.toString())
//                    }
//
//                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//                    override fun afterTextChanged(s: Editable?) {}
//
//                })
            }
                .debounce(400L, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter { it.length > 1 }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
//                    viewModel.dispatch(Action.Loading)
                    viewModel.dispatch(Action.Search(it))
                }
                .subscribe()
        )

//        search_query.setOnEditorActionListener { _, actionId, _ ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_SEARCH -> hideKeyboard(search_query)
//            }
//            true
//        }
//        clear_search.setOnClickListener { search_query.setText("") }
    }

    private fun renderState(state: State) {
        search_progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        if (state.searchResults != null) {
            adapter.update(listOf(state.searchResults))
        }
    }

}
