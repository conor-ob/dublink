package ie.dublinmapper.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CollectionsKtxTest {

    @Test
    fun `sorted maps should truncate to the desired number of entries`() {
        // arrange
        val limit = 3
        val map = sortedMapOf(
            1 to "one",
            2 to "two",
            3 to "three",
            4 to "four",
            5 to "five"
        )

        // act
        val headMap = map.truncateHead(limit)

        // assert
        assertThat(headMap.size).isEqualTo(limit)
    }
}
