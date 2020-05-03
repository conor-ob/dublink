package ie.dublinmapper.favourites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.domain.internet.InternetStatus
import ie.dublinmapper.model.AbstractServiceLocationItem
import ie.dublinmapper.model.getLocationId
import ie.dublinmapper.model.getService
import ie.dublinmapper.viewModelProvider
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.view.favourites_list
import timber.log.Timber

class FavouritesFragment : DublinMapperFragment(R.layout.fragment_favourites) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as FavouritesViewModel }
    private var adapter: GroupAdapter<GroupieViewHolder>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favourites_toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    (activity as DublinMapperNavigator).navigateToEditFavourites()
                    return@setOnMenuItemClickListener true
                }
                R.id.action_settings -> {
                    (activity as DublinMapperNavigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }

        favourites_fab_search.setOnClickListener {
            (activity as DublinMapperNavigator).navigateToSearch()
        }

        adapter = GroupAdapter()
        adapter?.setOnItemClickListener { item, _ ->
            if (item is AbstractServiceLocationItem) {
                (activity as DublinMapperNavigator).navigateToLiveData(
                    service = item.getService(),
                    locationId = item.getLocationId()
                )
            }
        }
        view.favourites_list.adapter = adapter
        view.favourites_list.setHasFixedSize(true)
        view.favourites_list.layoutManager = LinearLayoutManager(requireContext())

        favourites_swipe_resresh.apply {
            setColorSchemeResources(R.color.color_on_surface)
            setProgressBackgroundColorSchemeResource(R.color.color_surface)
            setOnRefreshListener {
                viewModel.dispatch(Action.GetFavourites)
                viewModel.dispatch(Action.RefreshLiveData)
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
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        viewModel.dispatch(Action.GetFavourites)
        viewModel.dispatch(Action.GetLiveData)
        viewModel.dispatch(Action.SubscribeToInternetStatusChanges)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun renderState(state: State) {
        Timber.d("isLoading=${state.isLoading}")
        favourites_swipe_resresh.isRefreshing = state.isLoading
        if (state.favourites != null) {
            adapter?.update(listOf(FavouritesMapper.map(state.favourites, state.favouritesWithLiveData)))
        }
        if (state.internetStatusChange == InternetStatus.ONLINE) {
            viewModel.dispatch(Action.GetFavourites)
            viewModel.dispatch(Action.GetLiveData)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}
