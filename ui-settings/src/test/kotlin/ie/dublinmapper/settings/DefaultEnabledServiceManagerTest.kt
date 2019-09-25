package ie.dublinmapper.settings

import com.google.common.truth.Truth.assertThat
import ie.dublinmapper.util.PreferenceStore
import ie.dublinmapper.util.Service
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class DefaultEnabledServiceManagerTest {

    // arrange
    private val preferenceStore = mockk<PreferenceStore> {
        every { getBoolean(Service.AIRCOACH.name, any()) } returns true
        every { getBoolean(Service.BUS_EIREANN.name, any()) } returns false
        every { getBoolean(Service.DUBLIN_BIKES.name, any()) } returns true
        every { getBoolean(Service.DUBLIN_BUS.name, any()) } returns true
        every { getBoolean(Service.IRISH_RAIL.name, any()) } returns true
        every { getBoolean(Service.LUAS.name, any()) } returns true
        every { getBoolean(Service.SWORDS_EXPRESS.name, any()) } returns false
    }
    private val enabledServiceManager = DefaultEnabledServiceManager(preferenceStore,
        Service.values().associateBy( { it }, { it.name } )
    )

//    private val preferenceStore = object : PreferenceStore {
//
//        private val preferences = mutableMapOf(
//            Service.AIRCOACH.name to true,
//            Service.BUS_EIREANN.name to false,
//            Service.DUBLIN_BIKES.name to true,
//            Service.DUBLIN_BUS.name to true,
//            Service.IRISH_RAIL.name to true,
//            Service.LUAS.name to true,
//            Service.SWORDS_EXPRESS.name to false
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
        val isSwordsExpressEnabled = enabledServiceManager.isServiceEnabled(Service.SWORDS_EXPRESS)

        // assert
        assertThat(isAircoachEnabled).isTrue()
        assertThat(isBusEireannEnabled).isFalse()
        assertThat(isDublinBikesEnabled).isTrue()
        assertThat(isDublinBusEnabled).isTrue()
        assertThat(isIrishRailEnabled).isTrue()
        assertThat(isLuasEnabled).isTrue()
        assertThat(isSwordsExpressEnabled).isFalse()
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
