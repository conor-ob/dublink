package ie.dublinmapper.util

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import org.threeten.bp.temporal.ChronoUnit
import java.lang.IllegalStateException
import java.util.*

object TimeUtils {

    private val zoneId: ZoneId by lazy { ZoneId.of(TimeZone.getDefault().id) }
    private val zoneOffset: ZoneOffset by lazy { ZoneOffset.systemDefault().rules.getOffset(Instant.now()) }
    private val parsers: List<DateTimeFormatter> by lazy {
        listOf(
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss VV"),
            DateTimeFormatter.ofPattern("HH:mm")
        )
    }
    private val formatters: List<DateTimeFormatter> by lazy {
        listOf(
            DateTimeFormatter.ofPattern("dd MMM yyyy"),
            DateTimeFormatter.ofPattern("dd MMM"),
            DateTimeFormatter.ofPattern("d MMM, HH:mm")
        )
    }

    private val timestampParser: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun now(): Instant {
        val instant = Instant.now()
        val zonedDateTime = instant.atZone(zoneId)
        return zonedDateTime.toInstant()
    }

    fun toTime(instant: Instant): LocalTime {
        return LocalTime.from(instant.atZone(zoneId))
    }

    fun toInstant(timestamp: String): Instant {
        for (parser in parsers) {
            try {
                return LocalDateTime.parse(timestamp, parser).toInstant(zoneOffset)
            } catch (e: DateTimeParseException) {
//                logger.debug("parser [$parser] failed to parse timestamp [$timestamp]")
            }
        }
        throw IllegalStateException("Unable to parse timestamp [$timestamp]")
    }

    fun timestampToInstant(timestamp: String, formatter: DateTimeFormatter = timestampParser): Instant {
        val localTime = LocalTime.parse(timestamp, formatter)
        val localDate = LocalDate.now(zoneId)
        val localDateTime = LocalDateTime.of(localDate, localTime)
        return localDateTime.toInstant(zoneOffset)
    }

    fun toInstant(dateTime: LocalDateTime): Instant {
        return dateTime.atZone(zoneId).toInstant()
    }

    fun timeBetween(unit: ChronoUnit, earlierInstant: Instant, laterInstant: Instant): Long {
        return unit.between(earlierInstant, laterInstant)
    }

}