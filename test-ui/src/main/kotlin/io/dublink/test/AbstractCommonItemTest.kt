package io.dublink.test

import com.google.common.truth.Truth.assertThat
import com.xwray.groupie.kotlinandroidextensions.Item
import org.junit.Test

abstract class AbstractCommonItemTest<T : Item> {

    // arrange
    protected abstract val item1: T
    protected abstract val item2: T

    @Test
    fun itemsShouldAlwaysBeEqual() {
        // act & assert
        assertThat(item1).isEqualTo(item2)
    }

    @Test
    fun itemsShouldAlwaysHaveTheSameHashCode() {
        // act & assert
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode())
    }

    @Test
    fun itemsAreUniqueBasedOnTheirId() {
        // act & assert
        assertThat(item1.isSameAs(item2)).isFalse()
        assertThat(item1.isSameAs(item1)).isTrue()
        assertThat(item2.isSameAs(item2)).isTrue()
    }

    /**
     * Annotate this function with @Test
     */
    abstract fun ignored()
}
