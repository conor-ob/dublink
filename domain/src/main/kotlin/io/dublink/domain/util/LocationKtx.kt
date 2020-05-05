package io.dublink.domain.util

import io.rtpi.api.Coordinate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS = 6371001

fun Coordinate.haversine(coordinate: Coordinate): Double {
    val dLat = Math.toRadians(coordinate.latitude - this.latitude)
    val dLong = Math.toRadians(coordinate.longitude - this.longitude)

    val startLat = Math.toRadians(this.latitude)
    val endLat = Math.toRadians(coordinate.latitude)

    val a = haversine(dLat) + cos(startLat) * cos(endLat) * haversine(
        dLong
    )
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return EARTH_RADIUS * c
}

private fun haversine(value: Double): Double {
    return sin(value / 2).pow(2.0)
}
