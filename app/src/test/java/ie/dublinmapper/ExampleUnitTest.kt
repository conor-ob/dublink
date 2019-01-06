package ie.dublinmapper

import com.google.android.gms.maps.model.BitmapDescriptor
import ie.dublinmapper.util.ImageUtils
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testMapMarker() {
        val map = TreeMap<Float, Float>()
        map[16f] = 16f
        map[32f] = 32f

        print(map.floorEntry(0f))
    }

}
