package ie.dublinmapper.repository.dart

import com.google.common.truth.Truth.assertThat
import ie.dublinmapper.service.irishrail.IrishRailStationDataXml
import ie.dublinmapper.util.MockCurrentDateTimeProvider
import ie.dublinmapper.util.TimeUtils
import org.junit.Before
import org.junit.Test

class DartLiveDataMapperTest {

    @Before
    fun setup() {
        TimeUtils.currentDateTimeProvider = MockCurrentDateTimeProvider()
    }

    @Test
    fun `test map live data train starting from query station`() {
        val liveData = DartLiveDataMapper.map(IrishRailStationDataXml(
            expArrival = "00:00",
            schArrival = "00:00",
            dueIn = "88",
            trainType = "DART",
            trainCode = "E263 ",
            destination = "Bray",
            direction = "Southbound"
        ))
        assertThat(liveData.dueTime.size).isEqualTo(1)
        assertThat(liveData.dueTime[0].minutes).isEqualTo(88L)
        assertThat(liveData.destination).isEqualTo("Bray")
        assertThat(liveData.direction).isEqualTo("Southbound")
    }

}
