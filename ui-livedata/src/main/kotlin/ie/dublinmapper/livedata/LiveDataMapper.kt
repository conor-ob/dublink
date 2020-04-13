package ie.dublinmapper.livedata

import com.xwray.groupie.Section
import ie.dublinmapper.model.DublinBikesLiveDataItem
import ie.dublinmapper.model.LiveDataItem
import ie.dublinmapper.model.NoLiveDataItem
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData

object LiveDataMapper {

    fun map(response: LiveDataPresentationResponse) = Section(
        when (response) {
            is LiveDataPresentationResponse.Data -> {
                val items = response.liveData.mapNotNull {
                    when (it) {
                        is PredictionLiveData -> LiveDataItem(it)
                        is DockLiveData -> DublinBikesLiveDataItem(it)
                        else -> null
                    }
                }
                if (items.isNullOrEmpty()) {
                    listOf(NoLiveDataItem())
                } else {
                    items
                }
            }
            is LiveDataPresentationResponse.Error -> TODO()
        }
    )
}
