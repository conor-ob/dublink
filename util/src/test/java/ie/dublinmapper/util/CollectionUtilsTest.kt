package ie.dublinmapper.util

import org.junit.Test
import java.util.*

class CollectionUtilsTest {

    @Test
    fun testContainsAny() {
        val test1 = !Collections.disjoint(Operator.dublinBus(), Operator.bus())
        val test2 = !Collections.disjoint(Operator.bus(), Operator.dublinBus())

        val test3 = Operator.dublinBus().stream().anyMatch { Operator.bus().contains(it) }
        val test4 = Operator.bus().stream().anyMatch { Operator.dublinBus().contains(it) }

        val test5 = !Collections.disjoint(Operator.rail(), Operator.bus())
        val test6 = !Collections.disjoint(Operator.tram(), Operator.rail())
    }

}
