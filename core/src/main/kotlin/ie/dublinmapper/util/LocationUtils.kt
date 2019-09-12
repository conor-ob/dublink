package ie.dublinmapper.util

object LocationUtils {

    private const val EARTH_RADIUS = 6371001

    @JvmStatic
    fun haversineDistance(coordinate1: Coordinate, coordinate2: Coordinate): Double {
        val dLat = Math.toRadians(coordinate2.latitude - coordinate1.latitude)
        val dLong = Math.toRadians(coordinate2.longitude - coordinate1.longitude)

        val startLat = Math.toRadians(coordinate1.latitude)
        val endLat = Math.toRadians(coordinate2.latitude)

        val a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(
            dLong
        )
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return EARTH_RADIUS * c
    }

    @JvmStatic
    private fun haversine(value: Double): Double {
        return Math.pow(Math.sin(value / 2), 2.0)
    }

}
