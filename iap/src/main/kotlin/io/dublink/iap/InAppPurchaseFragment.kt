package io.dublink.iap

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import io.dublink.DubLinkFragment
import io.dublink.viewModelProvider
import kotlinx.android.synthetic.main.fragment_iap.*

class InAppPurchaseFragment : DubLinkFragment(R.layout.fragment_iap) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as InAppPurchaseViewModel }

//    private var featuresAdapter: GroupAdapter<GroupieViewHolder>? = null
//    private lateinit var featuresList: RecyclerView
//    private lateinit var dubLinkProPriceButton: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iap_toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
//        view.findViewById<Toolbar>(R.id.iap_toolbar).apply {
//            setNavigationOnClickListener {
//                findNavController().navigateUp()
//            }
//        }
//        featuresAdapter = GroupAdapter()
//        featuresList = view.findViewById<RecyclerView>(R.id.features_list).apply {
//            adapter = featuresAdapter
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(requireContext())
//        }
//        dubLinkProPriceButton = view.findViewById<MaterialButton>(R.id.iap_dublink_pro_buy_button).apply {
//            setOnClickListener {
//                viewModel.onBuy(requireActivity())
//            }
//        }
//
//        renderFeaturesList()
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel.observableState.observe(
//            viewLifecycleOwner, Observer { state ->
//                state?.let { renderState(state) }
//            }
//        )
//    }

//    private fun renderState(state: State) {
//        state.dubLinkProPrice?.let {
//            renderBuyButton(it)
//        }
//    }

    override fun onResume() {
        super.onResume()
//        viewModel.start()
//        viewModel.dispatch(Action.Connect)
//        viewModel.start()
        viewModel.dispatch(Action.QuerySkuDetails)
//        viewModel.dispatch(Action.QueryPurchases)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        viewModel.stop()
    }

//    private fun renderFeaturesList() {
//        featuresAdapter?.update(
//            listOf(
//                DubLinkProItem(),
//                DividerItem(),
//                FeatureItem(
//                    title = "Dublin nomad",
//                    summary = "Full access to all services\n- DART\n- Luas\n- Dublin Bikes\n- Bus Éireann\n- Aircoach\n- Commuter & InterCity Rail"
//                ),
//                FeatureItem(
//                    title = "Join the dark side",
//                    summary = "Start using a dark theme"
//                ),
//                FeatureItem(
//                    title = "Everything you need in one place",
//                    summary = "View real time info for up to 10 places in the favourites screen"
//                ),
//                FeatureItem(
//                    title = "Sit back and relax",
//                    summary = "Sort favourites by location so that wherever you're going simply open the app to get the info you need right away"
//                ),
//                FeatureItem(
//                    title = "VIP",
//                    summary = "As DubLink grows you'll have exclusive access to all new features"
//                ),
//                SpacerItem()
//            )
//        )
//    }

//    private fun renderBuyButton(dubLinkProPrice: String) {
//        dubLinkProPriceButton.updateText(newText = getString(R.string.iap_buy_button, dubLinkProPrice))
//        dubLinkProPriceButton.visibility = View.VISIBLE
//    }
}
