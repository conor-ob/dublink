package io.dublink.repository.location

import com.google.common.truth.Truth.assertThat
import io.dublink.test.createDubLinkDockLocation
import io.dublink.test.createDubLinkStopLocation
import io.rtpi.api.Coordinate
import org.junit.Test

class TwoDimensionalKdTreeTest {

    private val twoDimensionalKdTree = TwoDimensionalKdTree()
    private val currentLocation = Coordinate(53.347247, -6.259077)

    @Test
    fun `should return empty list if empty`() {
        // arrange
        val limit = 0

        // act
        val nearest = twoDimensionalKdTree.getNearest(coordinate = currentLocation, limit = limit).blockingFirst()

        // assert
        assertThat(twoDimensionalKdTree.isEmpty()).isTrue()
        assertThat(nearest).isEmpty()
    }

    @Test
    fun `should be empty if elements are not inserted`() {
        // assert
        assertThat(twoDimensionalKdTree.isEmpty()).isTrue()
    }

    @Test
    fun `should not be empty if elements are inserted`() {
        // arrange
        twoDimensionalKdTree.insert(
            listOf(
                createDubLinkStopLocation()
            )
        )

        // assert
        assertThat(twoDimensionalKdTree.isEmpty()).isFalse()
    }

    @Test
    fun `should return the nearest n elements`() {
        // arrange
        val location1 = createDubLinkStopLocation(coordinate = Coordinate(53.345563, -6.257629))
        val location2 = createDubLinkStopLocation(coordinate = Coordinate(53.348086, -6.248445))
        val location3 = createDubLinkDockLocation(coordinate = Coordinate(53.347888, -6.260644))
        val location4 = createDubLinkStopLocation(coordinate = Coordinate(53.340895, -6.258383))

        // act
        twoDimensionalKdTree.insert(listOf(location1, location2, location3, location4))
        val nearest = twoDimensionalKdTree.getNearest(coordinate = currentLocation, limit = 2).blockingFirst()

        assertThat(nearest).containsExactly(location1, location3)
    }

    @Test
    fun `should be empty if cleared`() {
        // assert
        assertThat(twoDimensionalKdTree.isEmpty()).isTrue()

        // act
        twoDimensionalKdTree.insert(
            listOf(
                createDubLinkStopLocation(),
                createDubLinkDockLocation()
            )
        )

        // assert
        assertThat(twoDimensionalKdTree.isEmpty()).isFalse()

        // act
        twoDimensionalKdTree.clear()

        // assert
        assertThat(twoDimensionalKdTree.isEmpty()).isTrue()
    }
}
