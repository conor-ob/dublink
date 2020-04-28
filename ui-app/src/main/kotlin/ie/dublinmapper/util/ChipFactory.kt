package ie.dublinmapper.util

import android.content.Context
import android.content.res.ColorStateList
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import ie.dublinmapper.ui.R
import io.rtpi.api.Operator

object ChipFactory {

    fun newRouteChip(context: Context, pair: Pair<Operator, String>): Chip {
        val (operator, route) = pair
        val (textColour, backgroundColour) = mapColour(operator, route)
        return Chip(context).apply {
            setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_DublinMapper_RouteChip))
            setChipBackgroundColorResource(backgroundColour)
            setTextAppearanceResource(R.style.TextAppearance_DublinMapper_Button)
            setTextColor(ColorStateList.valueOf(context.resources.getColor(textColour)))
            text = " $route "
            isAllCaps = false
            isClickable = false
            elevation = 4f.dipToPx(context)
        }
    }

    fun newRouteFilterChip(context: Context, pair: Pair<Operator, String>): Chip {
        val (operator, route) = pair
        val (textColour, backgroundColour) = mapColour(operator, route)
        return Chip(context).apply {
            setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_DublinMapper_RouteFilterChip))
            setChipBackgroundColorResource(backgroundColour)
            setTextAppearanceResource(R.style.TextAppearance_DublinMapper_Button_RouteFilter)
            setTextColor(ColorStateList.valueOf(context.resources.getColor(textColour)))
            text = route
            tag = operator
            isAllCaps = false
            elevation = 4f.dipToPx(context)
            isCheckedIconVisible = false
            setRippleColorResource(android.R.color.white)
        }
    }

    fun mapColour(operator: Operator, route: String): Pair<Int, Int> {
        return when (operator) {
            Operator.AIRCOACH -> Pair(android.R.color.white, R.color.aircoachOrange)
            Operator.BUS_EIREANN -> Pair(android.R.color.white, R.color.busEireannRed)
            Operator.COMMUTER -> Pair(android.R.color.white, R.color.commuterBlue)
            Operator.DART -> Pair(android.R.color.white, R.color.dartGreen)
            Operator.DUBLIN_BIKES -> Pair(android.R.color.white, R.color.dublinBikesTeal)
            Operator.DUBLIN_BUS -> Pair(android.R.color.black, R.color.dublinBusYellow)
            Operator.GO_AHEAD -> Pair(android.R.color.white, R.color.goAheadBlue)
            Operator.INTERCITY -> Pair(android.R.color.black, R.color.intercityYellow)
            Operator.LUAS -> {
                when (route) {
                    "Green", "Green Line" -> Pair(android.R.color.white, R.color.luasGreen)
                    "Red", "Red Line" -> Pair(android.R.color.white, R.color.luasRed)
                    else -> Pair(android.R.color.white, R.color.luasPurple)
                }
            }
        }
    }
}
