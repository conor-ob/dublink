package ie.dublinmapper.domain.dublinbikes

data class DublinBikesDock(
    val id: String,
    val name: String,
    val docks: Int,
    val bikes: Int,
    val availableDocks: Int
)
