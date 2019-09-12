package ie.dublinmapper.favourites

import android.os.Bundle
import android.view.View
import ie.dublinmapper.Navigator
import ie.dublinmapper.ui.DublinMapperFragment
import kotlinx.android.synthetic.main.fragment_favourites.*

class FavouritesFragment : DublinMapperFragment(R.layout.fragment_favourites, R.style.FavouritesTheme) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_fab.setOnClickListener { (activity as Navigator).navigateFromFavouritesToSearch() }
    }
}