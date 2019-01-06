package ie.dublinmapper.util

import java.util.*

object CollectionUtils {

    @JvmStatic
    fun <T> isNullOrEmpty(collection: Collection<T>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    @JvmStatic
    fun <T> isNotNullOrEmpty(collection: Collection<T>?): Boolean {
        return !isNullOrEmpty(collection)
    }

    @JvmStatic
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

    @JvmStatic
    fun <T> safeFirstElement(collection: Collection<T>?): T? {
        if (isNullOrEmpty(collection)) {
            return null
        }
        return collection!!.iterator().next()
    }

    @JvmStatic
    fun <T> toSet(collection: Collection<T>): Set<T> {
        val set = mutableSetOf<T>()
        if (isNullOrEmpty(collection)) {
            return set
        }
        set.addAll(collection)
        return set
    }

}
