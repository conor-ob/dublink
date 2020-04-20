package ie.dublinmapper.livedata

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.domain.model.getSortedRoutes
import ie.dublinmapper.domain.model.isFavourite
import ie.dublinmapper.setVisibility
import ie.dublinmapper.util.ChipFactory
import ie.dublinmapper.util.dipToPx
import ie.dublinmapper.viewModelProvider
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import kotlinx.android.synthetic.main.fragment_livedata.*

class LiveDataFragment : DublinMapperFragment(R.layout.fragment_livedata) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as LiveDataViewModel }
    private lateinit var args: LiveDataArgs

    private var liveDataAdapter: GroupAdapter<GroupieViewHolder>? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var routeFilterBottomSheetLayoutListener: ViewTreeObserver.OnGlobalLayoutListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = fromBundle(requireArguments())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createServiceLocationView()
        createLiveDataView()
        createRouteFilterView(view)
    }

    override fun onResume() {
        super.onResume()
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
        live_data_bottom_sheet_view_container.viewTreeObserver
            .addOnGlobalLayoutListener(routeFilterBottomSheetLayoutListener)
    }

    override fun onPause() {
        super.onPause()
        live_data_bottom_sheet_view_container.viewTreeObserver
            .removeOnGlobalLayoutListener(routeFilterBottomSheetLayoutListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        liveDataAdapter = null
    }

    private fun createServiceLocationView() {
        live_data_toolbar.apply {
            title = args.serviceLocationName
            subtitle = when (val service = args.serviceLocationService) {
                Service.BUS_EIREANN,
                Service.DUBLIN_BUS -> "${service.fullName} (${args.serviceLocationId})"
                else -> service.fullName
            }
            setNavigationOnClickListener { activity?.onBackPressed() }
            menu.findItem(R.id.action_favourite).setIcon(
                if (args.serviceLocationIsFavourite) {
                    R.drawable.ic_favourite_selected
                } else {
                    R.drawable.ic_favourite_unselected
                }
            )
            setOnMenuItemClickListener { menuItem ->
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
        }
    }

    private fun createLiveDataView() {
        live_data_loader.apply {
            isEnabled = false
            setColorSchemeResources(R.color.color_on_surface)
            setProgressBackgroundColorSchemeResource(R.color.color_surface)
        }
        liveDataAdapter = GroupAdapter()
        live_data_recycler_view.apply {
            adapter = liveDataAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun createRouteFilterView(view: View) {
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.live_data_bottom_sheet_route_filters))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        live_data_button_expand_route_filters.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        live_data_button_collapse_route_filters.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        live_data_button_clear_route_filters.setOnClickListener {
            viewModel.dispatch(Action.RouteFilterIntent(RouteFilterChangeType.Clear))
            live_data_chip_group_route_filters.clearCheck()
        }
        routeFilterBottomSheetLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            bottomSheetBehavior.setPeekHeight(live_data_bottom_sheet_view_container.measuredHeight, true)
        }
    }

    private fun renderState(state: State) {
        renderServiceLocationState(state)
        renderLiveDataState(state)
        renderRouteFilterState(state)
    }

    private fun renderServiceLocationState(state: State) {
        if (state.isFavourite != null) {
            args = args.copy(serviceLocationIsFavourite = state.isFavourite)
            val favouriteMenuItem = live_data_toolbar.menu.findItem(R.id.action_favourite)
            if (state.isFavourite) {
                favouriteMenuItem.setIcon(R.drawable.ic_favourite_selected)
                Toast.makeText(requireContext(), "Saved to Favourites", Toast.LENGTH_SHORT).show()
            } else {
                favouriteMenuItem.setIcon(R.drawable.ic_favourite_unselected)
                Toast.makeText(requireContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderLiveDataState(state: State) {
        live_data_loader.isRefreshing = state.isLoading
        if (state.filteredLiveDataResponse != null) {
            liveDataAdapter?.update(listOf(LiveDataMapper.map(state.filteredLiveDataResponse)))
        }
    }

    private fun renderRouteFilterState(state: State) {
        live_data_button_clear_route_filters.setVisibility(isVisible = state.activeRouteFilters.isNotEmpty())
        if (state.serviceLocationResponse is ServiceLocationPresentationResponse.Data &&
            state.serviceLocationResponse.serviceLocation is StopLocation
        ) {
            val sortedRoutes = state.serviceLocationResponse.serviceLocation.getSortedRoutes()
            if (sortedRoutes.size > 1) {
                live_data_button_expand_route_filters.visibility = View.VISIBLE
            }
            if (sortedRoutes.size != live_data_chip_group_route_filters.childCount) {
                live_data_chip_group_route_filters.removeAllViewsInLayout()
                for ((operator, route) in sortedRoutes) {
                    val routeFilterChip = ChipFactory
                        .newRouteFilterChip(requireContext(), operator to route)
                        .apply {
                            isChecked = state.activeRouteFilters.contains(route)
                            chipStrokeWidth = if (state.activeRouteFilters.contains(route)) {
                                3f.dipToPx(requireContext())
                            } else {
                                0f
                            }
                            setOnCheckedChangeListener(routeFilterClickedListener)
                        }
                    live_data_chip_group_route_filters.addView(routeFilterChip)
                }
                live_data_chip_group_route_filters.visibility = View.VISIBLE
            }
        }
    }

    private val routeFilterClickedListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        (buttonView as Chip).apply {
            chipStrokeWidth = if (isChecked) {
                3f.dipToPx(requireContext())
            } else {
                0f
            }
        }

        val checkedChipIds = live_data_chip_group_route_filters.checkedChipIds
        if (checkedChipIds.isNullOrEmpty()) {
            live_data_button_clear_route_filters.visibility = View.INVISIBLE
        } else {
            live_data_button_clear_route_filters.visibility = View.VISIBLE
        }

        if (isChecked) {
            viewModel.dispatch(
                Action.RouteFilterIntent(
                    RouteFilterChangeType.Add(buttonView.text.toString())
                )
            )
        } else {
            viewModel.dispatch(
                Action.RouteFilterIntent(
                    RouteFilterChangeType.Remove(buttonView.text.toString())
                )
            )
        }
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
