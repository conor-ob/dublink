package ie.dublinmapper.domain.model

import io.rtpi.api.ServiceLocation

private const val favourite = "key_favourite"
private const val customName = "key_custom_name"

fun ServiceLocation.isFavourite(): Boolean = data[favourite] as Boolean? ?: false

fun ServiceLocation.setFavourite() {
    data[favourite] = true
}

fun ServiceLocation.setCustomName(name: String) {
    data[customName] = name
}

fun ServiceLocation.getName(): String = data[customName] as String? ?: name
