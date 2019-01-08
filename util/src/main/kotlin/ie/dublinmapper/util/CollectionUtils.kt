package ie.dublinmapper.util

import java.util.*

object CollectionUtils {

    fun <T> isNullOrEmpty(collection: Collection<T>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    fun <T> isNotNullOrEmpty(collection: Collection<T>?): Boolean {
        return !isNullOrEmpty(collection)
    }

    fun <K, V> headMap(map: SortedMap<K, V>, limit: Int): SortedMap<K, V> {
        var count = 0
        val headMap = TreeMap<K, V>()
        for ((key, value) in map) {
            if (count >= limit) {
                break
            }
            headMap[key] = value
            count++
        }
        return headMap
    }

    fun <T> safeFirstElement(collection: Collection<T>?): T? {
        if (isNullOrEmpty(collection)) {
            return null
        }
        return collection!!.iterator().next()
    }

    fun <T> toSet(collection: Collection<T>): Set<T> {
        val set = mutableSetOf<T>()
        if (isNullOrEmpty(collection)) {
            return set
        }
        set.addAll(collection)
        return set
    }

    fun doIntersect(collection1: Collection<Any>, collection2: Collection<Any>): Boolean {
        return !Collections.disjoint(collection1, collection2)
    }

}
