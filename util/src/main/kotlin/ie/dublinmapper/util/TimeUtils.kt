package ie.dublinmapper.util

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object TimeUtils {

    var currentDateTimeProvider: CurrentDateTimeProvider = DefaultCurrentDateTimeProvider()
//    private val parsers: List<DateTimeFormatter> by lazy {
//        listOf(
//            DateTimeFormatter.ISO_DATE_TIME,
//            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
//            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss VV"),
//            DateTimeFormatter.ofPattern("HH:mm")
//        )
//    }
//    private val formatters: List<DateTimeFormatter> by lazy {
//        listOf(
//            DateTimeFormatter.ofPattern("dd MMM yyyy"),
//            DateTimeFormatter.ofPattern("dd MMM"),
//            DateTimeFormatter.ofPattern("d MMM, HH:mm")
//        )
//    }

    fun now(): Instant {
        return currentDateTimeProvider.getCurrentInstant()
    }

    fun toTime(instant: Instant): LocalTime {
        return LocalTime.from(instant.atZone(currentDateTimeProvider.zoneId))
    }

    fun dateTimeStampToInstant(timestamp: String, formatter: DateTimeFormatter): Instant {
        return LocalDateTime.parse(timestamp, formatter).toInstant(currentDateTimeProvider.zoneOffset)
    }

    fun timestampToInstant(timestamp: String, formatter: DateTimeFormatter): Instant {
        val localTime = LocalTime.parse(timestamp, formatter)
        val localDate = currentDateTimeProvider.getCurrentDate()
        val localDateTime = LocalDateTime.of(localDate, localTime)
        return localDateTime.toInstant(currentDateTimeProvider.zoneOffset)
    }

    fun toInstant(dateTime: LocalDateTime): Instant {
        return dateTime.atZone(currentDateTimeProvider.zoneId).toInstant()
    }

    fun timeBetween(unit: ChronoUnit, earlierInstant: Instant, laterInstant: Instant): Long {
        return unit.between(earlierInstant, laterInstant)
    }

    @JvmStatic
    fun toIso8601Timestamp(timestamp: String, dateTime: DateTimeFormatter): String {
        val instant = dateTimeStampToInstant(timestamp, dateTime)
        return Formatter.isoDateTime.format(ZonedDateTime.ofInstant(instant, currentDateTimeProvider.zoneId))
    }

    @JvmStatic
    fun formatAsTime(instant: Instant): String {
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString()
    }

}

interface CurrentDateTimeProvider {

    val zoneId: ZoneId

    val zoneOffset: ZoneOffset

    fun getCurrentInstant(): Instant

    fun getCurrentDate(): LocalDate

}

class DefaultCurrentDateTimeProvider : CurrentDateTimeProvider {

    override val zoneId: ZoneId by lazy { ZoneId.of(TimeZone.getDefault().id) }

    override val zoneOffset: ZoneOffset by lazy { ZoneOffset.systemDefault().rules.getOffset(Instant.now()) }

    override fun getCurrentInstant(): Instant {
        val instant = Instant.now()
        val zonedDateTime = instant.atZone(zoneId)
        return zonedDateTime.toInstant()
    }

    override fun getCurrentDate(): LocalDate {
        return LocalDate.now(zoneId)
    }

}

object Formatter {

    val isoDateTime: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    val dateTime: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    val dateTimeAlt: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val zonedDateTime: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss VV")
//    val dayMonthYear: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val hourMinuteSecond: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val hourMinute: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//    val dayMonth: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM")
//    val dayMonthTime: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM, HH:mm")

}
