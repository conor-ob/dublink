package ie.dublinmapper.favourites

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import ie.dublinmapper.ui.DublinMapperFragment
import kotlinx.android.synthetic.main.fragment_favourites.*

class FavouritesFragment : DublinMapperFragment(R.layout.fragment_favourites) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_fab.setOnClickListener { findNavController().navigate(R.id.favouritesFragment_to_searchFragment) }
    }
}