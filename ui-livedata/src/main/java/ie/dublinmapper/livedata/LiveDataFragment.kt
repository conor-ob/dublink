package ie.dublinmapper.livedata

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.Navigator
import ie.dublinmapper.ui.DublinMapperFragment
import ie.dublinmapper.ui.viewModelProvider
import io.rtpi.api.Service
import kotlinx.android.synthetic.main.fragment_livedata.*
import timber.log.Timber

class LiveDataFragment : DublinMapperFragment(R.layout.fragment_livedata) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as LiveDataViewModel }

    private lateinit var adapter: GroupAdapter<ViewHolder>

    override fun styleId() = arguments!!.getInt("serviceLocationStyleId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.inflateMenu(R.menu.menu_live_data)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        if (arguments!!.getBoolean("serviceLocationIsFavourite")) {
            val favouriteMenuItem = toolbar.menu.findItem(R.id.action_favourite)
            favouriteMenuItem.setIcon(R.drawable.ic_favourite_selected)
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_favourite -> {
                    if (arguments!!.getBoolean("serviceLocationIsFavourite")) {
                        viewModel.dispatch(Action.RemoveFavourite(
                            serviceLocationId = arguments!!.getString("serviceLocationId")!!,
                            serviceLocationService = arguments!!.getSerializable("serviceLocationService")!! as Service
                        ))
                    } else {
                        viewModel.dispatch(Action.SaveFavourite(
                            serviceLocationId = arguments!!.getString("serviceLocationId")!!,
                            serviceLocationName = arguments!!.getString("serviceLocationName")!!,
                            serviceLocationService = arguments!!.getSerializable("serviceLocationService")!! as Service
                        ))
                    }
                    return@setOnMenuItemClickListener true
                }
                R.id.action_settings -> {
                    (activity as Navigator).navigateLiveDataToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }
        serviceLocationName.text = arguments!!.getString("serviceLocationName")

        adapter = GroupAdapter()
        liveDataList.adapter = adapter
        liveDataList.setHasFixedSize(true)
        liveDataList.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemClickListener { item, view ->
            Timber.d("clicked")
        }

        viewModel.dispatch(Action.GetLiveData(
            serviceLocationId = arguments!!.getString("serviceLocationId")!!,
            serviceLocationName = arguments!!.getString("serviceLocationName")!!,
            serviceLocationService = arguments!!.getSerializable("serviceLocationService")!! as Service
        ))
    }

    private fun renderState(state: State) {
        Timber.d(state.toString())
        if (state.liveData != null) {
            adapter.update(listOf(state.liveData))
        }
    }
}
