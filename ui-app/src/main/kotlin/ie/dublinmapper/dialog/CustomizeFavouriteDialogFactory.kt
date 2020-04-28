package ie.dublinmapper.dialog

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.chip.Chip
import ie.dublinmapper.domain.model.addCustomRoute
import ie.dublinmapper.domain.model.getCustomName
import ie.dublinmapper.domain.model.getCustomRoutes
import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.domain.model.getSortedRoutes
import ie.dublinmapper.domain.model.hasCustomRoute
import ie.dublinmapper.domain.model.removeCustomRoute
import ie.dublinmapper.domain.model.setCustomName
import ie.dublinmapper.ui.R
import ie.dublinmapper.util.ChipFactory
import io.rtpi.api.DockLocation
import io.rtpi.api.Operator
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import kotlinx.android.synthetic.main.dialog_customize_favourite.view.*

object CustomizeFavouriteDialogFactory {

    fun newDialog(
        context: Context,
        activity: FragmentActivity,
        serviceLocation: ServiceLocation,
        onFavouriteSavedListener: OnFavouriteSavedListener
    ): AlertDialog {
        var editedStopLocation = serviceLocation

        val builder = AlertDialog.Builder(context)
        builder.setTitle(serviceLocation.name)
            .setMessage(serviceLocation.service.fullName)
            .setPositiveButton("Ok", null)
            .setNegativeButton("Cancel", null)
        val customizeFavouriteView = activity.layoutInflater.inflate(R.layout.dialog_customize_favourite, null)
        builder.setView(customizeFavouriteView)

        when (serviceLocation) {
            is DockLocation -> {
                customizeFavouriteView.favourite_edit_routes.visibility = View.GONE
            }
            is StopLocation -> {
//                if (serviceLocation.routeGroups.map { it.operator }.contains(Operator.DART)) {
//                    "dart"
//                } else {
//                    "stop"
//                }
                customizeFavouriteView.favourite_edit_routes.visibility = View.VISIBLE
                val sortedRoutes = serviceLocation.getSortedRoutes()
                for ((operator, route) in sortedRoutes) {
                    val routeFilterChip = ChipFactory
                        .newRouteFilterChip(context, operator to route)
                        .apply {
                            isChecked = serviceLocation.hasCustomRoute(operator, route)
                            alpha = if (serviceLocation.hasCustomRoute(operator, route)) {
                                1.0f
                            } else {
                                0.33f
                            }
                            setOnCheckedChangeListener { buttonView, isChecked ->
                                (buttonView as Chip).apply {
                                    alpha = if (isChecked) {
                                        1.0f
                                    } else {
                                        0.33f
                                    }
                                }
                                editedStopLocation = if (isChecked) {
                                    editedStopLocation.addCustomRoute(
                                        buttonView.tag as Operator,
                                        buttonView.text.toString()
                                    ) as StopLocation
                                } else {
                                    editedStopLocation.removeCustomRoute(
                                        buttonView.tag as Operator,
                                        buttonView.text.toString()
                                    ) as StopLocation
                                }
                            }
                        }
                    customizeFavouriteView.favourite_edit_routes.addView(routeFilterChip)
                }
                builder.setNeutralButton("Select All", null)
            }
        }

        customizeFavouriteView.favourite_edit_name.hint = serviceLocation.getName()
        if (!serviceLocation.getCustomName().isNullOrEmpty()) {
            customizeFavouriteView.favourite_edit_name.setText(serviceLocation.getCustomName())
        }

        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dialog.dismiss()
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                for (i in 0 until customizeFavouriteView.favourite_edit_routes.childCount) {
                    val chip = customizeFavouriteView.favourite_edit_routes.getChildAt(i) as Chip
                    chip.isChecked = true
                }
            }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (serviceLocation is StopLocation && editedStopLocation.getCustomRoutes().isNullOrEmpty()) {
                    Toast.makeText(context, "Select at least 1 route", Toast.LENGTH_SHORT).show()
                } else {
                    val customName = customizeFavouriteView.favourite_edit_name.text
                    editedStopLocation = if (customName.isNullOrBlank()) {
                        editedStopLocation.setCustomName(serviceLocation.name)
                    } else {
                        editedStopLocation.setCustomName(customName.toString())
                    }
                    onFavouriteSavedListener.onSave(editedStopLocation)
                    dialog.dismiss()
                }
            }
        }
        return dialog
    }
}

interface OnFavouriteSavedListener {
    fun onSave(serviceLocation: ServiceLocation)
}
