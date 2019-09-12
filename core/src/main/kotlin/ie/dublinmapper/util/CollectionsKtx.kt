package ie.dublinmapper.util

import java.util.*

fun <T> Collection<T>.intersectWith(other: Collection<T>): Boolean {
    return !Collections.disjoint(this, other)
}
