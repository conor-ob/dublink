package ie.dublinmapper.livedata

import com.xwray.groupie.Section
import ie.dublinmapper.model.DublinBikesLiveDataItem
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData

object LiveDataMapper {

    fun map(response: LiveDataPresentationResponse) = Section(
        when (response) {
            is LiveDataPresentationResponse.Data -> response.liveData.mapNotNull {
                when (it) {
                    is PredictionLiveData -> LiveDataItem(it)
                    is DockLiveData -> DublinBikesLiveDataItem(it)
                    else -> null
                }
            }
            is LiveDataPresentationResponse.Error -> TODO()
        }
    )
}
