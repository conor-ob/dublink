package ie.dublinmapper.nearby

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import ie.dublinmapper.ui.DublinMapperFragment
import ie.dublinmapper.ui.viewModelProvider
import kotlinx.android.synthetic.main.fragment_nearby.*

private const val locationRequestCode = 42069

class NearbyFragment : DublinMapperFragment(R.layout.fragment_nearby) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as NearbyViewModel }

    override fun styleId() = R.style.IrishRailTheme //TODO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            locationRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationRequestCode) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED } ) {
                viewModel.dispatch(Action.GetLocation)
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun renderState(state: State) {
        if (state.location != null) {
            location.text = state.location.toString()
        }
    }

}
