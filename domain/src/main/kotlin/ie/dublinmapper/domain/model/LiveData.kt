package ie.dublinmapper.domain.model

import ie.dublinmapper.util.Operator
import org.threeten.bp.LocalTime

sealed class LiveData {

    data class Dart(
        val dueTime: DueTime,
        val operator: Operator,
        val destination: String,
        val direction: String
    ) : LiveData()

    data class DublinBikes(
        val bikes: Int,
        val docks: Int,
        val operator: Operator
    ) : LiveData()

    data class DublinBus(
        val dueTime: DueTime,
        val operator: Operator,
        val route: String,
        val destination: String
    ) : LiveData()

    data class Luas(
        val dueTime: DueTime,
        val operator: Operator,
        val route: String,
        val destination: String
    ) : LiveData()

}

data class DueTime(
    val minutes: Long,
    val time: LocalTime
)
