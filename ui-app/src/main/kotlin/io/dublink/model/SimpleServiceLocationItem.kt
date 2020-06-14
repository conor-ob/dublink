package io.dublink.model

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.ui.R

open class SimpleServiceLocationItem(
    serviceLocation: DubLinkServiceLocation,
    walkDistance: Double?
) : AbstractServiceLocationItem(serviceLocation, walkDistance) {

    override fun getLayout() = R.layout.list_item_simple_service_location
}
