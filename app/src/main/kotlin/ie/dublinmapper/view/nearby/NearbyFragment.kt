package ie.dublinmapper.view.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.android.support.DaggerFragment
import ie.dublinmapper.R
import kotlinx.android.synthetic.main.fragment_nearby.*
import javax.inject.Inject

class NearbyFragment : DaggerFragment(), OnMapReadyCallback, Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: NearbyViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(NearbyViewModel::class.java)
    }
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        toolbar.apply {
            inflateMenu(R.menu.nearby)
            setOnMenuItemClickListener(this@NearbyFragment)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (view == null || googleMap == null) {
            return
        }
        this.googleMap = googleMap
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search -> {
                val action = NearbyFragmentDirections.onSearchPressed()
                findNavController().navigate(action)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}
