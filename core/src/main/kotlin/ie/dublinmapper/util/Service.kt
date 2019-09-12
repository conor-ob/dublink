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

    override fun toString(): String {
        return fullName
    }

    companion object {

        fun parse(value: String): Service {
            for (operator in values()) {
                if (operator.name.equals(value, ignoreCase = true)
                    || operator.fullName.equals(value, ignoreCase = true)
                    || operator.shortName.equals(value, ignoreCase = true)) {
                    return operator
                }
            }
            throw IllegalArgumentException("Unable to parse Service from string value: $value")
        }

    }

}
