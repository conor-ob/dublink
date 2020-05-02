package ie.dublinmapper.livedata

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.updateSubtitle
import androidx.appcompat.widget.updateTitle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.dialog.CustomizeFavouriteDialogFactory
import ie.dublinmapper.dialog.OnFavouriteSavedListener
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.model.DubLinkStopLocation
import ie.dublinmapper.domain.model.Filter
import ie.dublinmapper.setVisibility
import ie.dublinmapper.util.ChipFactory
import ie.dublinmapper.viewModelProvider
import io.rtpi.api.Service
import kotlinx.android.synthetic.main.fragment_livedata.*
import kotlinx.android.synthetic.main.layout_live_data_route_filter_bottom_sheet.*

class LiveDataFragment : DublinMapperFragment(R.layout.fragment_livedata) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as LiveDataViewModel }
    private lateinit var args: LiveDataArgs
    private lateinit var serviceLocation: DubLinkServiceLocation

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
        viewModel.onResume()
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
        viewModel.onPause()
        live_data_bottom_sheet_view_container.viewTreeObserver
            .removeOnGlobalLayoutListener(routeFilterBottomSheetLayoutListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        liveDataAdapter = null
    }

    private fun createServiceLocationView() {
        live_data_toolbar.apply {
            setNavigationOnClickListener { activity?.onBackPressed() }
//            menu.findItem(R.id.action_favourite).setIcon(
//                if (args.serviceLocationIsFavourite) {
//                    R.drawable.ic_favourite_selected
//                } else {
//                    R.drawable.ic_favourite_unselected
//                }
//            )
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_favourite -> {
                        if (serviceLocation.isFavourite) {
                            viewModel.dispatch(
                                Action.RemoveFavourite(
                                    serviceLocationId = args.serviceLocationId,
                                    serviceLocationService = args.serviceLocationService
                                )
                            )
                        } else {
                            CustomizeFavouriteDialogFactory.newDialog(
                                context = requireContext(),
                                activity = requireActivity(),
                                serviceLocation = serviceLocation,
                                onFavouriteSavedListener = object : OnFavouriteSavedListener {

                                    override fun onSave(serviceLocation: DubLinkServiceLocation) {
                                        viewModel.dispatch(Action.SaveFavourite(serviceLocation))
                                    }
                                }
                            ).show()
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
        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(bottomSheetCallback)
        }
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
            viewModel.dispatch(Action.FilterIntent(FilterChangeType.Clear))
            live_data_chip_group_route_filters.clearCheck()
        }
        routeFilterBottomSheetLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            bottomSheetBehavior.setPeekHeight(live_data_bottom_sheet_view_container.measuredHeight)
        }
    }

    private fun renderState(state: State) {
        if (state.toastMessage != null) {
            Toast.makeText(requireContext(), state.toastMessage, Toast.LENGTH_SHORT).show()
        }
        renderServiceLocationState(state)
        renderLiveDataState(state)
        renderRouteFilterState(state)
    }

    private fun renderServiceLocationState(state: State) {
        if (state.serviceLocation != null) {
            serviceLocation = state.serviceLocation
            live_data_toolbar.apply {
                updateTitle(newText = serviceLocation.name)
                updateSubtitle(
                    newText = when (val service = args.serviceLocationService) {
                        Service.BUS_EIREANN,
                        Service.DUBLIN_BUS -> "${service.fullName} (${args.serviceLocationId})"
                        else -> service.fullName
                    }
                )
                menu.findItem(R.id.action_favourite).apply {
                    if (serviceLocation.isFavourite) {
                        setIcon(R.drawable.ic_favourite_selected)
                    } else {
                        setIcon(R.drawable.ic_favourite_unselected)
                    }
                }
            }
        }
    }

    private fun renderLiveDataState(state: State) {
        live_data_loader.isRefreshing = state.isLoading
        if (state.liveDataResponse != null) {
            liveDataAdapter?.update(listOf(LiveDataMapper.map(state.liveDataResponse, state.serviceLocation)))
        }
    }

    private fun renderRouteFilterState(state: State) {
        live_data_button_clear_route_filters.setVisibility(
            isVisible = if (state.serviceLocation is DubLinkStopLocation) {
                state.serviceLocation.filters.any { it.isActive }
            } else {
                false
            }
        )
        if (state.serviceLocation != null && state.serviceLocation is DubLinkStopLocation) {
            val filters = state.serviceLocation.filters
            if (filters.size > 1) {
                live_data_button_expand_route_filters.visibility = View.VISIBLE
            }
            if (0 == live_data_chip_group_route_filters.childCount) {
                live_data_chip_group_route_filters.removeAllViewsInLayout()
                for (filter in filters) {
                    val filterChip = ChipFactory.newFilterChip(requireContext(), filter).apply {
                        isChecked = filter.isActive
                        alpha = if (filter.isActive) {
                            1.0f
                        } else {
                            0.33f
                        }
                        setOnCheckedChangeListener(routeFilterClickedListener)
                    }
                    live_data_chip_group_route_filters.addView(filterChip)
                }
                live_data_chip_group_route_filters.visibility = View.VISIBLE
            }
        }
        if (state.routeFilterState != null) {
            bottomSheetBehavior.state = state.routeFilterState
        }
    }

    private val routeFilterClickedListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        (buttonView as Chip).apply {
            alpha = if (isChecked) {
                1.0f
            } else {
                0.33f
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
                Action.FilterIntent(
                    FilterChangeType.Add(buttonView.tag as Filter)
                )
            )
        } else {
            viewModel.dispatch(
                Action.FilterIntent(
                    FilterChangeType.Remove(buttonView.tag as Filter)
                )
            )
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // nothing
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> viewModel.dispatch(Action.RouteFilterSheetMoved(BottomSheetBehavior.STATE_HIDDEN))
                BottomSheetBehavior.STATE_COLLAPSED -> viewModel.dispatch(Action.RouteFilterSheetMoved(BottomSheetBehavior.STATE_COLLAPSED))
                BottomSheetBehavior.STATE_EXPANDED -> viewModel.dispatch(Action.RouteFilterSheetMoved(BottomSheetBehavior.STATE_EXPANDED))
                else -> {}
            }
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
            serviceLocation: DubLinkServiceLocation
        ) = Bundle().apply {
            putString(id, serviceLocation.id)
            putString(name, serviceLocation.name)
            putSerializable(service, serviceLocation.service)
            putBoolean(isFavourite, serviceLocation.isFavourite)
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
