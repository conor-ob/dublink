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
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.view.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.model.dart.DartStationItem
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
        if (item is DartStationItem) {
            val dartLiveDataController = LiveDataController.Builder(
                serviceLocationId = item.dartStation.id,
                serviceLocationName = item.dartStation.name,
                serviceLocationService = item.dartStation.service,
                serviceLocationStyleId = item.dartStation.styleId,
                serviceLocationIsFavourite = false
            ).build()
            router.pushController(
                RouterTransaction.with(dartLiveDataController)
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler())
            )
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.start()
    }

    override fun onDetach(view: View) {
        presenter.stop()
        super.onDetach(view)
    }

    override fun showFavourites(favourites: List<ServiceLocationUi>) {
        val groups = mutableListOf<Group>()
        groups.add(DividerItem())
        groups.add(HeaderItem("Favourites"))
        for (i in 0 until favourites.size) {
            val isLast = i == favourites.size - 1
            val isEven = i % 2 == 0
            groups.add(favourites[i].toItem(isEven, isLast))
        }
        groups.add(DividerItem())
        adapter.update(groups)
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
