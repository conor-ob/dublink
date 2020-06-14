package io.dublink.iap.dublinkpro

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.google.common.truth.Truth.assertThat
import io.dublink.domain.service.DubLinkProService
import io.dublink.domain.service.RxScheduler
import io.dublink.iap.DubLinkSku
import io.dublink.iap.InAppPurchaseVerifier
import io.dublink.iap.RxBilling
import io.dublink.test.getOrAwaitValue
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DubLinkProViewModelTest {

    private val rxBilling = mockk<RxBilling>()
    private val viewModel = DubLinkProViewModel(
        useCase = DubLinkProUseCase(
            rxBilling = rxBilling,
            inAppPurchaseVerifier = InAppPurchaseVerifier(
                encodedPublicKey = "XYZ"
            ),
            dubLinkProService = object : DubLinkProService {

                override fun isFreeTrialRunning(): Boolean {
                    return false
                }

                override fun grantDubLinkProAccess() {
                }

                override fun grantDubLinkProTrial() {
                }

                override fun grantDubLinkProPreferences() {
                }

                override fun revokeDubLinkProPreferences() {
                }

                override fun revokeDubLinkProTrial() {
                }
            }
        ),
        rxScheduler = RxScheduler(
            io = Schedulers.trampoline(),
            ui = Schedulers.trampoline()
        )
    )

    @Test
    fun `get sku details displays price`() {
        // arrange
        val skuDetails = SkuDetails(
            """
            {
                "productId":"${DubLinkSku.DUBLINK_PRO.productId}",
                "price":"€2.99"
            }
            """.trimIndent()
        )
        every { rxBilling.getSkuDetails(any()) } returns Single.just(listOf(skuDetails))

        // act
        viewModel.dispatch(Action.QuerySkuDetails)

        // assert
//        assertThat(viewModel.observableState.getOrAwaitValue()).isEqualTo(
//            State(
//                dubLinkProPrice = "€2.99",
//                canPurchaseDubLinkPro = null,
//                dubLinkProPurchased = null,
//                message = null
//            )
//        )
        val state = viewModel.observableState.getOrAwaitValue()
        assertThat(state.dubLinkProPrice).isEqualTo("€2.99")
        assertThat(state.canPurchaseDubLinkPro).isNull()
        assertThat(state.dubLinkProPurchased).isNull()
        assertThat(state.message).isNull()
    }

    @Test
    fun `allow purchase if no purchase on record`() {
        // arrange
        val purchases = emptyList<Purchase>()
        every { rxBilling.getPurchases(BillingClient.SkuType.INAPP) } returns Single.just(purchases)

        // act
        viewModel.dispatch(Action.QueryPurchases)

        // assert
//        assertThat(viewModel.observableState.getOrAwaitValue()).isEqualTo(
//            State(
//                dubLinkProPrice = null,
//                canPurchaseDubLinkPro = true,
//                dubLinkProPurchased = null,
//                message = null
//            )
//        )
        val state = viewModel.observableState.getOrAwaitValue()
        assertThat(state.dubLinkProPrice).isNull()
        assertThat(state.canPurchaseDubLinkPro).isTrue()
        assertThat(state.dubLinkProPurchased).isNull()
        assertThat(state.message).isNull()
    }

    @Test
    fun `block purchase if already purchased`() {
        // arrange
        val purchase = Purchase(
            """
            {
                "productId":"${DubLinkSku.DUBLINK_PRO.productId}"
            }
            """.trimIndent(),
            ""
        )
        every { rxBilling.getPurchases(BillingClient.SkuType.INAPP) } returns Single.just(listOf(purchase))

        // act
        viewModel.dispatch(Action.QueryPurchases)

        // TODO acknowledge purchase
        // assert
//        assertThat(viewModel.observableState.getOrAwaitValue()).isEqualTo(
//            State(
//                dubLinkProPrice = null,
//                canPurchaseDubLinkPro = false,
//                dubLinkProPurchased = null,
//                message = null
//            )
//        )
    }

    @Test
    fun `full integration test`() {
        // arrange
        val skuDetails = SkuDetails(
            """
            {
                "productId":"${DubLinkSku.DUBLINK_PRO.productId}",
                "price":"€2.99"
            }
            """.trimIndent()
        )
        every { rxBilling.getSkuDetails(any()) } returns Single.just(listOf(skuDetails))
        val purchases = emptyList<Purchase>()
        every { rxBilling.getPurchases(BillingClient.SkuType.INAPP) } returns Single.just(purchases)

        // act
        viewModel.dispatch(Action.QuerySkuDetails)
        viewModel.dispatch(Action.QueryPurchases)

        // assert
//        assertThat(viewModel.observableState.getOrAwaitValue()).isEqualTo(
//            State(
//                dubLinkProPrice = "€2.99",
//                canPurchaseDubLinkPro = true,
//                dubLinkProPurchased = null,
//                message = null
//            )
//        )
        val state = viewModel.observableState.getOrAwaitValue()
        assertThat(state.dubLinkProPrice).isEqualTo("€2.99")
        assertThat(state.canPurchaseDubLinkPro).isTrue()
        assertThat(state.dubLinkProPurchased).isNull()
        assertThat(state.message).isNull()
    }
}
