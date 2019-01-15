package ie.dublinmapper.util

import org.junit.Assert
import org.junit.Test
import java.util.*

class CollectionUtilsTest {

    @Test
    fun testIntersect() {
        Assert.assertTrue(CollectionUtils.doIntersect(Operator.dublinBus(), Operator.bus()))
        Assert.assertTrue(CollectionUtils.doIntersect(Operator.bus(), Operator.goAhead()))
        Assert.assertTrue(CollectionUtils.doIntersect(Operator.rail(), Operator.interCity()))
        Assert.assertTrue(CollectionUtils.doIntersect(Operator.commuter(), Operator.rail()))
        Assert.assertTrue(CollectionUtils.doIntersect(Operator.rail(), Operator.dart()))
        Assert.assertFalse(CollectionUtils.doIntersect(Operator.rail(), Operator.bus()))
        Assert.assertFalse(CollectionUtils.doIntersect(Operator.tram(), Operator.rail()))
        Assert.assertFalse(CollectionUtils.doIntersect(Operator.dublinBikes(), Operator.dublinBus()))
        Assert.assertFalse(CollectionUtils.doIntersect(Operator.rail(), Operator.goAhead()))
        Assert.assertFalse(CollectionUtils.doIntersect(Operator.luas(), Collections.emptySet()))
    }

}
