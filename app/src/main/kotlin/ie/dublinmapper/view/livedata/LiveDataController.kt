package ie.dublinmapper.view.livedata

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.model.LiveDataUi
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.getApplicationComponent
import timber.log.Timber

class LiveDataController(
    args: Bundle
) : MvpBaseController<LiveDataView, LiveDataPresenter>(args), LiveDataView {

    private lateinit var background: ViewGroup
    private lateinit var serviceLocationName: TextView
    private lateinit var serviceLocationInfo: TextView
    private lateinit var liveDataAdapter: LiveDataAdapter
    private lateinit var recyclerView: RecyclerView

    override val layoutId = R.layout.view_live_data

    override fun createPresenter(): LiveDataPresenter {
        return getApplicationComponent().liveDataPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupLayout(view)
        return view
    }

    private fun setupLayout(view: View) {
        background = view.findViewById(R.id.background_live_data)
        serviceLocationName = view.findViewById(R.id.service_location_name)
        serviceLocationInfo = view.findViewById(R.id.service_location_info)
        liveDataAdapter = LiveDataAdapter()
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            adapter = liveDataAdapter
        }
    }

    fun focusOnServiceLocation(serviceLocation: ServiceLocationUi) {
        presenter.onFocusedOnServiceLocation(serviceLocation)
    }

    override fun showServiceLocationColour(colourId: Int) {
        background.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext!!, colourId))
    }

    override fun showServiceLocation(serviceLocation: ServiceLocationUi) {
        serviceLocationName.text = serviceLocation.name
        serviceLocationInfo.text = serviceLocation.mapIconText
    }

    override fun showLiveData(liveData: List<LiveDataUi>) {
        liveData.forEach { Timber.d(it.toString()) }
        liveDataAdapter.showLiveData(liveData)
    }

}
