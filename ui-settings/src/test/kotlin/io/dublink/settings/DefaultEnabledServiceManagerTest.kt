package io.dublink.settings

import com.google.common.truth.Truth.assertThat
import io.dublink.domain.service.PreferenceStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.rtpi.api.Service
import org.junit.Test

class DefaultEnabledServiceManagerTest {

    // arrange
    private val preferenceStore = mockk<PreferenceStore> {
        every { isServiceEnabled(Service.AIRCOACH) } returns false
        every { isServiceEnabled(Service.BUS_EIREANN) } returns false
        every { isServiceEnabled(Service.DUBLIN_BIKES) } returns true
        every { isServiceEnabled(Service.DUBLIN_BUS) } returns true
        every { isServiceEnabled(Service.IRISH_RAIL) } returns true
        every { isServiceEnabled(Service.LUAS) } returns false

        every { setServiceEnabled(any()) } returns true
    }
    private val enabledServiceManager = DefaultEnabledServiceManager(preferenceStore)

    @Test
    fun `checking if a service is enabled should delegate to value stored in preferences`() {
        // act & assert
        assertThat(enabledServiceManager.isServiceEnabled(Service.AIRCOACH)).isFalse()
        assertThat(enabledServiceManager.isServiceEnabled(Service.BUS_EIREANN)).isFalse()
        assertThat(enabledServiceManager.isServiceEnabled(Service.DUBLIN_BIKES)).isTrue()
        assertThat(enabledServiceManager.isServiceEnabled(Service.DUBLIN_BUS)).isTrue()
        assertThat(enabledServiceManager.isServiceEnabled(Service.IRISH_RAIL)).isTrue()
        assertThat(enabledServiceManager.isServiceEnabled(Service.LUAS)).isFalse()
        verify(exactly = Service.values().size) {
            preferenceStore.isServiceEnabled(any())
        }
    }

    @Test
    fun `checking all enabled services should check every value stored in preferences`() {
        // act
        val enabledServices = enabledServiceManager.getEnabledServices()

        // assert
        assertThat(enabledServices).containsExactly(
            Service.IRISH_RAIL,
            Service.DUBLIN_BIKES,
            Service.DUBLIN_BUS
        ).inOrder()
        verify(exactly = Service.values().size) {
            preferenceStore.isServiceEnabled(any())
        }
    }

    @Test
    fun `enabling a service should delegate to preferences`() {
        // act
        enabledServiceManager.enableService(Service.LUAS)

        // assert
        verify(exactly = 1) {
            preferenceStore.setServiceEnabled(Service.LUAS)
        }
    }
}
