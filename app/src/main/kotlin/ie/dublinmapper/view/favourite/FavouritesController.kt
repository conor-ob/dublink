package ie.dublinmapper.view.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.view.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.model.buseireann.BusEireannStopItem
import ie.dublinmapper.model.dart.DartStationItem
import ie.dublinmapper.model.dublinbikes.DublinBikesDockItem
import ie.dublinmapper.model.dublinbus.DublinBusStopItem
import ie.dublinmapper.model.luas.LuasStopItem
import ie.dublinmapper.util.CircularRevealChangeHandler
import ie.dublinmapper.util.getApplicationComponent
import ie.dublinmapper.util.requireContext
import ie.dublinmapper.view.livedata.LiveDataController
import ie.dublinmapper.view.search.SearchController
import kotlinx.android.synthetic.main.view_favourites.view.*

class FavouritesController(
    args: Bundle
) : MvpBaseController<FavouritesView, FavouritesPresenter>(args), FavouritesView {

    private lateinit var adapter: GroupAdapter<ViewHolder>

    override val layoutId = R.layout.view_favourites

    override fun createPresenter(): FavouritesPresenter {
        return getApplicationComponent().favouritesPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupFavouritesList(view)
        setupSearchFab(view)
        return view
    }

    private fun setupFavouritesList(view: View) {
        adapter = GroupAdapter()
        adapter.setOnItemClickListener { item, _ -> onItemClicked(item) }
        view.liveDataList.adapter = adapter
        view.liveDataList.setHasFixedSize(true)
        view.liveDataList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onItemClicked(item: Item<*>) {
        val liveDataController: LiveDataController = when (item) {
            is AircoachStopItem -> {
                LiveDataController.Builder(
                    serviceLocationId = item.aircoachStop.id,
                    serviceLocationName = item.aircoachStop.name,
                    serviceLocationService = item.aircoachStop.service,
                    serviceLocationStyleId = R.style.AircoachTheme, //TODO
                    serviceLocationIsFavourite = true
                ).build()
            }
            is BusEireannStopItem -> {
                LiveDataController.Builder(
                    serviceLocationId = item.busEireannStop.id,
                    serviceLocationName = item.busEireannStop.name,
                    serviceLocationService = item.busEireannStop.service,
                    serviceLocationStyleId = R.style.BusEireannTheme, //TODO
                    serviceLocationIsFavourite = true
                ).build()
            }
            is DartStationItem -> {
                LiveDataController.Builder(
                    serviceLocationId = item.dartStation.id,
                    serviceLocationName = item.dartStation.name,
                    serviceLocationService = item.dartStation.service,
                    serviceLocationStyleId = R.style.DartTheme, //TODO
                    serviceLocationIsFavourite = true
                ).build()
            }
            is DublinBikesDockItem -> {
                LiveDataController.Builder(
                    serviceLocationId = item.dublinBikesDock.id,
                    serviceLocationName = item.dublinBikesDock.name,
                    serviceLocationService = item.dublinBikesDock.service,
                    serviceLocationStyleId = R.style.DublinBikesTheme, //TODO
                    serviceLocationIsFavourite = true
                ).build()
            }
            is DublinBusStopItem -> {
                LiveDataController.Builder(
                    serviceLocationId = item.dublinBusStop.id,
                    serviceLocationName = item.dublinBusStop.name,
                    serviceLocationService = item.dublinBusStop.service,
                    serviceLocationStyleId = R.style.DublinBusTheme, //TODO
                    serviceLocationIsFavourite = true
                ).build()
            }
            is LuasStopItem -> {
                LiveDataController.Builder(
                    serviceLocationId = item.luasStop.id,
                    serviceLocationName = item.luasStop.name,
                    serviceLocationService = item.luasStop.service,
                    serviceLocationStyleId = R.style.LuasTheme, //TODO
                    serviceLocationIsFavourite = true
                ).build()
            }
            else -> TODO()
        }
        router.pushController(
            RouterTransaction.with(liveDataController)
                .pushChangeHandler(FadeChangeHandler())
                .popChangeHandler(FadeChangeHandler())
        )
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.start()
    }

    override fun onDetach(view: View) {
        presenter.stop()
        super.onDetach(view)
    }

    override fun showFavourites(favourites: Group) {
        adapter.update(listOf(favourites))
    }

    private fun setupSearchFab(view: View) {
        view.search_fab.setOnClickListener {
            router.pushController(
                RouterTransaction
                    .with(SearchController.Builder(R.style.SearchTheme).build())
                    .pushChangeHandler(CircularRevealChangeHandler(view.search_fab, view.view_favourites_root))
                    .popChangeHandler(CircularRevealChangeHandler(view.search_fab, view.view_favourites_root))
//                    .pushChangeHandler(FadeChangeHandler())
//                    .popChangeHandler(FadeChangeHandler())
            )
        }
    }

    class Builder(
        serviceLocationStyleId: Int
    ) : MvpBaseController.Builder(serviceLocationStyleId) {

        fun build(): FavouritesController {
            return FavouritesController(buildArgs())
        }

    }

}
