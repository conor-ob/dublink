package ie.dublinmapper.domain.model

import org.threeten.bp.LocalTime

sealed class LiveData {

    data class Dart(
        val dueTime: DueTime
    ) : LiveData()

    data class DublinBikes(
        val dummy: Any
    ) : LiveData()

    data class DublinBus(
        val dueTime: DueTime
    ) : LiveData()

    data class Luas(
        val dueTime: DueTime
    ) : LiveData()

}

data class DueTime(
    val minutes: Long,
    val time: LocalTime
)
