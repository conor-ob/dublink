package ie.dublinmapper.view.search

import com.hannesdorfmann.mosby3.mvp.MvpPresenter

interface SearchPresenter : MvpPresenter<SearchView> {

    fun onViewAttached()

    fun onViewDetached()

    fun onQueryTextSubmit(query: String)

}
