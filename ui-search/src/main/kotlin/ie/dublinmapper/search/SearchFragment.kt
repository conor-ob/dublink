package ie.dublinmapper.search

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.model.AbstractServiceLocationItem
import ie.dublinmapper.model.getLocationId
import ie.dublinmapper.model.getService
import ie.dublinmapper.model.isSearchCandidate
import ie.dublinmapper.util.hideKeyboard
import ie.dublinmapper.util.showKeyboard
import ie.dublinmapper.viewModelProvider
import kotlinx.android.synthetic.main.fragment_search.search_input
import kotlinx.android.synthetic.main.fragment_search.search_list
import kotlinx.android.synthetic.main.fragment_search.search_progress_bar
import kotlinx.android.synthetic.main.fragment_search.search_toolbar

private const val locationRequestCode = 42069

class SearchFragment : DublinMapperFragment(R.layout.fragment_search) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as SearchViewModel }

    private var adapter: GroupAdapter<GroupieViewHolder>? = null

    private var keyboardHasFocus = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        search_toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    (activity as DublinMapperNavigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }

        adapter = GroupAdapter()
        search_list.adapter = adapter
        search_list.setHasFixedSize(true)
        search_list.layoutManager = LinearLayoutManager(requireContext())
        adapter?.setOnItemClickListener { item, _ ->
            if (item is AbstractServiceLocationItem && item.isSearchCandidate()) {
                viewModel.dispatch(
                    Action.AddRecentSearch(
                        service = item.getService(),
                        locationId = item.getLocationId()
                    )
                )
            }
            if (item is AbstractServiceLocationItem) {
                (activity as DublinMapperNavigator).navigateToLiveData(
                    service = item.getService(),
                    locationId = item.getLocationId()
                )
            }
        }

        search_input.apply {
            setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
    //                    query?.let {
    //                        if (it.isEmpty()) {
    //                            viewModel.dispatch(Action.GetRecentSearches)
    //                            viewModel.dispatch(Action.GetNearbyLocations)
    //                        }
    //                    }
    //                    viewModel.dispatch(Action.Search(query.toString()))
                        hideKeyboard(search_input)
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let {
                            if (it.isEmpty()) {
                                viewModel.dispatch(Action.GetRecentSearches)
                                viewModel.dispatch(Action.GetNearbyLocations)
                            }
                        }
                        viewModel.dispatch(Action.Search(newText.toString()))
                        return true
                    }
                }
            )

            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showKeyboard(view.findFocus())
                }
                keyboardHasFocus = hasFocus
            }

            if (keyboardHasFocus) {
                setIconifiedByDefault(true)
                focusable = View.FOCUSABLE
                isIconified = false
                requestFocus()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (activity?.isKeyboardOpened() == true) {
                        hideKeyboard(search_input)
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        val query = search_input.query?.toString()
        if (query != null && query.length > 1) {
            viewModel.dispatch(Action.Search(query))
        } else {
            viewModel.dispatch(Action.GetRecentSearches)
            viewModel.dispatch(Action.GetNearbyLocations)
        }
        if (!keyboardHasFocus) {
            hideKeyboard(search_input)
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(search_input)
    }

    private fun renderState(state: State) {
        if (state.throwable != null) {
            Toast.makeText(requireContext(), state.throwable.message, Toast.LENGTH_LONG).show()
        }
        if (state.loading != null && state.loading == true) {
            search_progress_bar.visibility = View.VISIBLE
        } else {
            search_progress_bar.visibility = View.GONE
        }
        adapter?.update(
            listOf(
                SearchResponseMapper.map(
                    state.searchResults,
                    state.recentSearches,
                    state.nearbyLocations,
                    object : OnEnableLocationClickedListener {
                        override fun onEnableLocationClicked() {
                            enableLocation()
                        }
                    },
                    object : ClearRecentSearchesClickListener {
                        override fun onClearRecentSearchesClicked() {
                            viewModel.dispatch(Action.ClearRecentSearches)
                        }
                    }
                )
            )
        )
        if (state.scrollToTop != null && state.scrollToTop == true) {
            try {
                search_list.scrollToPosition(0)
            } catch (e: Exception) {
                // ignored
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    private fun enableLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
//            viewModel.dispatch(Action.GetNearbyLocations)
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationRequestCode) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                viewModel.dispatch(Action.GetNearbyLocations)
            }
        }
    }
}

fun Activity.isKeyboardOpened(): Boolean {
    val r = Rect()

    val activityRoot = getActivityRoot()
    val visibleThreshold = dip(100)

    activityRoot.getWindowVisibleDisplayFrame(r)

    val heightDiff = activityRoot.rootView.height - r.height()

    return heightDiff > visibleThreshold
}

fun Activity.getActivityRoot(): View {
    return (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)
}

fun dip(value: Int): Int {
    return (value * Resources.getSystem().displayMetrics.density).toInt()
}
