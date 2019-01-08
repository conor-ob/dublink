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
    LUAS("Luas", "LUAS");

    companion object {

        fun commuter(): EnumSet<Operator> {
            return EnumSet.of(COMMUTER)
        }

        fun dart(): EnumSet<Operator> {
            return EnumSet.of(DART)
        }

        fun dublinBikes(): EnumSet<Operator> {
            return EnumSet.of(DUBLIN_BIKES)
        }

        fun dublinBus(): EnumSet<Operator> {
            return EnumSet.of(DUBLIN_BUS)
        }

        fun goAhead(): EnumSet<Operator> {
            return EnumSet.of(GO_AHEAD)
        }

        fun interCity(): EnumSet<Operator> {
            return EnumSet.of(INTERCITY)
        }

        fun luas(): EnumSet<Operator> {
            return EnumSet.of(LUAS)
        }

        fun bike(): EnumSet<Operator> {
            return EnumSet.of(DUBLIN_BIKES)
        }

        fun bus(): EnumSet<Operator> {
            return EnumSet.of(DUBLIN_BUS, GO_AHEAD)
        }

        fun rail(): EnumSet<Operator> {
            return EnumSet.of(COMMUTER, DART, INTERCITY)
        }

        fun tram(): EnumSet<Operator> {
            return EnumSet.of(LUAS)
        }

    }

}
