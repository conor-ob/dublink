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
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.model.getServiceLocation
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.viewModelProvider
import io.rtpi.api.Service
import kotlinx.android.synthetic.main.fragment_nearby.*
import kotlinx.android.synthetic.main.fragment_nearby.view.*

private const val locationRequestCode = 42069

class NearbyFragment : DublinMapperFragment(R.layout.fragment_nearby) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as NearbyViewModel }

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
                    (activity as DublinMapperNavigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }

        adapter = GroupAdapter()
        adapter.setOnItemClickListener { item, _ ->
            val serviceLocation = item.getServiceLocation()
            if (Service.DUBLIN_BIKES != serviceLocation.service) {
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
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(),
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationRequestCode) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED } ) {
                viewModel.dispatch(Action.GetNearbyServiceLocations)
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun renderState(state: State) {
        if (state.serviceLocations != null) {
            adapter.update(listOf(state.serviceLocations))
        }
    }

}