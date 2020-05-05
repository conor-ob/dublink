package ie.dublinmapper.model

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.ui.R

class SimpleServiceLocationItem(
    serviceLocation: DubLinkServiceLocation,
    walkDistance: Double?
) : AbstractServiceLocationItem(serviceLocation, walkDistance) {

    override fun getLayout() = R.layout.list_item_simple_service_location
}
