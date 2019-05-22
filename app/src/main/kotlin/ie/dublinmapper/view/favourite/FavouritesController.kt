package ie.dublinmapper.view.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.util.CircularRevealChangeHandler
import ie.dublinmapper.util.getApplicationComponent
import ie.dublinmapper.view.search.SearchController
import kotlinx.android.synthetic.main.view_favourites.view.*

class FavouritesController(
    args: Bundle
) : MvpBaseController<FavouritesView, FavouritesPresenter>(args), FavouritesView {

    override val layoutId = R.layout.view_favourites

    override fun createPresenter(): FavouritesPresenter {
        return getApplicationComponent().favouritesPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupSearchFab(view)
        return view
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
