package io.dublink.dialog

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.model.Filter
import io.dublink.domain.model.addFilter
import io.dublink.domain.model.removeFilter
import io.dublink.domain.model.setCustomName
import io.dublink.ui.R
import io.dublink.util.ChipFactory
import io.rtpi.api.Service
import kotlinx.android.synthetic.main.dialog_customize_favourite.view.*

object FavouriteDialogFactory {

    fun newCustomizationDialog(
        context: Context,
        activity: FragmentActivity,
        serviceLocation: DubLinkServiceLocation,
        onFavouriteSavedListener: OnFavouriteSavedListener,
        onFavouriteRouteChangedListener: OnFavouriteRouteChangedListener
    ): AlertDialog {
        var editedStopLocation = serviceLocation

        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(serviceLocation.defaultName)
            .setMessage(
                when (val service = serviceLocation.service) {
                    Service.BUS_EIREANN,
                    Service.DUBLIN_BUS -> "${service.fullName} (${serviceLocation.id})"
                    else -> service.fullName
                }
            )
            .setPositiveButton("Ok", null)
            .setNegativeButton("Cancel", null)
        val customizeFavouriteView = activity.layoutInflater.inflate(R.layout.dialog_customize_favourite, null)
        builder.setView(customizeFavouriteView)

        when (serviceLocation) {
            is DubLinkDockLocation -> {
                customizeFavouriteView.favourite_edit_routes.visibility = View.GONE
                customizeFavouriteView.favourite_edit_spacer.visibility = View.VISIBLE
            }
            is DubLinkStopLocation -> {
                customizeFavouriteView.favourite_edit_spacer.visibility = View.GONE
                customizeFavouriteView.favourite_edit_routes.visibility = View.VISIBLE
                for (filter in serviceLocation.filters) {
                    val filterChip = ChipFactory
                        .newFilterChip(context, filter)
                        .apply {
                            isChecked = filter.isActive
                            alpha = if (filter.isActive) {
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
                                    val filterToAdd = buttonView.tag as Filter
                                    val newStopLocation = (editedStopLocation as DubLinkStopLocation).addFilter(
                                        filter = filterToAdd
                                    )
                                    onFavouriteRouteChangedListener.onAdded(filterToAdd)
                                    newStopLocation
                                } else {
                                    val filterToRemove = buttonView.tag as Filter
                                    val newStopLocation = (editedStopLocation as DubLinkStopLocation).removeFilter(
                                        filter = filterToRemove
                                    )
                                    onFavouriteRouteChangedListener.onRemoved(filterToRemove)
                                    newStopLocation
                                }
                            }
                        }
                    customizeFavouriteView.favourite_edit_routes.addView(filterChip)
                }
                builder.setNeutralButton("Select All", null)
            }
        }

        if (editedStopLocation.isFavourite) {
            customizeFavouriteView.favourite_edit_name.editText?.setText(serviceLocation.name)
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
                if (editedStopLocation is DubLinkStopLocation &&
                    (editedStopLocation as DubLinkStopLocation).filters
                        .filterIsInstance<Filter.RouteFilter>()
                        .none { it.isActive } &&
                    (editedStopLocation as DubLinkStopLocation).filters
                        .filterIsInstance<Filter.DirectionFilter>()
                        .none { it.isActive }
                ) {
                    Toast.makeText(context, "Select at least 1 route", Toast.LENGTH_SHORT).show()
                } else {
                    val customName = customizeFavouriteView.favourite_edit_name?.editText?.text
                    editedStopLocation = if (customName.isNullOrBlank()) {
                        editedStopLocation.setCustomName(serviceLocation.defaultName)
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

    fun newEditDialog(
        context: Context,
        serviceLocation: DubLinkServiceLocation,
        onFavouriteEditListener: OnFavouriteEditListener,
        onFavouriteRemovedListener: OnFavouriteRemovedListener
    ): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(serviceLocation.name)
            .setMessage("Edit or remove this?")
            .setPositiveButton("Remove", null)
            .setNegativeButton("Edit", null)
            .setNeutralButton("Cancel", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onFavouriteRemovedListener.onRemove()
                dialog.dismiss()
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                onFavouriteEditListener.onEdit()
                dialog.dismiss()
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                dialog.dismiss()
            }
        }
        return dialog
    }
}

interface OnFavouriteSavedListener {
    fun onSave(serviceLocation: DubLinkServiceLocation)
}

interface OnFavouriteRemovedListener {
    fun onRemove()
}

interface OnFavouriteEditListener {
    fun onEdit()
}

interface OnFavouriteRouteChangedListener {
    fun onAdded(filter: Filter)
    fun onRemoved(filter: Filter)
}
