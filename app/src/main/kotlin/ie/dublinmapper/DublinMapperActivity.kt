package ie.dublinmapper

import android.os.Bundle
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import dagger.android.support.DaggerAppCompatActivity

class DublinMapperActivity : DaggerAppCompatActivity(), NavHost, Navigator {

    private val navigationController by lazy { findNavController(R.id.navHostFragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
    }

    override fun getNavController() = navigationController

    override fun onSupportNavigateUp() = navigationController.navigateUp()

    override fun navigateFromFavouritesToSearch() {
        navigationController.navigate(R.id.favouritesFragment_to_searchFragment)
    }

}
