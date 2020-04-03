package ie.dublinmapper

import android.os.Bundle
import android.view.View
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import ie.dublinmapper.domain.internet.InternetStatusMonitor
import ie.dublinmapper.domain.internet.InternetStatusSubscriber
import ie.dublinmapper.livedata.LiveDataFragment
import io.rtpi.api.ServiceLocation
import kotlinx.android.synthetic.main.activity_root.*
import javax.inject.Inject

class DublinMapperActivity : DaggerAppCompatActivity(), NavHost, DublinMapperNavigator {

    private val navigationController by lazy { findNavController(R.id.navHostFragment) }
    @Inject lateinit var listener: InternetStatusMonitor
    private var snackBar: Snackbar? = null
    private val subscriber = object : InternetStatusSubscriber {

        override fun online() {
            snackBar = Snackbar.make(activity_root, "Back online! \uD83D\uDE0C", Snackbar.LENGTH_LONG)
            if (navigation.visibility == View.VISIBLE) {
                snackBar?.anchorView = navigation
            }
            snackBar?.setActionTextColor(getColor(R.color.colorOnError))
            snackBar?.setTextColor(getColor(R.color.colorOnError))
            snackBar?.setBackgroundTint(getColor(R.color.colorSuccess))
            snackBar?.show()
        }

        override fun offline() {
            snackBar = Snackbar.make(activity_root, "Offline", Snackbar.LENGTH_INDEFINITE)
            snackBar?.setAction("Dismiss") {
                snackBar?.dismiss()
            }
            if (navigation.visibility == View.VISIBLE) {
                snackBar?.anchorView = navigation
            }
            snackBar?.setActionTextColor(getColor(R.color.colorOnError))
            snackBar?.setTextColor(getColor(R.color.colorOnError))
            snackBar?.setBackgroundTint(getColor(R.color.colorError))
            snackBar?.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        navigation.setupWithNavController(navigationController)
        navigation.setOnNavigationItemSelectedListener { item ->
            onNavDestinationSelected(item, navigationController)
        }
        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            snackBar?.dismiss()
            when (destination.id) {
                R.id.newsFragment,
                R.id.nearbyFragment,
                R.id.favouritesFragment,
                R.id.searchFragment -> navigation.visibility = View.VISIBLE
                else -> navigation.visibility = View.GONE
            }
        }
        listener.subscribe(subscriber)
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.unsubscribe(subscriber)
    }

    override fun getNavController() = navigationController

    override fun onSupportNavigateUp() = navigationController.navigateUp()

    override fun navigateToSettings() {
        navigationController.navigate(
            R.id.settingsFragment,
            Bundle.EMPTY,
            NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build()
        )
    }

    override fun navigateToLiveData(serviceLocation: ServiceLocation) {
        navigationController.navigate(
            R.id.liveDataFragment,
            LiveDataFragment.toBundle(serviceLocation),
            NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build()
        )
    }
}
