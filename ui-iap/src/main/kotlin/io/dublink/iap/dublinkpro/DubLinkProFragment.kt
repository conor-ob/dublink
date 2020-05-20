package io.dublink.iap.dublinkpro

import android.os.Bundle
import android.view.View
import android.widget.updateText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.DubLinkFragment
import io.dublink.iap.BillingConnectionManager
import io.dublink.iap.R
import io.dublink.iap.RxBilling
import io.dublink.viewModelProvider
import javax.inject.Inject

class DubLinkProFragment : DubLinkFragment(R.layout.fragment_dublink_pro) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as DubLinkProViewModel }

    @Inject lateinit var rxBilling: RxBilling

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
        lifecycle.addObserver(BillingConnectionManager(rxBilling))
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
    }

    private fun renderState(state: State) {
        renderBuyButton(state)
    }

    private fun renderBuyButton(state: State) {
        if (state.dubLinkProPrice != null && state.canPurchaseDubLinkPro == true) {
            dubLinkProPriceButton.updateText(newText = getString(R.string.iap_buy_button, state.dubLinkProPrice))
            dubLinkProPriceButton.visibility = View.VISIBLE
        } else {
            dubLinkProPriceButton.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.dispatch(Action.ObservePurchaseUpdates)
        viewModel.dispatch(Action.QuerySkuDetails)
        viewModel.dispatch(Action.QueryPurchases)
        viewModel.dispatch(Action.QueryPurchaseHistory)
    }

    private fun renderFeaturesList() {
        featuresAdapter?.update(
            listOf(
                DubLinkProHeaderItem(),
                DubLinkProDividerItem(),
                DubLinkProFeatureItem(
                    title = "Go anywhere",
                    summary = "Full access to every service\n\n- DART\n- Luas\n- Dublin Bikes\n- Bus Éireann\n- Aircoach\n- Commuter & InterCity Rail"
                ),
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
                DubLinkProSpacerItem()
            )
        )
    }
}
