package io.dublink

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.service.PreferenceStore
import io.dublink.iap.BillingConnectionManager
import io.dublink.iap.RxBilling
import io.dublink.livedata.LiveDataFragment
import io.dublink.util.setupWithNavController
import io.dublink.web.WebViewFragment
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_root.*
import timber.log.Timber

class DubLinkActivity : DaggerAppCompatActivity(), DubLinkNavigator {

    private var currentNavController: LiveData<NavController>? = null

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { viewModelProvider(viewModelFactory) as DubLinkActivityViewModel }

    @Inject lateinit var preferenceStore: PreferenceStore
    @Inject lateinit var rxBilling: RxBilling
    private lateinit var rxBillingLifecycleObserver: LifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        setContentView(R.layout.activity_root)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
//        navigation.setupWithNavController(
//            listOf(R.id.nearbyFragment, R.id.favouritesFragment, R.id.searchFragment),
//            fragmentManager = supportFragmentManager,
//            containerId = R.id.nav_host_container,
//            intent = intent
//        )
//        navigation.setOnNavigationItemSelectedListener { item ->
//            onNavDestinationSelected(item, navigationController)
//        }
//        navigationController.addOnDestinationChangedListener { _, destination, _ ->
////            snackBar?.dismiss()
//            when (destination.id) {
//                R.id.nearbyFragment,
//                R.id.favouritesFragment,
//                R.id.searchFragment -> navigation.visibility = View.VISIBLE
//                else -> navigation.visibility = View.GONE
//            }
//        }
        rxBillingLifecycleObserver = BillingConnectionManager(rxBilling)
        lifecycle.addObserver(rxBillingLifecycleObserver)
        viewModel.observableState.observe(
            this, Observer { state ->
                state?.let { renderState(state) }
            }
        )
        viewModel.dispatch(DubLinkActivityAction.PreloadData)
        viewModel.dispatch(DubLinkActivityAction.BuildSearchIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation)

        val navGraphIds = listOf(R.navigation.nearby, R.navigation.favourites, R.navigation.search)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
//        controller.observe(this, Observer { navController ->
//            setupActionBarWithNavController(navController)
//        })
        currentNavController = controller

//        bottomNavigationView.selectedItemId = R.id.favourites
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onResume() {
        super.onResume()
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
        viewModel.dispatch(DubLinkActivityAction.QueryPurchases) // TODO this should be called when internet status restored
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(rxBillingLifecycleObserver)
    }

    override fun navigateToSettings() {
//        navigationController.navigate(
//            R.id.settingsFragment,
//            Bundle.EMPTY,
//            NavOptions.Builder()
//                .setEnterAnim(R.anim.nav_default_enter_anim)
//                .setExitAnim(R.anim.nav_default_exit_anim)
//                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
//                .build()
//        )
    }

    override fun navigateToLiveData(serviceLocation: DubLinkServiceLocation) {
        currentNavController!!.value!!.navigate(
            R.id.searchFragment_to_livedata,
            LiveDataFragment.toBundle(serviceLocation)
        )
//        navigationController.navigate(
//            R.id.liveDataFragment,
//            LiveDataFragment.toBundle(serviceLocation),
//            NavOptions.Builder()
//                .setEnterAnim(R.anim.nav_default_enter_anim)
//                .setExitAnim(R.anim.nav_default_exit_anim)
//                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
//                .build()
//        )
    }

    override fun navigateToEditFavourites() {
//        navigationController.navigate(
//            R.id.editFavouritesFragment,
//            Bundle.EMPTY,
//            NavOptions.Builder()
//                .setEnterAnim(R.anim.nav_default_enter_anim)
//                .setExitAnim(R.anim.nav_default_exit_anim)
//                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
//                .build()
//        )
    }

    override fun navigateToIap() {
//        navigationController.navigate(
//            R.id.dubLinkProFragment,
//            Bundle.EMPTY,
//            NavOptions.Builder()
//                .setEnterAnim(R.anim.nav_default_enter_anim)
//                .setExitAnim(R.anim.nav_default_exit_anim)
//                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
//                .build()
//        )
    }

    override fun navigateToWebView(title: String, url: String) {
//        navigationController.navigate(
//            R.id.webViewFragment,
//            WebViewFragment.toBundle(title, url),
//            NavOptions.Builder()
//                .setEnterAnim(R.anim.nav_default_enter_anim)
//                .setExitAnim(R.anim.nav_default_exit_anim)
//                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
//                .build()
//        )
    }

    private fun renderState(state: DubLinkActivityState) {
        // nothing to do
    }
}
