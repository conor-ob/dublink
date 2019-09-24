package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import org.threeten.bp.LocalTime
import java.util.*

interface LiveData {

    val operator: Operator

    val customHash: Int

}

interface TimedLiveData : LiveData {

    val dueTime: List<DueTime>

    val route: String

    val destination: String

}

data class AircoachLiveData(
    override val dueTime: List<DueTime>,
    override val operator: Operator,
    override val route: String,
    override val destination: String,
    override val customHash: Int = Objects.hash(operator, route, destination)
) : TimedLiveData

data class BusEireannLiveData(
    override val dueTime: List<DueTime>,
    override val operator: Operator,
    override val route: String,
    override val destination: String,
    override val customHash: Int = Objects.hash(operator, route, destination)
) : TimedLiveData

data class IrishRailLiveData(
    override val dueTime: List<DueTime>,
    override val operator: Operator,
    override val route: String,
    override val destination: String,
    val direction: String
) : TimedLiveData {
    override val customHash: Int = Objects.hash(operator, route, destination, direction)
}

data class DublinBikesLiveData(
    override val operator: Operator,
    override val customHash: Int = Objects.hash(operator),
    val bikes: Int,
    val docks: Int
) : LiveData

data class DublinBusLiveData(
    override val dueTime: List<DueTime>,
    override val operator: Operator,
    override val route: String,
    override val destination: String,
    override val customHash: Int = Objects.hash(operator, route, destination)
) : TimedLiveData

data class LuasLiveData(
    override val dueTime: List<DueTime>,
    override val operator: Operator,
    override val route: String,
    override val destination: String,
    val direction: String
) : TimedLiveData {
    override val customHash: Int = Objects.hash(operator, route, destination, direction)
}

data class SwordsExpressLiveData(
    override val dueTime: List<DueTime>,
    override val operator: Operator,
    override val route: String,
    override val destination: String,
    override val customHash: Int = Objects.hash(operator, route, destination)
) : TimedLiveData

data class DueTime(
    val minutes: Long,
    val time: LocalTime
)

data class LiveDataResponse(
    val service: Service,
    val serviceLocationName: String,
    val liveData: List<LiveData>
)
