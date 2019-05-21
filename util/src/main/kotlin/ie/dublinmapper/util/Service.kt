package ie.dublinmapper.util

enum class Service(
    val fullName: String,
    val shortName: String
) {

    AIRCOACH("Aircoach", "AC"),
    BUS_EIREANN("Bus Éireann", "BE"),
    DUBLIN_BIKES("Dublin Bikes", "BIKE"),
    DUBLIN_BUS("Dublin Bus", "BAC"),
    IRISH_RAIL("Irish Rail", "IR"),
    LUAS("Luas", "LUAS"),
    SWORDS_EXPRESS("Swords Express", "SE");

}
