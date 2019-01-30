package ie.dublinmapper.util

import java.util.*

enum class Operator(
    val fullName: String,
    val shortName: String
) {

    COMMUTER("Commuter", "COMM"),
    DART("DART", "DART"),
    DUBLIN_BIKES("Dublin Bikes", "BIKE"),
    DUBLIN_BUS("Dublin Bus", "BAC"),
    GO_AHEAD("Go Ahead", "GAD"),
    INTERCITY("InterCity", "ICTY"),
    LUAS("Luas", "LUAS"),
    SWORDS_EXPRESS("Swords Express", "SE");

    companion object {

        fun commuter(): EnumSet<Operator> = EnumSet.of(COMMUTER)

        fun dart(): EnumSet<Operator> = EnumSet.of(DART)

        fun dublinBikes(): EnumSet<Operator> = EnumSet.of(DUBLIN_BIKES)

        fun dublinBus(): EnumSet<Operator> = EnumSet.of(DUBLIN_BUS)

        fun goAhead(): EnumSet<Operator> = EnumSet.of(GO_AHEAD)

        fun interCity(): EnumSet<Operator> = EnumSet.of(INTERCITY)

        fun luas(): EnumSet<Operator> = EnumSet.of(LUAS)

        fun swordsExpress(): EnumSet<Operator> = EnumSet.of(SWORDS_EXPRESS)

        fun bike(): EnumSet<Operator> = EnumSet.of(DUBLIN_BIKES)

        fun bus(): EnumSet<Operator> = EnumSet.of(DUBLIN_BUS, GO_AHEAD, SWORDS_EXPRESS)

        fun rail(): EnumSet<Operator> = EnumSet.of(COMMUTER, DART, INTERCITY)

        fun tram(): EnumSet<Operator> = EnumSet.of(LUAS)

    }

    override fun toString(): String {
        return fullName
    }

}
