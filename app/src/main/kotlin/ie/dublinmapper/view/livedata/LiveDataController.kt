package ie.dublinmapper.view.livedata

import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ContextThemeWrapper
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

    override val styleId = args.getInt(SERVICE_LOCATION_STYLE_ID)

    override val layoutId = R.layout.view_live_data

    override fun createPresenter(): LiveDataPresenter {
        return getApplicationComponent().dartLiveDataPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val contextThemeWrapper = ContextThemeWrapper(requireActivity(), args.getInt(SERVICE_LOCATION_STYLE_ID))
        val themeInflater = inflater.cloneInContext(contextThemeWrapper)
        val view = super.onCreateView(themeInflater, container)
        setupTheme(view)
        setupToolbar(view)
        setupLiveData(view)
        return view
    }

    private fun setupTheme(view: View) {
        val attributes = requireActivity().obtainStyledAttributes(styleId, R.styleable.ThemeAttributes)
        val primaryColour = attributes.getColor(R.styleable.ThemeAttributes_android_colorPrimary, 0)
        view.appbar.setBackgroundColor(primaryColour)
        view.liveDataList.setBackgroundColor(primaryColour)
        attributes.recycle()
    }

    private fun setupToolbar(view: View) {
        view.serviceLocationName.text = args.getString(SERVICE_LOCATION_NAME)
        view.toolbar.inflateMenu(R.menu.menu_live_data)
        view.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.toolbar.setNavigationOnClickListener { router.handleBack() }
        view.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_favourite -> Timber.d("Add Favourite pressed")
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
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.start(args.getString(SERVICE_LOCATION_ID)!!, args.getSerializable(SERVICE_LOCATION_SERVICE) as Service)
    }

    override fun onDetach(view: View) {
        presenter.stop()
        super.onDetach(view)
    }

    override fun showLiveData(liveData: List<Group>) {
        adapter.update(liveData)
    }

    companion object {

        private const val SERVICE_LOCATION_ID = "service_location_id"
        private const val SERVICE_LOCATION_NAME = "service_location_name"
        private const val SERVICE_LOCATION_SERVICE = "service_location_service"
        private const val SERVICE_LOCATION_STYLE_ID = "service_location_style_id"
//        private const val SERVICE_LOCATION_STYLE_ATTRIBUTES_ID = "service_location_colour_id"

    }

    class Builder(
        private val serviceLocationId: String,
        private val serviceLocationName: String,
        private val serviceLocationService: Service,
        private val serviceLocationStyleId: Int
//        private val serviceLocationStyleAttributesId: IntArray
    ) {

        fun build(): LiveDataController {
            val args = Bundle()
            args.putString(SERVICE_LOCATION_ID, serviceLocationId)
            args.putString(SERVICE_LOCATION_NAME, serviceLocationName)
            args.putSerializable(SERVICE_LOCATION_SERVICE, serviceLocationService)
            args.putInt(SERVICE_LOCATION_STYLE_ID, serviceLocationStyleId)
//            args.putIntArray(SERVICE_LOCATION_STYLE_ATTRIBUTES_ID, serviceLocationStyleAttributesId)
            return LiveDataController(args)
        }

    }

}
