package ie.dublinmapper

import android.os.Bundle
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import dagger.android.support.DaggerAppCompatActivity

class DublinMapperActivity : DaggerAppCompatActivity(), NavHost {

    private val navigationController by lazy { findNavController(R.id.nav_host_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
    }

    override fun getNavController() = navigationController

    override fun onSupportNavigateUp() = navigationController.navigateUp()

}
