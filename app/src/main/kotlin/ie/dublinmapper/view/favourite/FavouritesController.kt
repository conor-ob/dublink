package ie.dublinmapper.view.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.CircularRevealChangeHandler
import ie.dublinmapper.util.getApplicationComponent
import ie.dublinmapper.util.requireContext
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
        view.favouritesList.adapter = adapter
        view.favouritesList.setHasFixedSize(true)
        view.favouritesList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.onViewAttached()
    }

    override fun onDetach(view: View) {
        presenter.onViewDetached()
        super.onDetach(view)
    }

    override fun showFavourites(favourites: List<ServiceLocationUi>) {
        adapter.clear()
        adapter.addAll(favourites.map { it.toItem() })
    }

    private fun setupSearchFab(view: View) {
        view.search_fab.setOnClickListener {
            val searchController = SearchController(Bundle.EMPTY)
            router.pushController(
                RouterTransaction
                    .with(searchController)
                    .pushChangeHandler(CircularRevealChangeHandler(view.search_fab, view.view_favourites_root))
                    .popChangeHandler(CircularRevealChangeHandler(view.search_fab, view.view_favourites_root))
            )
        }
    }

}
