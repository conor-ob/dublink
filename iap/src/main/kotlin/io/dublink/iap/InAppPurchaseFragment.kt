package io.dublink.iap

import android.os.Bundle
import android.view.View
import android.widget.updateText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import io.dublink.DubLinkFragment
import io.dublink.viewModelProvider

class InAppPurchaseFragment : DubLinkFragment(R.layout.fragment_iap) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as InAppPurchaseViewModel }

    private lateinit var dubLinkProPriceButton: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Toolbar>(R.id.iap_toolbar).apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        dubLinkProPriceButton = view.findViewById<MaterialButton>(R.id.iap_dublink_pro_buy_button).apply {
            setOnClickListener {
                viewModel.onBuy(requireActivity())
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.start()
        viewModel.dubLinkProPriceLiveData.observe(
            viewLifecycleOwner, Observer { price -> renderBuyButton(price) }
        )
    }

    private fun renderBuyButton(dubLinkProPrice: String) {
        dubLinkProPriceButton.updateText(newText = getString(R.string.iap_buy_button, dubLinkProPrice))
        dubLinkProPriceButton.visibility = View.VISIBLE
    }
}
