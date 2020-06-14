package io.dublink.iap.dublinkpro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.updateText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.DubLinkFragment
import io.dublink.domain.service.DubLinkProService
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.ThemeService
import io.dublink.iap.BillingConnectionManager
import io.dublink.iap.R
import io.dublink.iap.RxBilling
import io.dublink.viewModelProvider
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_dublink_pro.*

class DubLinkProFragment : DubLinkFragment(R.layout.fragment_dublink_pro) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as DubLinkProViewModel }

    @Inject lateinit var dubLinkProService: DubLinkProService
    @Inject lateinit var preferenceStore: PreferenceStore
    @Inject lateinit var themeService: ThemeService
    @Inject lateinit var rxBilling: RxBilling
    private lateinit var rxBillingLifecycleObserver: LifecycleObserver

    private var featuresAdapter: GroupAdapter<GroupieViewHolder>? = null
    private lateinit var featuresList: RecyclerView
    private lateinit var dubLinkProPriceButton: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Toolbar>(R.id.dublink_pro_toolbar).apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        featuresAdapter = GroupAdapter()
        featuresList = view.findViewById<RecyclerView>(R.id.features_list).apply {
            adapter = featuresAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        dubLinkProPriceButton = view.findViewById<MaterialButton>(R.id.dublink_pro_buy_button).apply {
            setOnClickListener {
                viewModel.dispatch(Action.BuyDubLinkPro(requireActivity()))
            }
        }

        renderFeaturesList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rxBillingLifecycleObserver = BillingConnectionManager(rxBilling)
        lifecycle.addObserver(rxBillingLifecycleObserver)
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.dispatch(Action.ObservePurchaseUpdates)
        viewModel.dispatch(Action.QuerySkuDetails)
        viewModel.dispatch(Action.QueryPurchases)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(rxBillingLifecycleObserver)
    }

    private fun renderState(state: State) {
        renderBuyButton(state)
        renderMessage(state.message)
        renderPurchase(state.dubLinkProPurchased)
    }

    private fun renderBuyButton(state: State) {
        if (state.dubLinkProPrice != null && state.canPurchaseDubLinkPro == true) {
            dubLinkProPriceButton.updateText(newText = "Test purchase") // TODO reset this
//            dubLinkProPriceButton.updateText(newText = getString(R.string.iap_buy_button, state.dubLinkProPrice))
            dubLinkProPriceButton.visibility = View.VISIBLE
        } else {
            dubLinkProPriceButton.visibility = View.GONE
        }
    }

    private fun renderMessage(errorMessage: String?) {
        if (errorMessage != null) {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun renderPurchase(dubLinkProPurchased: Boolean?) {
        if (dubLinkProPurchased == true) {
            findNavController().navigateUp()
        }
    }

    private fun renderFeaturesList() {
        featuresAdapter?.update(
            listOfNotNull(
                DubLinkProHeaderItem(),
                DubLinkProDividerItem(),
                DubLinkProFeatureItem(
                    title = "Join the dark side",
                    summary = "Start using a dark theme"
                ),
                DubLinkProFeatureItem(
                    title = "Everything you need in one place",
                    summary = "View real time info for up to 10 places in the favourites screen"
                ),
                DubLinkProFeatureItem(
                    title = "Sit back and relax",
                    summary = "Sort favourites by location so that wherever you're going simply open the app to get the info you need right away"
                ),
                DubLinkProFeatureItem(
                    title = "VIP",
                    summary = "As DubLink grows you'll have exclusive access to all new features"
                ),
                if (dubLinkProService.isFreeTrialRunning()) {
                    null
                } else {
                    DubLinkProFeatureItem(
                        title = "Free Trial",
                        summary = "Want to try before you buy? Please note that pro features will reset when you exit the app",
                        tryItListener = object : TryItListener {

                            override fun onTryItClicked() {
                                dubLinkProService.grantDubLinkProTrial()
                            }
                        }
                    )
                },
                DubLinkProSpacerItem()
            )
        )
    }

    override fun attachSnackBarToView(text: String, length: Int): Snackbar? {
        return Snackbar.make(dublink_pro_layout_root, text, length)
    }

    override fun onInternetRestored() {
        super.onInternetRestored()
        viewModel.dispatch(Action.QuerySkuDetails)
        viewModel.dispatch(Action.QueryPurchases)
    }
}
