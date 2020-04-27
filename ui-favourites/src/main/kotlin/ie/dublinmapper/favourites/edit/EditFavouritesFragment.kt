package ie.dublinmapper.favourites.edit

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.TouchCallback
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import ie.dublinmapper.dialog.CustomizeFavouriteDialogFactory
import ie.dublinmapper.dialog.OnFavouriteSavedListener
import ie.dublinmapper.favourites.R
import ie.dublinmapper.model.extractServiceLocation
import ie.dublinmapper.viewModelProvider
import io.rtpi.api.ServiceLocation
import kotlinx.android.synthetic.main.fragment_edit_favourites.*
import kotlinx.android.synthetic.main.fragment_edit_favourites.view.*

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
                CustomizeFavouriteDialogFactory.newDialog(
                    context = requireContext(),
                    activity = requireActivity(),
                    serviceLocation = serviceLocation,
                    onFavouriteSavedListener = object : OnFavouriteSavedListener {

                        override fun onSave(serviceLocation: ServiceLocation) {
                             viewModel.dispatch(Action.EditFavourite(serviceLocation))
                        }
                    }
                ).show()
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
