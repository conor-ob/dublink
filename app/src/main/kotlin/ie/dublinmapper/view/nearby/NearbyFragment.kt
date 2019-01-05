package ie.dublinmapper.view.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import ie.dublinmapper.R
import ie.dublinmapper.domain.dublinbikes.DublinBikesDock
import kotlinx.android.synthetic.main.fragment_nearby.*
import javax.inject.Inject

class NearbyFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: NearbyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NearbyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNearbyServiceLocations().observe(this, Observer<List<DublinBikesDock>>{ users ->
            text_view.text = users.toString()
        })
    }

}
