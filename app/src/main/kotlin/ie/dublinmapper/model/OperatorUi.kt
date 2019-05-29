package ie.dublinmapper.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import ie.dublinmapper.R

enum class OperatorUi(
    @StringRes val id: Int,
    @StringRes val code: Int,
    @ColorRes val colour: Int
) {

    AIRCOACH(
        id = R.string.operator_aircoach_id,
        code = R.string.operator_aircoach_code,
        colour = R.color.aircoachOrange
    ),
    BUS_EIREANN(
        id = R.string.operator_buseireann_id,
        code = R.string.operator_buseireaann_code,
        colour = R.color.busEireannRed
    ),
    COMMUTER(
        id = R.string.operator_commuter_id,
        code = R.string.operator_commuter_code,
        colour = R.color.commuterBlue
    ),
    DART(
        id = R.string.operator_dart_id,
        code = R.string.operator_dart_code,
        colour = R.color.dartGreen
    ),
    DUBLIN_BIKES(
        id = R.string.operator_dublinbikes_id,
        code = R.string.operator_dublinbikes_code,
        colour = R.color.dublinBikesTeal
    ),
    DUBLIN_BUS(
        id = R.string.operator_dublinbus_id,
        code = R.string.operator_dublinbus_code,
        colour = R.color.dublinBusYellow
    ),
    GO_AHEAD(
        id = R.string.operator_goahead_id,
        code = R.string.operator_goahead_code,
        colour = R.color.goAheadBlue
    ),
    INTERCITY(
        id = R.string.operator_intercity_id,
        code = R.string.operator_intercity_code,
        colour = R.color.intercityGrey
    ),
    LUAS(
        id = R.string.operator_luas_id,
        code = R.string.operator_luas_code,
        colour = R.color.luasPurple
    ),
    SWORDS_EXPRESS(
        id = R.string.operator_swordsexpress_id,
        code = R.string.operator_swordsexpress_code,
        colour = R.color.swordsExpressGreen
    );

}
