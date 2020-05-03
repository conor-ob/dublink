package ie.dublinmapper.livedata

import com.xwray.groupie.Section
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.util.LiveDataFilter
import ie.dublinmapper.model.DublinBikesLiveDataItem
import ie.dublinmapper.model.LiveDataItem
import ie.dublinmapper.model.SimpleMessageItem
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
                    listOf(NoLiveDataItem(id = 1L))
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
