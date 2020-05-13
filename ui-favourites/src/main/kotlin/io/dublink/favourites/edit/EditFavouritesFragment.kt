package io.dublink.favourites.edit

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.TouchCallback
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.DubLinkFragment
import io.dublink.DubLinkNavigator
import io.dublink.dialog.FavouriteDialogFactory
import io.dublink.dialog.OnFavouriteSavedListener
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.service.StringProvider
import io.dublink.favourites.R
import io.dublink.model.AbstractServiceLocationItem
import io.dublink.model.getServiceLocation
import io.dublink.viewModelProvider
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_edit_favourites.*
import kotlinx.android.synthetic.main.fragment_edit_favourites.view.*

class EditFavouritesFragment : DubLinkFragment(R.layout.fragment_edit_favourites) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as EditFavouritesViewModel }
    private var adapter: EditFavouritesAdapter<GroupieViewHolder>? = null

    @Inject
    lateinit var stringProvider: StringProvider

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
                        (activity as DubLinkNavigator).navigateToSettings()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_help -> {
                        val text = "Click an item to start editing\n\n" +
                            "Long click an item to drag & drop and change its position\n\n" +
                            "Turn off the Sort by location setting to use your custom sort order\n\n" +
                            "Click Save changes when finished"
                        val test = SpannableString(text).apply {
                            setSpan(
                                ForegroundColorSpan(
                                    resources.getColor(R.color.dublink_blue, null)
                                ),
                                text.indexOf("Click"), text.indexOf("Click") + 5,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                            setSpan(
                                ForegroundColorSpan(
                                    resources.getColor(R.color.dublink_blue, null)
                                ),
                                text.indexOf("Long click"), text.indexOf("Long click") + 10,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                            setSpan(
                                ForegroundColorSpan(
                                    resources.getColor(R.color.dublink_blue, null)
                                ),
                                text.indexOf("drag & drop"), text.indexOf("drag & drop") + 11,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                            setSpan(
                                ForegroundColorSpan(
                                    resources.getColor(R.color.dublink_blue, null)
                                ),
                                text.indexOf("Sort by location"), text.indexOf("Sort by location") + 16,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                            setSpan(
                                ForegroundColorSpan(
                                    resources.getColor(R.color.dublink_blue, null)
                                ),
                                text.indexOf("Save changes"), text.indexOf("Save changes") + 12,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                        }

                        AlertDialog.Builder(requireContext())
                            .setTitle("Info")
                            .setMessage(test)
//                            .setMessage(
//                                "Click an item to start editing\n\n" +
//                                    "Long click on an item and drag and drop to change its position\n\n" +
//                                    "Click \"Save Changes\" when finished"
//                            )
                            .setPositiveButton("Ok", null)
                            .create()
                            .show()
                        return@setOnMenuItemClickListener true
                    }
                    else -> super.onOptionsItemSelected(menuItem)
                }
            }
        }

        adapter = EditFavouritesAdapter()
        adapter?.setOnItemClickListener { item, view ->
            if (item is AbstractServiceLocationItem) {
                val serviceLocation = item.getServiceLocation()
                FavouriteDialogFactory.newCustomizationDialog(
                    context = requireContext(),
                    activity = requireActivity(),
                    serviceLocation = serviceLocation,
                    onFavouriteSavedListener = object : OnFavouriteSavedListener {

                        override fun onSave(serviceLocation: DubLinkServiceLocation) {
                            viewModel.dispatch(
                                Action.EditFavourite(
                                    serviceLocation
                                )
                            )
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

        edit_favourites_swipe_resresh.apply {
            isEnabled = false
            setColorSchemeResources(R.color.color_on_surface)
            setProgressBackgroundColorSchemeResource(R.color.color_surface)
        }
    }

    override fun onStart() {
        super.onStart()
        // TODO add viewLifecycleOwner owner as param?
        activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        viewModel.dispatch(Action.GetFavourites)
    }

    override fun onStop() {
        super.onStop()
        onBackPressedCallback.remove()
    }

    private fun renderState(state: State) {
        renderErrorMessage(state)
        renderSaveButtonState(state)
        renderFavourites(state)
    }

    private fun renderErrorMessage(state: State) {
        if (state.toastMessage != null) {
            Toast.makeText(requireContext(), state.toastMessage, Toast.LENGTH_SHORT).show()
        } else if (state.errorResponses.isNotEmpty()) {
            Toast.makeText(requireContext(), stringProvider.errorMessage(state.errorResponses), Toast.LENGTH_LONG).show()
        }
    }

    private fun renderSaveButtonState(state: State) {
        edit_favourites_swipe_resresh.isRefreshing = state.original.isNullOrEmpty()
        if (state.isFinished) {
            edit_favourites_save_fab.visibility = View.GONE
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

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            if (edit_favourites_save_fab.visibility == View.VISIBLE) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Unsaved changes")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes") { _, _ -> findNavController().navigateUp() }
                    .setNegativeButton("No", null)
                    .create()
                    .show()
            } else {
                findNavController().navigateUp()
            }
        }
    }
}
