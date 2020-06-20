package io.dublink.nearby

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.util.AppConstants
import io.dublink.domain.util.LiveDataFilter
import io.dublink.model.DockLiveDataItem
import io.dublink.model.GroupedLiveDataItem
import io.dublink.model.SimpleMessageItem
import io.dublink.model.SimpleServiceLocationItem
import io.dublink.model.StopLocationItem
import io.rtpi.api.DockLiveData
import io.rtpi.api.LiveData
import io.rtpi.api.PredictionLiveData
import io.rtpi.util.LiveDataGrouper

object NearbyMapper {

    fun map(serviceLocation: DubLinkServiceLocation, liveData1: List<LiveData>?): Group {
        return Section(
            listOfNotNull(
                SimpleServiceLocationItem(serviceLocation, null),
                if (liveData1 == null) {
                    SimpleMessageItem("Loading...", 1L)
                } else {
                    val liveData = LiveDataGrouper.groupLiveData(LiveDataFilter.filterLiveData(serviceLocation, liveData1))
                    if (liveData.size == 1 && liveData.first().size == 1 && liveData.first().first() is DockLiveData) {
                        Section(
                            DockLiveDataItem(
                                liveData.first().first() as DockLiveData
                            )
                        )
                    } else {
                        val items = mutableListOf<GroupedLiveDataItem>()
                        for (thing in liveData.take(AppConstants.favouritesLiveDataLimit)) {
                            items.add(GroupedLiveDataItem(thing.take(AppConstants.favouritesGroupedLiveDataLimit) as List<PredictionLiveData>))
                        }
                        Section(items)
                    }
                }
            )
        )
    }
}
