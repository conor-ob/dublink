package io.dublink.iap.dublinkpro

//import com.google.common.truth.Truth.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DubLinkProFragmentTest {

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("io.dublink.iap.test", appContext.packageName)

//        assertThat(appContext.packageName).isEqualTo("io.dublink.iap.test")
    }
}
