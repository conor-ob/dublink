package ie.dublinmapper

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerAppCompatActivity
import ie.dublinmapper.domain.dublinbikes.DublinBikesDock
import ie.dublinmapper.view.nearby.NearbyViewModel
import kotlinx.android.synthetic.main.activity_root.*
import javax.inject.Inject

class DublinMapperActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: NearbyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NearbyViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNearbyServiceLocations().observe(this, Observer<List<DublinBikesDock>>{ users ->
            text_view.text = users.toString()
        })
    }

}
