package ie.dublinmapper.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ServiceLocationTest {

    @Test
    fun `test the thing`() {
        val luasStop = createLuasStop()
        assertThat(luasStop.isFavourite()).isFalse()
        assertThat(luasStop.getName()).isEqualTo("St. Stephen's Green")

        luasStop.setFavourite()
        assertThat(luasStop.isFavourite()).isTrue()
        assertThat(luasStop.getName()).isEqualTo("St. Stephen's Green")

        luasStop.setCustomName("My Favourite Luas Stop")
        assertThat(luasStop.isFavourite()).isTrue()
        assertThat(luasStop.getName()).isEqualTo("My Favourite Luas Stop")
    }
}
