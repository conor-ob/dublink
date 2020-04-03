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
import ie.dublinmapper.domain.internet.InternetStatusMonitor
import ie.dublinmapper.domain.internet.InternetStatusSubscriber
import ie.dublinmapper.viewModelProvider
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.view.*
import javax.inject.Inject

class FavouritesFragment : DublinMapperFragment(R.layout.fragment_favourites) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as FavouritesViewModel }
    @Inject lateinit var listener: InternetStatusMonitor
    private var adapter: GroupAdapter<GroupieViewHolder>? = null
    private val subscriber = object : InternetStatusSubscriber {

        override fun onStatusChange(isOnline: Boolean) {
            if (isOnline) {
                viewModel.dispatch(Action.GetFavourites)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    (activity as DublinMapperNavigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
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
        view.liveDataList.adapter = adapter
        view.liveDataList.setHasFixedSize(true)
        view.liveDataList.layoutManager = LinearLayoutManager(requireContext())
        listener.subscribe(subscriber)
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
        viewModel.dispatch(Action.GetFavourites)
    }

    private fun renderState(state: State) {
        if (state.favourites != null) {
            adapter?.update(listOf(state.favourites))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        listener.unsubscribe(subscriber)
    }
}
