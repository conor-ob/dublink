package ie.dublinmapper.livedata

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.viewModelProvider
import kotlinx.android.synthetic.main.bottom_sheet_route_filters.*
import timber.log.Timber

class RouteFilterFragment : DublinMapperFragment(R.layout.bottom_sheet_route_filters) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as LiveDataViewModel }

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

        clearRouteFilters.setOnClickListener {

        }

        val bottomSheetBehavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(view.findViewById(R.id.routeFilterBottomSheet))

        collapseRouteFilters.setOnClickListener {
//            viewModel.dispatch(Action.CollapseRouteFilters)
        }
    }

    private fun renderState(state: State) {
        Timber.d(state.toString())
    }
}