package io.dublink.favourites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.DubLinkFragment
import io.dublink.DubLinkNavigator
import io.dublink.domain.internet.InternetStatus
import io.dublink.model.AbstractServiceLocationItem
import io.dublink.model.getServiceLocation
import io.dublink.viewModelProvider
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.view.favourites_list

class FavouritesFragment : DubLinkFragment(R.layout.fragment_favourites) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as FavouritesViewModel }
    private var adapter: GroupAdapter<GroupieViewHolder>? = null

    @Inject
    lateinit var favouritesMapper: FavouritesMapper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favourites_toolbar.menu.findItem(R.id.action_edit).apply {
            isVisible = false
        }

        favourites_toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // TODO analytics
                    (activity as DubLinkNavigator).navigateToEditFavourites()
                    return@setOnMenuItemClickListener true
                }
                R.id.action_settings -> {
                    // TODO analytics
                    (activity as DubLinkNavigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }

        favourites_fab_search.setOnClickListener {
            (activity as DubLinkNavigator).navigateToSearch()
        }

        adapter = GroupAdapter()
        adapter?.setOnItemClickListener { item, _ ->
            if (item is AbstractServiceLocationItem) {
                (activity as DubLinkNavigator).navigateToLiveData(
                    serviceLocation = item.getServiceLocation()
                )
            }
        }
        view.favourites_list.adapter = adapter
        view.favourites_list.setHasFixedSize(true)
        view.favourites_list.layoutManager = LinearLayoutManager(requireContext())

        favourites_swipe_refresh.apply {
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

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    private fun renderState(state: State) {
        if (state.favourites != null && state.favourites.isEmpty()) {
            favourites_swipe_refresh.isRefreshing = false
        } else {
            favourites_swipe_refresh.isRefreshing = state.favourites == null ||
                state.favouritesWithLiveData == null ||
                state.favouritesWithLiveData.any { it is LiveDataPresentationResponse.Loading }
        }
        favourites_toolbar.menu.findItem(R.id.action_edit).apply {
            isVisible = !state.favourites.isNullOrEmpty()
        }
        adapter?.update(listOf(favouritesMapper.map(state.favourites, state.favouritesWithLiveData)))
        if (state.internetStatusChange == InternetStatus.ONLINE) {
            viewModel.dispatch(Action.GetFavourites)
            viewModel.dispatch(Action.GetLiveData)
        }
    }
}
