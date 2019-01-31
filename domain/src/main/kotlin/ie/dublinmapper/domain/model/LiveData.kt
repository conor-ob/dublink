package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Operator
import org.threeten.bp.LocalTime
import java.util.*

sealed class LiveData(
    open val customHash: Int = 0
)

data class AircoachLiveData(
    val dueTime: List<DueTime>,
    val operator: Operator,
    val route: String,
    val destination: String
) : LiveData(
    Objects.hash(operator, route, destination)
)

data class DartLiveData(
    val dueTime: List<DueTime>,
    val operator: Operator,
    val destination: String,
    val direction: String
) : LiveData(
    Objects.hash(operator, destination, direction)
)

data class DublinBikesLiveData(
    val bikes: Int,
    val docks: Int,
    val operator: Operator
) : LiveData()

data class DublinBusLiveData(
    val dueTime: List<DueTime>,
    val operator: Operator,
    val route: String,
    val destination: String
) : LiveData(
    Objects.hash(operator, route, destination)
)

data class LuasLiveData(
    val dueTime: List<DueTime>,
    val operator: Operator,
    val route: String,
    val destination: String
) : LiveData(
    Objects.hash(operator, route, destination)
)

data class SwordsExpressLiveData(
    val placeHolder: Any
)

data class DueTime(
    val minutes: Long,
    val time: LocalTime
)
