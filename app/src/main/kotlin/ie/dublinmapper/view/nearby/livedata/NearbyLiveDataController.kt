package ie.dublinmapper.view.nearby.livedata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.getApplicationComponent
import ie.dublinmapper.view.livedata.LiveDataAdapter
import ie.dublinmapper.view.nearby.HomeController
import timber.log.Timber

class NearbyLiveDataController(
    args: Bundle
) : MvpBaseController<NearbyLiveDataView, NearbyLiveDataPresenter>(args), NearbyLiveDataView {

    private lateinit var background: ViewGroup
    private lateinit var serviceLocationName: TextView
    private lateinit var serviceLocationInfo: TextView
    private lateinit var liveDataAdapter: LiveDataAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var container: View

    override val layoutId = R.layout.view_nearby_live_data

    override fun createPresenter(): NearbyLiveDataPresenter {
        return getApplicationComponent().nearbyLiveDataPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupLayout(view)
        return view
    }

    private fun setupLayout(view: View) {
        background = view.findViewById(R.id.background_live_data)
//        serviceLocationName = view.findViewById(R.id.service_location_name)
//        serviceLocationInfo = view.findViewById(R.id.service_location_info)
        liveDataAdapter = LiveDataAdapter()
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            adapter = liveDataAdapter
        }
    }

    override fun render(viewModel: NearbyLiveDataViewModel) {
//        if (viewModel.serviceLocation != null) {
//            serviceLocationName.text = viewModel.serviceLocation.name
//            serviceLocationInfo.text = viewModel.serviceLocation.mapIconText
//        }
        viewModel.liveData.forEach { Timber.d(it.toString()) }
        liveDataAdapter.showLiveData(viewModel.liveData)
        val viewTreeObserver = background.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {
                    background.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    Timber.d("height: ${background.height} px")
                    (targetController as HomeController).onLiveDataResized(background.height)
                }

            })
        }
    }

    fun getLiveData(serviceLocation: ServiceLocationUi?) {
        presenter.onLiveDataRequested(serviceLocation)
    }

//    fun focusOnServiceLocation(serviceLocation: ServiceLocationUi) {
//        presenter.onFocusedOnServiceLocation(serviceLocation)
//    }
//
//    override fun showServiceLocationColour(colourId: Int) {
//        background.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext!!, colourId))
//    }
//
//    override fun showServiceLocation(serviceLocation: ServiceLocationUi) {
//        serviceLocationName.text = serviceLocation.name
//        serviceLocationInfo.text = serviceLocation.mapIconText
//    }
//
//    override fun showLiveData(liveData: List<LiveDataUi>) {
//        liveData.forEach { Timber.d(it.toString()) }
//        liveDataAdapter.showLiveData(liveData)
//        val viewTreeObserver = background.viewTreeObserver
//        if (viewTreeObserver.isAlive) {
//            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//
//                override fun onGlobalLayout() {
//                    background.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    Timber.d("height: ${background.height} px")
//                    listener.onBottomSheetMeasure(background.height)
//                }
//
//            })
//        }
//    }

}
