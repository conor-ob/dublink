package ie.dublinmapper.favourites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.model.extractServiceLocation
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.domain.internet.InternetStatus
import ie.dublinmapper.viewModelProvider
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.view.*
import timber.log.Timber

class FavouritesFragment : DublinMapperFragment(R.layout.fragment_favourites) {

    private var showLoading = true

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as FavouritesViewModel }
    private var adapter: GroupAdapter<GroupieViewHolder>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favourites_toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
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
            val serviceLocation = item.extractServiceLocation()
            if (serviceLocation != null) {
                if (!enabledServiceManager.isServiceEnabled(serviceLocation.service)) {
                    enabledServiceManager.enableService(serviceLocation.service)
                }
                (activity as DublinMapperNavigator).navigateToLiveData(serviceLocation)
            }
        }
        view.favourites_list.adapter = adapter
        view.favourites_list.setHasFixedSize(true)
        view.favourites_list.layoutManager = LinearLayoutManager(requireContext())
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
        viewModel.bindActions()
        viewModel.dispatch(Action.GetFavouritesWithLiveData(showLoading))
        viewModel.dispatch(Action.SubscribeToInternetStatusChanges)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unbindActions()
    }

    private fun renderState(state: State) {
        if (state.favouritesWithLiveData != null) {
            adapter?.update(listOf(FavouritesMapper.map(state.favouritesWithLiveData)))
            showLoading = false
        }
        if (state.internetStatusChange == InternetStatus.ONLINE) {
            viewModel.dispatch(Action.GetFavouritesWithLiveData(true))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}
