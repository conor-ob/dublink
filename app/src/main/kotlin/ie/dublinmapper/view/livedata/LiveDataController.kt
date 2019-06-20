package ie.dublinmapper.view.livedata

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.view.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.util.*
import kotlinx.android.synthetic.main.view_live_data.view.*
import timber.log.Timber

class LiveDataController(args: Bundle) : MvpBaseController<LiveDataView, LiveDataPresenter>(args), LiveDataView {

    private lateinit var adapter: GroupAdapter<ViewHolder>

    override val layoutId = R.layout.view_live_data

    override fun createPresenter(): LiveDataPresenter {
        return getApplicationComponent().liveDataPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupToolbar(view)
        setupLiveData(view)
        return view
    }

    private fun setupToolbar(view: View) {
        view.serviceLocationName.text = requireStringArg(SERVICE_LOCATION_NAME)
        if (args.getBoolean(SERVICE_LOCATION_IS_FAVOURITE)) {
            view.toolbar.inflateMenu(R.menu.menu_live_data_favourite)
        } else {
            view.toolbar.inflateMenu(R.menu.menu_live_data)
        }
        view.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
//        view.toolbar.setLogo(R.drawable.ic_map_marker_dart_1)
        view.toolbar.setNavigationOnClickListener { router.handleBack() }
        view.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_favourite -> {
                    Timber.d("Add Favourite pressed")
                    args.putBoolean(SERVICE_LOCATION_IS_FAVOURITE, true)
                }
                R.id.action_settings -> Timber.d("Settings pressed")
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun setupLiveData(view: View) {
        adapter = GroupAdapter()
        view.liveDataList.adapter = adapter
        view.liveDataList.setHasFixedSize(true)
        view.liveDataList.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemClickListener { item, view ->
            Timber.d("clicked")
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.start(
            serviceLocationId = requireStringArg(SERVICE_LOCATION_ID),
            serviceLocationName = requireStringArg(SERVICE_LOCATION_NAME),
            service = requireSerializableArg(SERVICE_LOCATION_SERVICE) as Service
        )
    }

    override fun onDetach(view: View) {
        presenter.stop()
        super.onDetach(view)
    }

    override fun showLiveData(liveData: Group) {
        adapter.update(listOf(liveData))
    }

    companion object {

        private const val SERVICE_LOCATION_ID = "service_location_id"
        private const val SERVICE_LOCATION_NAME = "service_location_name"
        private const val SERVICE_LOCATION_SERVICE = "service_location_service"
        private const val SERVICE_LOCATION_IS_FAVOURITE = "service_location_is_favourite"

    }

    class Builder(
        private val serviceLocationId: String,
        private val serviceLocationName: String,
        private val serviceLocationService: Service,
        private val serviceLocationIsFavourite: Boolean,
        serviceLocationStyleId: Int
    ) : MvpBaseController.Builder(serviceLocationStyleId) {

        fun build(): LiveDataController {
            val args = buildArgs().apply {
                putString(SERVICE_LOCATION_ID, serviceLocationId)
                putString(SERVICE_LOCATION_NAME, serviceLocationName)
                putSerializable(SERVICE_LOCATION_SERVICE, serviceLocationService)
                putBoolean(SERVICE_LOCATION_IS_FAVOURITE, serviceLocationIsFavourite)
            }
            return LiveDataController(args)
        }

    }

}
