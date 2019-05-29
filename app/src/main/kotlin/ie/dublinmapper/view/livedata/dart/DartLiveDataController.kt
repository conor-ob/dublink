package ie.dublinmapper.view.livedata.dart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.view.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.model.LiveDataUi
import ie.dublinmapper.util.getApplicationComponent
import ie.dublinmapper.util.requireContext
import kotlinx.android.synthetic.main.view_dart_live_data.view.favouritesList
import kotlinx.android.synthetic.main.view_dart_live_data.view.toolbar

class DartLiveDataController(
    args: Bundle
) : MvpBaseController<DartLiveDataView, DartLiveDataPresenter>(args), DartLiveDataView {

    private lateinit var adapter: GroupAdapter<ViewHolder>

    override val layoutId = R.layout.view_dart_live_data

    override fun createPresenter(): DartLiveDataPresenter {
        return getApplicationComponent().dartLiveDataPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view =  super.onCreateView(inflater, container)
        setupToolbar(view)
        setupLiveData(view)
        return view
    }

    private fun setupToolbar(view: View) {
        view.toolbar.title = args.getString(STATION_NAME)
    }

    private fun setupLiveData(view: View) {
        adapter = GroupAdapter()
        view.favouritesList.adapter = adapter
        view.favouritesList.setHasFixedSize(true)
        view.favouritesList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.start(args.getString(STATION_ID)!!)
    }

    override fun onDetach(view: View) {
        presenter.stop()
        super.onDetach(view)
    }

    override fun showLiveData(liveData: List<LiveDataUi>) {
        adapter.update(liveData.map { it.toItem() })
    }

    companion object {

        private const val STATION_ID = "station_id"
        private const val STATION_NAME = "station_name"

    }

    class Builder(
        private val stationId: String,
        private val stationName: String
    ) {

        fun build(): DartLiveDataController {
            val args = Bundle()
            args.putString(STATION_ID, stationId)
            args.putString(STATION_NAME, stationName)
            return DartLiveDataController(args)
        }

    }

}
