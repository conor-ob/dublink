package ie.dublinmapper.favourites.edit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.TouchCallback
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.domain.model.getSortedRoutes
import ie.dublinmapper.favourites.R
import ie.dublinmapper.model.extractServiceLocation
import ie.dublinmapper.util.ChipFactory
import ie.dublinmapper.viewModelProvider
import io.rtpi.api.StopLocation
import kotlinx.android.synthetic.main.dialog_favourites_routes_selection.*
import kotlinx.android.synthetic.main.dialog_favourites_routes_selection.view.*
import kotlinx.android.synthetic.main.fragment_edit_favourites.*
import kotlinx.android.synthetic.main.fragment_edit_favourites.view.*
import timber.log.Timber

class EditFavouritesFragment : DublinMapperFragment(R.layout.fragment_edit_favourites) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as EditFavouritesViewModel }
    private var adapter: EditFavouritesAdapter<GroupieViewHolder>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit_favourites_toolbar.apply {
            setNavigationOnClickListener { activity?.onBackPressed() }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_settings -> {
                        (activity as DublinMapperNavigator).navigateToSettings()
                        return@setOnMenuItemClickListener true
                    }
                    else -> super.onOptionsItemSelected(menuItem)
                }
            }
        }

        adapter = EditFavouritesAdapter()
        adapter?.setOnItemClickListener { item, view ->
            val serviceLocation = item.extractServiceLocation()
            if (serviceLocation != null) {
                if (!enabledServiceManager.isServiceEnabled(serviceLocation.service)) {
                    enabledServiceManager.enableService(serviceLocation.service)
                }
                val sortedRoutes = if (serviceLocation is StopLocation) {
                    serviceLocation.getSortedRoutes()
                } else {
                    emptyList()
                }
                val dialogView = requireActivity()
                    .layoutInflater.
                    inflate(R.layout.dialog_favourites_routes_selection, null)
                for ((operator, route) in sortedRoutes) {
                    val routeFilterChip = ChipFactory
                        .newRouteFilterChip(requireContext(), operator to route)
                        .apply {
//                            isChecked = state.activeRouteFilters.contains(route)
                            isChecked = false
                            alpha = 0.4f
//                            alpha = if (state.activeRouteFilters.contains(route)) {
//                                1.0f
//                            } else {
//                                0.4f
//                            }
//                            setOnCheckedChangeListener(routeFilterClickedListener)
                        }
                    dialogView.favourites_edit_routes.addView(routeFilterChip)
                }
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setTitle(serviceLocation.name)
                    .setMessage(serviceLocation.service.fullName)
                    .setView(dialogView)
//                    .setItems(arrayOf("46A", "145"), null)
                    .setPositiveButton("ok", null)
                    .setNegativeButton("cancel", null)
                    .create()
                    .show()
            }
        }

        view.edit_favourites_list.adapter = adapter
        view.edit_favourites_list.setHasFixedSize(true)
        view.edit_favourites_list.layoutManager = LinearLayoutManager(requireContext())

        ItemTouchHelper(dragAndDropTouchCallback).attachToRecyclerView(edit_favourites_list)

        edit_favourites_save_fab.setOnClickListener {
            adapter?.getServiceLocations()?.let {
                viewModel.dispatch(Action.SaveChanges(it))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.dispatch(Action.GetFavourites)
    }

    private fun renderState(state: State) {
        renderSaveButtonState(state)
        renderFavourites(state)
    }

    private fun renderSaveButtonState(state: State) {
        if (state.isFinished == true) {
            activity?.onBackPressed()
        }
        if (state.original != null && state.editing != null && state.original != state.editing) {
            edit_favourites_save_fab.visibility = View.VISIBLE
        } else {
            edit_favourites_save_fab.visibility = View.GONE
        }
    }

    private fun renderFavourites(state: State) {
        if (state.editing != null) {
            adapter?.update(state.editing)
        }
    }

    private val dragAndDropTouchCallback = object : TouchCallback() {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val serviceLocations = adapter?.getServiceLocations()?.toMutableList() ?: mutableListOf()
            val item = adapter?.getServiceLocation(viewHolder.adapterPosition)
            val targetItem = adapter?.getServiceLocation(target.adapterPosition)
            if (item != null && targetItem != null) {
                val targetIndex = serviceLocations.indexOf(targetItem)
                serviceLocations.remove(item)
                serviceLocations.add(targetIndex, item)
                adapter?.update(serviceLocations)
                viewModel.dispatch(Action.FavouritesReordered(serviceLocations))
                return true
            }
            return false
        }
    }
}
