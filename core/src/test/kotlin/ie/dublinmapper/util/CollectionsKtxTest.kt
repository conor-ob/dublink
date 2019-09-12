package ie.dublinmapper.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*

class CollectionsKtxTest {

    @Test
    fun testIntersect() {
        assertThat(Operator.dublinBus().intersectWith(Operator.bus())).isTrue()
        assertThat(Operator.dublinBus().intersectWith(Operator.bus())).isTrue()
        assertThat(Operator.bus().intersectWith(Operator.goAhead())).isTrue()
        assertThat(Operator.rail().intersectWith(Operator.interCity())).isTrue()
        assertThat(Operator.commuter().intersectWith(Operator.rail())).isTrue()
        assertThat(Operator.rail().intersectWith(Operator.dart())).isTrue()
        assertThat(Operator.rail().intersectWith(Operator.bus())).isFalse()
        assertThat(Operator.tram().intersectWith(Operator.rail())).isFalse()
        assertThat(Operator.dublinBikes().intersectWith(Operator.dublinBus())).isFalse()
        assertThat(Operator.rail().intersectWith(Operator.goAhead())).isFalse()
        assertThat(Operator.luas().intersectWith(Collections.emptySet())).isFalse()
    }

}
