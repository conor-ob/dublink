package ie.dublinmapper.util

import androidx.test.core.app.ApplicationProvider
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.Month
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.zone.ZoneRulesProvider

class TimeUtilsTest {

    companion object {

        @BeforeClass
        @JvmStatic
        fun setup() {
            AndroidThreeTen.init(ApplicationProvider.getApplicationContext())
            val zoneIds = ZoneRulesProvider.getAvailableZoneIds()
            Assert.assertFalse(zoneIds.isEmpty())
        }

    }

    @Test
    fun testParseDartTimestamp() {
        val expectedArrivalTimestamp = "20:21"
        val currentDateTime = LocalDateTime.of(2019, Month.JANUARY, 11, 20, 5, 32)
        val currentInstant = TimeUtils.toInstant(currentDateTime)
        val expectedInstant = TimeUtils.timestampToInstant(expectedArrivalTimestamp, Formatter.hourMinute)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        val expectedTime = LocalTime.from(expectedInstant)
        Assert.assertEquals(16L, minutes)
        Assert.assertEquals(expectedTime.hour, 20)
        Assert.assertEquals(expectedTime.minute, 21)
    }

}
