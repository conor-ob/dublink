package ie.dublinmapper.dialog

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.chip.Chip
import ie.dublinmapper.domain.model.addCustomRoute
import ie.dublinmapper.domain.model.getCustomRoutes
import ie.dublinmapper.domain.model.getSortedRoutes
import ie.dublinmapper.domain.model.hasCustomRoute
import ie.dublinmapper.domain.model.removeCustomRoute
import ie.dublinmapper.ui.R
import ie.dublinmapper.util.ChipFactory
import io.rtpi.api.DockLocation
import io.rtpi.api.Operator
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import kotlinx.android.synthetic.main.dialog_favourites_routes_selection.view.*

object CustomizeFavouriteDialogFactory {

    fun newDialog(
        context: Context,
        activity: FragmentActivity,
        serviceLocation: ServiceLocation,
        onFavouriteSavedListener: OnFavouriteSavedListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(serviceLocation.name)
            .setMessage(serviceLocation.service.fullName)
            .setPositiveButton("Ok", null)
            .setNegativeButton("Cancel", null)
        return when (serviceLocation) {
            is StopLocation -> newStopLocationDialog(
                builder,
                context,
                activity,
                serviceLocation,
                onFavouriteSavedListener
            )
            is DockLocation -> newDockLocationDialog(
                builder,
                serviceLocation,
                onFavouriteSavedListener
            )
            else -> TODO()
        }
    }

    private fun newDockLocationDialog(
        builder: AlertDialog.Builder,
        dockLocation: DockLocation,
        onFavouriteSavedListener: OnFavouriteSavedListener
    ): AlertDialog {
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dialog.dismiss()
            }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onFavouriteSavedListener.onSave(dockLocation)
                dialog.dismiss()
            }
        }
        return dialog
    }

    private fun newStopLocationDialog(
        builder: AlertDialog.Builder,
        context: Context,
        activity: FragmentActivity,
        stopLocation: StopLocation,
        onFavouriteSavedListener: OnFavouriteSavedListener
    ): AlertDialog {
        var editedStopLocation = stopLocation
        val routesView = activity.layoutInflater.inflate(R.layout.dialog_favourites_routes_selection, null)
        val sortedRoutes = stopLocation.getSortedRoutes()
        for ((operator, route) in sortedRoutes) {
            val routeFilterChip = ChipFactory
                .newRouteFilterChip(context, operator to route)
                .apply {
                    isChecked = stopLocation.hasCustomRoute(operator, route)
                    alpha = if (stopLocation.hasCustomRoute(operator, route)) {
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
            routesView.favourites_edit_routes.addView(routeFilterChip)
        }
        builder.setView(routesView)
        builder.setNeutralButton("Select All", null)
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dialog.dismiss()
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                if (routesView != null) {
                    for (i in 0 until routesView.favourites_edit_routes.childCount) {
                        val chip = routesView.favourites_edit_routes.getChildAt(i) as Chip
                        chip.isChecked = true
                    }
                }
            }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (editedStopLocation.getCustomRoutes().isNullOrEmpty()) {
                        Toast.makeText(context, "Select at least 1 route", Toast.LENGTH_SHORT).show()
                } else {
//                    val text = routesView.favourites_edit_name.text
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
