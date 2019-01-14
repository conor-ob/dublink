package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Operator
import org.threeten.bp.LocalTime
import java.util.*

sealed class LiveData(
    open val customHash: Int = 0
) {

    data class Dart(
        val dueTime: List<DueTime>,
        val operator: Operator,
        val destination: String,
        val direction: String
    ) : LiveData(
        Objects.hash(operator, destination, direction)
    )

    data class DublinBikes(
        val bikes: Int,
        val docks: Int,
        val operator: Operator
    ) : LiveData()

    data class DublinBus(
        val dueTime: List<DueTime>,
        val operator: Operator,
        val route: String,
        val destination: String
    ) : LiveData(
        Objects.hash(operator, route, destination)
    )

    data class Luas(
        val dueTime: List<DueTime>,
        val operator: Operator,
        val route: String,
        val destination: String
    ) : LiveData(
        Objects.hash(operator, route, destination)
    )

}

data class DueTime(
    val minutes: Long,
    val time: LocalTime
)
