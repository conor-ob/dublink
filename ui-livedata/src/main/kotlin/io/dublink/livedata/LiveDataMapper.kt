package io.dublink.livedata

import com.xwray.groupie.Section
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.util.LiveDataFilter
import io.dublink.model.DublinBikesLiveDataItem
import io.dublink.model.LiveDataItem
import io.dublink.model.SimpleMessageItem
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData

object LiveDataMapper {

    fun map(
        response: LiveDataPresentationResponse,
        serviceLocation: DubLinkServiceLocation?
    ) = Section(
        when (response) {
            is LiveDataPresentationResponse.Data -> {
                val items = LiveDataFilter.filterLiveData(serviceLocation, response.liveData).mapNotNull {
                    when (it) {
                        is PredictionLiveData -> LiveDataItem(it)
                        is DockLiveData -> DublinBikesLiveDataItem(it)
                        else -> null
                    }
                }
                if (items.isNullOrEmpty()) {
                    listOf(
                        NoLiveDataItem(
                            service = serviceLocation?.service,
                            id = 1L
                        )
                    )
                } else {
                    items
                }
            }
            is LiveDataPresentationResponse.Error -> listOf(
                SimpleMessageItem(response.throwable.message ?: "error", 1L)
            )
        }
    )
}
