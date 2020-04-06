package ie.dublinmapper.search

import android.os.Bundle
import android.view.Menu
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
import kotlinx.android.synthetic.main.fragment_search.*

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
//                if (item.extras["searchCandidate"] as Boolean) {
                    // viewModel.dispatch(Action.AddRecentSearch())
//                }
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
    }

    override fun onResume() {
        super.onResume()
        viewModel.dispatch(Action.GetNearbyLocations)
        viewModel.dispatch(Action.GetRecentSearches)
    }

    override fun onPause() {
        super.onPause()

        hideKeyboard(search_input)
    }

    private fun renderState(state: State) {
        adapter?.update(
            listOf(
                SearchResponseMapper.map(
                    state.nearbyLocations,
                    state.searchResults,
                    state.recentSearches
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}
