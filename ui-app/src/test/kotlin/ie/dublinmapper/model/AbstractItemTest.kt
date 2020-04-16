package ie.dublinmapper.model

import com.google.common.truth.Truth.assertThat
import com.xwray.groupie.kotlinandroidextensions.Item
import org.junit.Test

abstract class AbstractItemTest<T : Item> {

    protected abstract val controlItem: T
    protected abstract val sameItemNotEqual: T
    protected abstract val equalItem: T

    @Test
    fun `identical items should pass equality and hash code checks`() {
        // assert
        assertThat(controlItem).isEqualTo(equalItem)
        assertThat(controlItem == equalItem).isTrue()
        assertThat(controlItem.hashCode()).isEqualTo(equalItem.hashCode())
    }

    @Test
    fun `items can be the same but not equal`() {
        // assert
        assertThat(controlItem.isSameAs(sameItemNotEqual))
        assertThat(controlItem == sameItemNotEqual).isFalse()
        assertThat(controlItem.hashCode()).isNotEqualTo(sameItemNotEqual.hashCode())
    }
}
