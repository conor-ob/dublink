package ie.dublinmapper.util

import org.threeten.bp.*

class MockCurrentDateTimeProvider : CurrentDateTimeProvider {

    override val zoneId: ZoneId = ZoneId.of("UTC")
    override val zoneOffset: ZoneOffset = ZoneOffset.UTC

    override fun getCurrentInstant(): Instant {
        //2019-06-21T12:00:00Z
        return Instant.ofEpochMilli(1561118400000L)
    }

    override fun getCurrentDate(): LocalDate {
        return LocalDateTime.ofInstant(getCurrentInstant(), ZoneOffset.UTC).toLocalDate()
    }

}
