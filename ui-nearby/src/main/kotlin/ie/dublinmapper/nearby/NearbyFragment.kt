package ie.dublinmapper.nearby

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.model.extractServiceLocation
import ie.dublinmapper.viewModelProvider
import kotlinx.android.synthetic.main.fragment_nearby.toolbar
import kotlinx.android.synthetic.main.fragment_nearby.view.nearbyLocations

private const val locationRequestCode = 42069

class NearbyFragment : DublinMapperFragment(R.layout.fragment_nearby) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as NearbyViewModel }

    private var adapter: GroupAdapter<GroupieViewHolder>? = null

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
                (activity as DublinMapperNavigator).navigateToLiveData(serviceLocation)
            }
        }
        view.nearbyLocations.adapter = adapter
        view.nearbyLocations.setHasFixedSize(true)
        view.nearbyLocations.layoutManager = LinearLayoutManager(requireContext())
//        view.nearbyLocations.addItemDecoration(
//            DividerItemDecoration(
//                view.nearbyLocations.context,
//                DividerItemDecoration.VERTICAL
//            )
//        )

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            viewModel.dispatch(Action.GetNearbyServiceLocations)
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationRequestCode
            )
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationRequestCode) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                viewModel.dispatch(Action.GetNearbyServiceLocations)
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun renderState(state: State) {
        if (state.serviceLocations != null) {
            adapter?.update(listOf(state.serviceLocations))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}
