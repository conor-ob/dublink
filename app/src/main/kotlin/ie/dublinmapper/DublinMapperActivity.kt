package ie.dublinmapper

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import ie.dublinmapper.view.nearby.NearbyViewModel
import javax.inject.Inject

class DublinMapperActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: NearbyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NearbyViewModel::class.java)
    }

}
