package io.dublink

import io.dublink.domain.model.DubLinkServiceLocation

interface DubLinkNavigator {

    fun navigateToLiveData(serviceLocation: DubLinkServiceLocation)

    fun navigateToSearch()

    fun navigateToSettings()

    fun navigateToEditFavourites()

    fun navigateToWebView(title: String, url: String)
}
