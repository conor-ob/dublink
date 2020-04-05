package ie.dublinmapper.search

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchFragment : DublinMapperFragment(R.layout.fragment_search) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as SearchViewModel }

    private var adapter: GroupAdapter<GroupieViewHolder>? = null
    private val subscriptions = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        search_toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    (activity as DublinMapperNavigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }

        search_input.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                showKeyboard(query)
            } else {
                hideKeyboard(query)
            }
        }

        adapter = GroupAdapter()
        search_list.adapter = adapter
        search_list.setHasFixedSize(true)
        search_list.layoutManager = LinearLayoutManager(requireContext())
        adapter?.setOnItemClickListener { item, _ ->
            val serviceLocation = item.extractServiceLocation()
            if (serviceLocation != null) {
                (activity as DublinMapperNavigator).navigateToLiveData(serviceLocation)
            }
        }

        search_input.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.dispatch(Action.Search(query.toString()))
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
    //                        clear_search.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
                    viewModel.dispatch(Action.Search(newText.toString()))
                    return true
                }
            }
        )

//        subscriptions.add(
//            Observable.create<String> { subscriber ->
//                search_input.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//
//                    override fun onQueryTextSubmit(query: String?): Boolean {
//                        subscriber.onNext(query.toString())
//                        return true
//                    }
//
//                    override fun onQueryTextChange(newText: String?): Boolean {
////                        clear_search.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
//                        subscriber.onNext(newText.toString())
//                        return true
//                    }
//                })
////                search_query.addTextChangedListener(object : TextWatcher {
////
////                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
////                        clear_search.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
////                        subscriber.onNext(s.toString())
////                    }
////
////                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
////
////                    override fun afterTextChanged(s: Editable?) {}
////
////                })
//            }
//                .debounce(400L, TimeUnit.MILLISECONDS)
//                .distinctUntilChanged()
////                .filter { it.length > 1 }
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext {
////                    viewModel.dispatch(Action.Loading)
//                    viewModel.dispatch(Action.Search(it))
//                }
//                .subscribe()
//        )

//        search_query.setOnEditorActionListener { _, actionId, _ ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_SEARCH -> hideKeyboard(search_query)
//            }
//            true
//        }
//        clear_search.setOnClickListener { search_query.setText("") }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            viewModel.dispatch(Action.GetNearbyLocations)
        }
        viewModel.dispatch(Action.GetRecentSearches)

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location : Location? ->
////                Timber.d("Location=$location")
//            }
    }

    private fun renderState(state: State) {
        if (state.searchResults != null) {
            adapter?.update(listOf(state.searchResults))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}
