package ie.dublinmapper.settings

import com.google.common.truth.Truth.assertThat
import ie.dublinmapper.domain.service.PreferenceStore
import io.mockk.every
import io.mockk.mockk
import io.rtpi.api.Service
import org.junit.Test

class DefaultEnabledServiceManagerTest {

    // arrange
    private val preferenceStore = mockk<PreferenceStore> {
        every { isServiceEnabled(Service.AIRCOACH) } returns true
        every { isServiceEnabled(Service.BUS_EIREANN) } returns false
        every { isServiceEnabled(Service.DUBLIN_BIKES) } returns true
        every { isServiceEnabled(Service.DUBLIN_BUS) } returns true
        every { isServiceEnabled(Service.IRISH_RAIL) } returns true
        every { isServiceEnabled(Service.LUAS) } returns true
    }
    private val enabledServiceManager = DefaultEnabledServiceManager(preferenceStore)

//    private val preferenceStore = object : PreferenceStore {
//
//        private val preferences = mutableMapOf(
//            Service.AIRCOACH.name to true,
//            Service.BUS_EIREANN.name to false,
//            Service.DUBLIN_BIKES.name to true,
//            Service.DUBLIN_BUS.name to true,
//            Service.IRISH_RAIL.name to true,
//            Service.LUAS.name to true
//        )
//
//        override fun getBoolean(preferenceKey: String, defaultValue: Boolean): Boolean {
//            return preferences.getValue(preferenceKey)
//        }
//
//        override fun setBoolean(preferenceKey: String, value: Boolean): Boolean {
//            return preferences.put(preferenceKey, value)!!
//        }
//
//    }

    @Test
    fun isServiceEnabled() {
        // act
        val isAircoachEnabled = enabledServiceManager.isServiceEnabled(Service.AIRCOACH)
        val isBusEireannEnabled = enabledServiceManager.isServiceEnabled(Service.BUS_EIREANN)
        val isDublinBikesEnabled = enabledServiceManager.isServiceEnabled(Service.DUBLIN_BIKES)
        val isDublinBusEnabled = enabledServiceManager.isServiceEnabled(Service.DUBLIN_BUS)
        val isIrishRailEnabled = enabledServiceManager.isServiceEnabled(Service.IRISH_RAIL)
        val isLuasEnabled = enabledServiceManager.isServiceEnabled(Service.LUAS)

        // assert
        assertThat(isAircoachEnabled).isTrue()
        assertThat(isBusEireannEnabled).isFalse()
        assertThat(isDublinBikesEnabled).isTrue()
        assertThat(isDublinBusEnabled).isTrue()
        assertThat(isIrishRailEnabled).isTrue()
        assertThat(isLuasEnabled).isTrue()
    }

    @Test
    fun getEnabledServices() {
        // act
        val enabledServices = enabledServiceManager.getEnabledServices()

        // assert
        assertThat(enabledServices).containsExactly(
            Service.AIRCOACH,
            Service.DUBLIN_BIKES,
            Service.DUBLIN_BUS,
            Service.IRISH_RAIL,
            Service.LUAS
        )
    }
}
