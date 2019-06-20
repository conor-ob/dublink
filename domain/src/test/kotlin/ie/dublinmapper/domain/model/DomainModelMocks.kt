package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Operator
import org.threeten.bp.LocalTime

fun createAircoachLiveData(
    dueTime: List<DueTime> = listOf(DueTime(minutes = 5L, time = LocalTime.of(10, 10))),
    operator: Operator = Operator.AIRCOACH,
    route: String = "700",
    destination: String = "Dublin Airport Terminal 1"
) : LiveData {
    return AircoachLiveData(
        dueTime = dueTime,
        operator = operator,
        route = route,
        destination = destination
    )
}
