package ie.dublinmapper.livedata

import com.xwray.groupie.Section
import ie.dublinmapper.domain.usecase.LiveDataResponse
import ie.dublinmapper.model.DublinBikesLiveDataItem
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.DublinBikesLiveData
import io.rtpi.api.TimedLiveData

object LiveDataMapper {

    fun map(response: LiveDataResponse) = Section(
        when (response) {
            is LiveDataResponse.Loading -> emptyList()
            is LiveDataResponse.Skipped -> TODO()
            is LiveDataResponse.Grouped -> TODO()
            is LiveDataResponse.Error -> TODO()
            is LiveDataResponse.Complete -> response.liveData.mapNotNull {
                when (it) {
                    is TimedLiveData -> LiveDataItem(it)
                    is DublinBikesLiveData -> DublinBikesLiveDataItem(it)
                    else -> null
                }
            }
        }
    )
}
