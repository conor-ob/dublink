package io.dublink.util

import android.content.Context
import android.content.res.ColorStateList
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import io.dublink.domain.model.Filter
import io.dublink.ui.R
import io.rtpi.api.Operator

object ChipFactory {

    fun newChip(context: Context, filter: Filter): Chip {
        return when (filter) {
            is Filter.RouteFilter -> {
                val (textColour, backgroundColour) = mapColour(filter.route.operator, filter.route.id)
                Chip(context).apply {
                    setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_DubLink_RouteChip))
                    setChipBackgroundColorResource(backgroundColour)
                    setTextAppearanceResource(R.style.TextAppearance_DubLink_Button)
                    setTextColor(ColorStateList.valueOf(context.resources.getColor(textColour)))
                    text = " ${filter.route.id} "
                    tag = filter
                    isAllCaps = false
                    isClickable = false
                    elevation = 2f.dipToPx(context)
                }
            }
            is Filter.DirectionFilter -> {
                Chip(context).apply {
                    setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_DubLink_RouteChip))
                    setChipBackgroundColorResource(R.color.direction_brand)
                    setTextAppearanceResource(R.style.TextAppearance_DubLink_Button)
                    setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.direction_brand_text)))
                    text = " ${filter.direction} "
                    tag = filter
                    isAllCaps = false
                    isClickable = false
                    elevation = 2f.dipToPx(context)
                }
            }
        }
    }

    fun newFilterChip(context: Context, filter: Filter): Chip {
        return when (filter) {
            is Filter.RouteFilter -> {
                val (textColour, backgroundColour) = mapColour(filter.route.operator, filter.route.id)
                Chip(context).apply {
                    setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_DubLink_RouteFilterChip))
                    setChipBackgroundColorResource(backgroundColour)
                    setTextAppearanceResource(R.style.TextAppearance_DubLink_Button_RouteFilter)
                    setTextColor(ColorStateList.valueOf(context.resources.getColor(textColour)))
                    text = filter.route.id
                    tag = filter
                    isAllCaps = false
                    elevation = 4f.dipToPx(context)
                    isCheckedIconVisible = false
                    setRippleColorResource(android.R.color.white)
                }
            }
            is Filter.DirectionFilter -> {
                Chip(context).apply {
                    setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_DubLink_RouteFilterChip))
                    setChipBackgroundColorResource(R.color.direction_brand)
                    setTextAppearanceResource(R.style.TextAppearance_DubLink_Button_RouteFilter)
                    setTextColor(ColorStateList.valueOf(context.resources.getColor(R.color.direction_brand_text)))
                    text = filter.direction
                    tag = filter
                    isAllCaps = false
                    elevation = 4f.dipToPx(context)
                    isCheckedIconVisible = false
                    setRippleColorResource(android.R.color.white)
                }
            }
        }
    }

    fun mapColour(operator: Operator, route: String): Pair<Int, Int> {
        return when (operator) {
            Operator.AIRCOACH -> Pair(R.color.aircoach_brand_text, R.color.aircoach_brand)
            Operator.BUS_EIREANN -> Pair(R.color.bus_eireann_brand_text, R.color.bus_eireann_brand)
            Operator.COMMUTER -> Pair(R.color.commuter_brand_text, R.color.commuter_brand)
            Operator.DART -> Pair(R.color.dart_brand_text, R.color.dart_brand)
            Operator.DUBLIN_BIKES -> Pair(R.color.dublin_bikes_brand_text, R.color.dublin_bikes_brand)
            Operator.DUBLIN_BUS -> Pair(R.color.dublin_bus_brand_text, R.color.dublin_bus_brand)
            Operator.GO_AHEAD -> Pair(R.color.go_ahead_brand_text, R.color.go_ahead_brand)
            Operator.INTERCITY -> Pair(R.color.intercity_brand_text, R.color.intercity_brand)
            Operator.LUAS -> {
                when (route) {
                    "Green", "Green Line" -> Pair(R.color.luas_green_brand_text, R.color.luas_green_brand)
                    "Red", "Red Line" -> Pair(R.color.luas_red_brand_text, R.color.luas_red_brand)
                    else -> Pair(android.R.color.white, android.R.color.black)
                }
            }
        }
    }
}
