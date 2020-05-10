package io.dublink.database

import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.model.FavouriteMetadata
import io.rtpi.api.StopLocation

fun createDubLinkStopLocation(
    stopLocation: StopLocation,
    favouriteMetadata: FavouriteMetadata
) = DubLinkStopLocation(
    stopLocation = stopLocation,
    favouriteMetadata = favouriteMetadata
)
