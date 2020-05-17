package io.dublink.iap

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
import io.dublink.viewModelProvider

class InAppPurchaseFragment : DubLinkFragment(R.layout.fragment_iap) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as InAppPurchaseViewModel }

    private var featuresAdapter: GroupAdapter<GroupieViewHolder>? = null
    private lateinit var featuresList: RecyclerView
    private lateinit var dubLinkProPriceButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.registerInAppPurchaseStatusListener(inAppPurchaseStatusListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Toolbar>(R.id.iap_toolbar).apply {
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
        dubLinkProPriceButton = view.findViewById<MaterialButton>(R.id.iap_dublink_pro_buy_button).apply {
            setOnClickListener {
                viewModel.onBuy(requireActivity())
            }
        }

        renderFeaturesList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.start()
        viewModel.dubLinkProPriceLiveData.observe(
            viewLifecycleOwner, Observer { price -> renderBuyButton(price) }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.unregisterInAppPurchaseStatusListener(inAppPurchaseStatusListener)
    }

    private fun renderFeaturesList() {
        featuresAdapter?.update(
            listOf(
                DubLinkProItem(),
                DividerItem(),
                FeatureItem(
                    title = "Dublin nomad",
                    summary = "Gain access to real time info for all transport services\n- Irish Rail (DART, Commuter and InterCity)\n- Luas\n- Dublin Bikes\n- Bus Éireann\n- Aircoach"
                ),
                FeatureItem(
                    title = "Join the dark side",
                    summary = "Start using the dark theme"
                ),
                FeatureItem(
                    title = "Everything you need in one place",
                    summary = "View real time info for up to 10 places in the favourites screen"
                ),
                FeatureItem(
                    title = "Sit back and relax",
                    summary = "Sort favourites by location so that wherever you're going, simply open the app to get the info you need right away"
                ),
                FeatureItem(
                    title = "VIP",
                    summary = "As DubLink evolves you'll have exclusive access to all new features"
                ),
                SpacerItem()
            )
        )
    }

    private fun renderBuyButton(dubLinkProPrice: String) {
        dubLinkProPriceButton.updateText(newText = "Buy for €2.99")
//        dubLinkProPriceButton.updateText(newText = getString(R.string.iap_buy_button, dubLinkProPrice))
        dubLinkProPriceButton.visibility = View.VISIBLE
    }

    private val inAppPurchaseStatusListener = object : InAppPurchaseStatusListener {

        override fun onPurchaseStarted() {
            dubLinkProPriceButton.visibility = View.GONE
        }

        override fun onPurchaseSuccessful() {
            findNavController().navigateUp()
        }

        override fun onPurchaseError() {

        }
    }
}
