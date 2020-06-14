package io.dublink

import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import dagger.android.support.DaggerAppCompatActivity
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.service.PreferenceStore
import io.dublink.iap.BillingConnectionManager
import io.dublink.iap.RxBilling
import io.dublink.livedata.LiveDataFragment
import io.dublink.web.WebViewFragment
import timber.log.Timber
import javax.inject.Inject

class DubLinkActivity : DaggerAppCompatActivity(), NavHost, DubLinkNavigator {

    private val navigationController by lazy { findNavController(R.id.navHostFragment) }
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { viewModelProvider(viewModelFactory) as DubLinkActivityViewModel }

    @Inject lateinit var preferenceStore: PreferenceStore
    @Inject lateinit var rxBilling: RxBilling
    private lateinit var rxBillingLifecycleObserver: LifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name}")
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
        rxBillingLifecycleObserver = BillingConnectionManager(rxBilling)
        lifecycle.addObserver(rxBillingLifecycleObserver)
        viewModel.observableState.observe(
            this, Observer { state ->
                state?.let { renderState(state) }
            }
        )
        viewModel.dispatch(DubLinkActivityAction.PreloadData)
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

    override fun navigateToLiveData(serviceLocation: DubLinkServiceLocation) {
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

    override fun navigateToEditFavourites() {
        navigationController.navigate(
            R.id.editFavouritesFragment,
            Bundle.EMPTY,
            NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build()
        )
    }

    override fun navigateToIap() {
        navigationController.navigate(
            R.id.dubLinkProFragment,
            Bundle.EMPTY,
            NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build()
        )
    }

    override fun navigateToWebView(title: String, url: String) {
        navigationController.navigate(
            R.id.webViewFragment,
            WebViewFragment.toBundle(title, url),
            NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build()
        )
    }

    private fun renderState(state: DubLinkActivityState) {
        // nothing to do
    }
}
