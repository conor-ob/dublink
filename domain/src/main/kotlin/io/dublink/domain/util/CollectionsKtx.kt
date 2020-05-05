package io.dublink.domain.util

import java.util.SortedMap
import java.util.TreeMap

fun <K, V> SortedMap<K, V>.truncateHead(limit: Int): SortedMap<K, V> {
    var count = 0
    val headMap = TreeMap<K, V>()
    for ((key, value) in this) {
        if (count >= limit) {
            break
        }
        headMap[key] = value
        count++
    }
    return headMap
}
