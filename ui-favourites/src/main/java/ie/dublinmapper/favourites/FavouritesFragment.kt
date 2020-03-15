package ie.dublinmapper.favourites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.Navigator
import ie.dublinmapper.model.getServiceLocation
import ie.dublinmapper.ui.DublinMapperFragment
import ie.dublinmapper.ui.viewModelProvider
import io.rtpi.api.Service
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.view.*

class FavouritesFragment : DublinMapperFragment(R.layout.fragment_favourites) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as FavouritesViewModel }

    private lateinit var adapter: GroupAdapter<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    (activity as Navigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }

        adapter = GroupAdapter()
        adapter.setOnItemClickListener { item, _ ->
            val serviceLocation = item.getServiceLocation()
            if (!enabledServiceManager.isServiceEnabled(serviceLocation.service)) {
                enabledServiceManager.enableService(serviceLocation.service)
            }
            if (Service.DUBLIN_BIKES != serviceLocation.service) {
                (activity as Navigator).navigateToLiveData(serviceLocation)
            }
        }
        view.liveDataList.adapter = adapter
        view.liveDataList.setHasFixedSize(true)
        view.liveDataList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.dispatch(Action.GetFavourites)
    }

    private fun renderState(state: State) {
        if (state.favourites != null) {
            adapter.update(listOf(state.favourites))
        }
    }

}
