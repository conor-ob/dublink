package io.dublink.repository.location

import io.dublink.domain.model.DubLinkServiceLocation
import io.reactivex.Observable
import io.rtpi.api.Coordinate
import net.sf.javaml.core.kdtree.KDTree

class TwoDimensionalKdTree {

    private var delegate: KDTree? = KDTree(2)
    private var isEmpty: Boolean = true

    fun insert(serviceLocations: List<DubLinkServiceLocation>) {
        serviceLocations.forEach { serviceLocation ->
            requireNotNull(delegate).insert(
                doubleArrayOf(
                    serviceLocation.coordinate.latitude,
                    serviceLocation.coordinate.longitude
                ),
                serviceLocation
            )
        }
        isEmpty = false
    }

    fun getNearest(coordinate: Coordinate, limit: Int): Observable<List<DubLinkServiceLocation>> {
        return Observable.just(
            requireNotNull(delegate).nearest(
                doubleArrayOf(
                    coordinate.latitude,
                    coordinate.longitude
                ),
                limit
            ).map { it as DubLinkServiceLocation }
        )
    }

    fun clear() {
        delegate = null
        delegate = KDTree(2)
        isEmpty = true
    }

    fun isEmpty(): Boolean = isEmpty
}
