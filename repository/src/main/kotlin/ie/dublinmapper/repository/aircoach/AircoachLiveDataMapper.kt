package ie.dublinmapper.repository.aircoach

import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.aircoach.ServiceJson
import ie.dublinmapper.util.Operator

object AircoachLiveDataMapper : Mapper<ServiceJson, AircoachLiveData> {

    override fun map(from: ServiceJson): AircoachLiveData {
        return AircoachLiveData(
            dueTime = emptyList(),
            operator = Operator.SWORDS_EXPRESS,
            destination = from.linkDate,
            direction = ""
        )
    }

}
