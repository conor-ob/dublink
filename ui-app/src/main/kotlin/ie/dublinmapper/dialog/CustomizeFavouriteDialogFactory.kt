package ie.dublinmapper.dialog

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.chip.Chip
import ie.dublinmapper.domain.model.DubLinkDockLocation
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.model.DubLinkStopLocation
import ie.dublinmapper.domain.model.Filter
import ie.dublinmapper.domain.model.addFilter
import ie.dublinmapper.domain.model.removeFilter
import ie.dublinmapper.domain.model.setCustomName
import ie.dublinmapper.ui.R
import ie.dublinmapper.util.ChipFactory
import kotlinx.android.synthetic.main.dialog_customize_favourite.view.*

object CustomizeFavouriteDialogFactory {

    fun newDialog(
        context: Context,
        activity: FragmentActivity,
        serviceLocation: DubLinkServiceLocation,
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
            is DubLinkDockLocation -> {
                customizeFavouriteView.favourite_edit_routes.visibility = View.GONE
            }
            is DubLinkStopLocation -> {
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
                                    (editedStopLocation as DubLinkStopLocation).addFilter(
                                        filter = buttonView.tag as Filter
                                    )
                                } else {
                                    (editedStopLocation as DubLinkStopLocation).removeFilter(
                                        filter = buttonView.tag as Filter
                                    )
                                }
                            }
                        }
                    customizeFavouriteView.favourite_edit_routes.addView(filterChip)
                }
                builder.setNeutralButton("Select All", null)
            }
        }

        customizeFavouriteView.favourite_edit_name.hint = serviceLocation.name
        customizeFavouriteView.favourite_edit_name.setText(serviceLocation.name)

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
                if (serviceLocation is DubLinkStopLocation && (editedStopLocation as DubLinkStopLocation).filters
                        .filterIsInstance<Filter.RouteFilter>()
                        .none { it.isActive }
                ) {
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
    fun onSave(serviceLocation: DubLinkServiceLocation)
}
