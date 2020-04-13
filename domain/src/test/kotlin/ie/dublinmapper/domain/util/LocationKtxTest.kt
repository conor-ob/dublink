package ie.dublinmapper.domain.util

import com.google.common.collect.Range
import com.google.common.truth.Truth.assertThat
import io.rtpi.api.Coordinate
import org.junit.Test

class LocationKtxTest {

    @Test
    fun `haversine distance should be accurate to within 10 metres`() {
        // arrange
        val stephensGreen = Coordinate(latitude = 53.338160, longitude = -6.259133)
        val killineyHill = Coordinate(latitude = 53.265562, longitude = -6.111817)

        // act
        val distance = stephensGreen.haversine(killineyHill)

        // assert
        assertThat(distance).isIn(Range.closed(12685.0, 12695.0))
    }
}
