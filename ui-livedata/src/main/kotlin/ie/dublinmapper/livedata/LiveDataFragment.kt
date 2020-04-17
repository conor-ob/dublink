package ie.dublinmapper.livedata

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.domain.model.isFavourite
import ie.dublinmapper.util.ChipFactory
import ie.dublinmapper.viewModelProvider
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import io.rtpi.util.AlphaNumericComparator
import kotlinx.android.synthetic.main.fragment_livedata.*

class LiveDataFragment : DublinMapperFragment(R.layout.fragment_livedata) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as LiveDataViewModel }

    private var adapter: GroupAdapter<GroupieViewHolder>? = null
    private lateinit var args: LiveDataArgs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = fromBundle(requireArguments())

        loader.setColorSchemeResources(R.color.color_on_surface)
        loader.setProgressBackgroundColorSchemeResource(R.color.color_surface)

        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        if (args.serviceLocationIsFavourite) {
            val favouriteMenuItem = toolbar.menu.findItem(R.id.action_favourite)
            favouriteMenuItem.setIcon(R.drawable.ic_favourite_selected)
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_favourite -> {
                    if (args.serviceLocationIsFavourite) {
                        viewModel.dispatch(
                            Action.RemoveFavourite(
                                serviceLocationId = args.serviceLocationId,
                                serviceLocationService = args.serviceLocationService
                            )
                        )
                    } else {
                        viewModel.dispatch(
                            Action.SaveFavourite(
                                serviceLocationId = args.serviceLocationId,
                                serviceLocationName = args.serviceLocationName,
                                serviceLocationService = args.serviceLocationService
                            )
                        )
                    }
                    return@setOnMenuItemClickListener true
                }
                R.id.action_settings -> {
                    (activity as DublinMapperNavigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }
        toolbar.title = args.serviceLocationName
        toolbar.subtitle = getSubtitle()

        loader.isEnabled = false

        adapter = GroupAdapter()
        liveDataList.adapter = adapter
        liveDataList.setHasFixedSize(true)
        liveDataList.layoutManager = LinearLayoutManager(requireContext())
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
        viewModel.dispatch(
            Action.GetServiceLocation(
                serviceLocationId = args.serviceLocationId,
                serviceLocationService = args.serviceLocationService
            )
        )
        viewModel.dispatch(
            Action.GetLiveData(
                serviceLocationId = args.serviceLocationId,
                serviceLocationName = args.serviceLocationName,
                serviceLocationService = args.serviceLocationService
            )
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.unbindActions()
    }

    private fun getSubtitle() = when (val service = args.serviceLocationService) {
        Service.BUS_EIREANN,
        Service.DUBLIN_BUS -> "${service.fullName} (${args.serviceLocationId})"
        else -> service.fullName
    }

    private fun renderState(state: State) {
        loader.isRefreshing = state.isLoading
        if (state.isFavourite != null && state.isFavourite) {
            args = args.copy(serviceLocationIsFavourite = true)
            val favouriteMenuItem = toolbar.menu.findItem(R.id.action_favourite)
            favouriteMenuItem.setIcon(R.drawable.ic_favourite_selected)
            Toast.makeText(requireContext(), "Saved to Favourites", Toast.LENGTH_SHORT).show()
        } else if (state.isFavourite != null && !state.isFavourite) {
            args = args.copy(serviceLocationIsFavourite = false)
            val favouriteMenuItem = toolbar.menu.findItem(R.id.action_favourite)
            favouriteMenuItem.setIcon(R.drawable.ic_favourite_unselected)
            Toast.makeText(requireContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show()
        }

        if (state.serviceLocationResponse != null &&
            state.serviceLocationResponse is ServiceLocationPresentationResponse.Data &&
            state.serviceLocationResponse.serviceLocation is StopLocation &&
            state.serviceLocationResponse.serviceLocation.routeGroups.flatMap { it.routes }.size != routes.childCount
        ) {
            routes.removeAllViewsInLayout()
            val sortedRouteGroups = state.serviceLocationResponse.serviceLocation.routeGroups
                .flatMap { routeGroup -> routeGroup.routes.map { routeGroup.operator to it } }
                .sortedWith(Comparator { o1, o2 -> AlphaNumericComparator.compare(o1.second, o2.second) })
            for (route in sortedRouteGroups) {
                routes.addView(ChipFactory.newRouteChip(requireContext(), route))
            }
            routes.visibility = View.VISIBLE
        }

        if (state.liveDataResponse != null) {
            adapter?.update(listOf(LiveDataMapper.map(state.liveDataResponse)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {

        private const val id = "serviceLocationId"
        private const val name = "serviceLocationName"
        private const val service = "serviceLocationService"
        private const val isFavourite = "serviceLocationIsFavourite"

        data class LiveDataArgs(
            val serviceLocationId: String,
            val serviceLocationName: String,
            val serviceLocationService: Service,
            val serviceLocationIsFavourite: Boolean
        )

        fun toBundle(
            serviceLocation: ServiceLocation
        ) = Bundle().apply {
            putString(id, serviceLocation.id)
            putString(name, serviceLocation.getName())
            putSerializable(service, serviceLocation.service)
            putBoolean(isFavourite, serviceLocation.isFavourite())
        }

        private fun fromBundle(
            bundle: Bundle
        ) = LiveDataArgs(
            serviceLocationId = requireNotNull(bundle.getString(id)),
            serviceLocationName = requireNotNull(bundle.getString(name)),
            serviceLocationService = bundle.getSerializable(service) as Service,
            serviceLocationIsFavourite = bundle.getBoolean(isFavourite)
        )
    }
}
