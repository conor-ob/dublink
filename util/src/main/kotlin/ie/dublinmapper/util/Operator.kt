package ie.dublinmapper.util

import java.text.ParseException
import java.util.*

enum class Operator(
    val fullName: String,
    val shortName: String
) {

    AIRCOACH("Aircoach", "AC"),
    BUS_EIREANN("Bus Éireann", "BE"),
    COMMUTER("Commuter", "COMM"),
    DART("DART", "DART"),
    DUBLIN_BIKES("Dublin Bikes", "BIKE"),
    DUBLIN_BUS("Dublin Bus", "BAC"),
    GO_AHEAD("Go Ahead", "GAD"),
    INTERCITY("InterCity", "ICTY"),
    LUAS("Luas", "LUAS"),
    SWORDS_EXPRESS("Swords Express", "SE");

    companion object {

        fun aircoach(): EnumSet<Operator> = EnumSet.of(AIRCOACH)

        fun busEireann(): EnumSet<Operator> = EnumSet.of(BUS_EIREANN)

        fun commuter(): EnumSet<Operator> = EnumSet.of(COMMUTER)

        fun dart(): EnumSet<Operator> = EnumSet.of(DART)

        fun dublinBikes(): EnumSet<Operator> = EnumSet.of(DUBLIN_BIKES)

        fun dublinBus(): EnumSet<Operator> = EnumSet.of(DUBLIN_BUS)

        fun goAhead(): EnumSet<Operator> = EnumSet.of(GO_AHEAD)

        fun interCity(): EnumSet<Operator> = EnumSet.of(INTERCITY)

        fun luas(): EnumSet<Operator> = EnumSet.of(LUAS)

        fun swordsExpress(): EnumSet<Operator> = EnumSet.of(SWORDS_EXPRESS)

        fun bike(): EnumSet<Operator> = EnumSet.of(DUBLIN_BIKES)

        fun bus(): EnumSet<Operator> = EnumSet.of(AIRCOACH, BUS_EIREANN, DUBLIN_BUS, GO_AHEAD, SWORDS_EXPRESS)

        fun rail(): EnumSet<Operator> = EnumSet.of(COMMUTER, DART, INTERCITY)

        fun tram(): EnumSet<Operator> = EnumSet.of(LUAS)

        fun parse(value: String): Operator {
            for (operator in values()) {
                if (operator.fullName.equals(value, ignoreCase = true)
                    || operator.shortName.equals(value, ignoreCase = true)) {
                    return operator
                }
            }
            throw IllegalArgumentException("Unable to parse Operator from string value: $value")
        }

    }

    override fun toString(): String {
        return fullName
    }

}
