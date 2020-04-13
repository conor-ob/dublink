package ie.dublinmapper

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import ie.dublinmapper.domain.internet.InternetStatus
import ie.dublinmapper.livedata.LiveDataFragment
import io.rtpi.api.ServiceLocation
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_root.activity_root

class DublinMapperActivity : DaggerAppCompatActivity(), NavHost, DublinMapperNavigator {

    private val navigationController by lazy { findNavController(R.id.navHostFragment) }
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { viewModelProvider(viewModelFactory) as DublinMapperActivityViewModel }
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
//        navigation.setupWithNavController(navigationController)
//        navigation.setOnNavigationItemSelectedListener { item ->
//            onNavDestinationSelected(item, navigationController)
//        }
//        navigationController.addOnDestinationChangedListener { _, destination, _ ->
//            snackBar?.dismiss()
//            when (destination.id) {
//                R.id.newsFragment,
//                R.id.nearbyFragment,
//                R.id.favouritesFragment,
//                R.id.searchFragment -> navigation.visibility = View.VISIBLE
//                else -> navigation.visibility = View.GONE
//            }
//        }
        viewModel.observableState.observe(
            this, Observer { state ->
                state?.let { renderState(state) }
            }
        )
        viewModel.dispatch(Action.SubscribeToInternetStatusChanges)
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

    override fun navigateToSearch() {
        navigationController.navigate(
            R.id.searchFragment,
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

    private fun renderState(state: State) {
        when (state.internetStatusChange) {
            InternetStatus.ONLINE -> {
                snackBar = Snackbar.make(activity_root, "Back online! \uD83D\uDE0C", Snackbar.LENGTH_LONG)
//                if (navigation.visibility == View.VISIBLE) {
//                    snackBar?.anchorView = navigation
//                }
                snackBar?.setActionTextColor(getColor(R.color.color_on_success))
                snackBar?.setTextColor(getColor(R.color.color_on_success))
                snackBar?.setBackgroundTint(getColor(R.color.color_success))
                snackBar?.show()
            }
            InternetStatus.OFFLINE -> {
                snackBar = Snackbar.make(activity_root, "Offline \uD83D\uDE14", Snackbar.LENGTH_INDEFINITE)
                snackBar?.setAction("Dismiss") {
                    snackBar?.dismiss()
                }
//                if (navigation.visibility == View.VISIBLE) {
//                    snackBar?.anchorView = navigation
//                }
                snackBar?.setActionTextColor(getColor(R.color.color_on_error))
                snackBar?.setTextColor(getColor(R.color.color_on_error))
                snackBar?.setBackgroundTint(getColor(R.color.color_error))
                snackBar?.show()
            }
        }
    }
}
