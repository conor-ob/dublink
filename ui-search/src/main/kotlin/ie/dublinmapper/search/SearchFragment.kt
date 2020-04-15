package ie.dublinmapper.search

import android.app.Activity
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.model.extractServiceLocation
import ie.dublinmapper.model.isSearchCandidate
import ie.dublinmapper.util.hideKeyboard
import ie.dublinmapper.viewModelProvider
import kotlinx.android.synthetic.main.fragment_search.search_input
import kotlinx.android.synthetic.main.fragment_search.search_list
import kotlinx.android.synthetic.main.fragment_search.search_progress_bar
import kotlinx.android.synthetic.main.fragment_search.search_toolbar

class SearchFragment : DublinMapperFragment(R.layout.fragment_search) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as SearchViewModel }

    private var adapter: GroupAdapter<GroupieViewHolder>? = null

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

        search_input.setIconifiedByDefault(true)
        search_input.focusable = View.FOCUSABLE
        search_input.isIconified = false
        search_input.requestFocus()

        adapter = GroupAdapter()
        search_list.adapter = adapter
        search_list.setHasFixedSize(true)
        search_list.layoutManager = LinearLayoutManager(requireContext())
        adapter?.setOnItemClickListener { item, _ ->
            val serviceLocation = item.extractServiceLocation()
            if (serviceLocation != null) {
                if (item is ServiceLocationItem && item.isSearchCandidate()) {
                    viewModel.dispatch(Action.AddRecentSearch(serviceLocation.service, serviceLocation.id))
                }
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
                    viewModel.dispatch(Action.Search(newText.toString()))
                    return true
                }
            }
        )
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
        viewModel.bindActions()
        search_input.query?.toString()?.let { query ->
            viewModel.dispatch(Action.Search(query))
        }
        viewModel.dispatch(Action.GetNearbyLocations)
        viewModel.dispatch(Action.GetRecentSearches)
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(search_input)
        viewModel.unbindActions()
    }

    private fun renderState(state: State) {
        if (state. throwable != null) {
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
                    state.nearbyLocations,
                    state.searchResults,
                    state.recentSearches
                )
            )
        )
        search_list.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
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
