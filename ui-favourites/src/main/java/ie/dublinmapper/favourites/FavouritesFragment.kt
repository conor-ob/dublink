package ie.dublinmapper.favourites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.Navigator
import ie.dublinmapper.domain.model.DetailedServiceLocation
import ie.dublinmapper.ui.DublinMapperFragment
import ie.dublinmapper.ui.viewModelProvider
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.view.*
import timber.log.Timber

class FavouritesFragment : DublinMapperFragment(R.layout.fragment_favourites) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as FavouritesViewModel }

    private lateinit var adapter: GroupAdapter<ViewHolder>

    override fun styleId() = R.style.FavouritesTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroupAdapter()
        adapter.setOnItemClickListener { item, _ ->
            (item.extras["serviceLocation"] as? DetailedServiceLocation)?.let { serviceLocation ->
                (activity as Navigator).navigateFavouritesToLiveData(serviceLocation)
            }
        }
        view.liveDataList.adapter = adapter
        view.liveDataList.setHasFixedSize(true)
        view.liveDataList.layoutManager = LinearLayoutManager(requireContext())

        search_fab.setOnClickListener { (activity as Navigator).navigateFavouritesToSearch() }

        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.dispatch(Action.GetFavourites)
    }

    private fun renderState(state: State) {
        Timber.d("${state.hashCode()} - $state")
        if (state.favourites != null) {
            adapter.update(listOf(state.favourites))
        }
    }

}
