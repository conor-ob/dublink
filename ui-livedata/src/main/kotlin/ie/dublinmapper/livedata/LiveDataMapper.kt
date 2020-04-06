package ie.dublinmapper.livedata

import com.xwray.groupie.Section
import ie.dublinmapper.model.DublinBikesLiveDataItem
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.DublinBikesLiveData
import io.rtpi.api.TimedLiveData

object LiveDataMapper {

    fun map(response: LiveDataPresentationResponse) = Section(
        when (response) {
            is LiveDataPresentationResponse.Data -> response.liveData.mapNotNull {
                when (it) {
                    is TimedLiveData -> LiveDataItem(it)
                    is DublinBikesLiveData -> DublinBikesLiveDataItem(it)
                    else -> null
                }
            }
            is LiveDataPresentationResponse.Error -> TODO()
        }
    )
}
