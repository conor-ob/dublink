package ie.dublinmapper.domain.model

import io.rtpi.api.ServiceLocation

private const val favourite = "key_favourite"
private const val customName = "key_custom_name"
private const val customOrder = "key_custom_order"

fun ServiceLocation.isFavourite(): Boolean = properties[favourite] as? Boolean? ?: false

fun ServiceLocation.setFavourite() {
    properties[favourite] = true
}

fun ServiceLocation.setCustomName(name: String) {
    properties[customName] = name
}

fun ServiceLocation.getName(): String = properties[customName] as? String? ?: name

fun ServiceLocation.setOrder(order: Int) {
    properties[customOrder] = order
}

fun ServiceLocation.getOrder(): Int = properties[customOrder] as? Int ?: -1
