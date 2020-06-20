package io.dublink

import io.dublink.domain.model.DubLinkServiceLocation

interface DubLinkNavigator {

    fun navigateToLiveData(serviceLocation: DubLinkServiceLocation)

    fun navigateToSettings()

    fun navigateToEditFavourites()

    fun navigateToIap()

    fun navigateToWebView(title: String, url: String)
}
