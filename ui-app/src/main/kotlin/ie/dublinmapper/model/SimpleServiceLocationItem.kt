package ie.dublinmapper.model

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.ui.R

class SimpleServiceLocationItem(
    serviceLocation: DubLinkServiceLocation,
    icon: Int,
    walkDistance: Double?
) : AbstractServiceLocationItem(serviceLocation, icon, walkDistance) {

    override fun getLayout() = R.layout.list_item_simple_service_location
}
