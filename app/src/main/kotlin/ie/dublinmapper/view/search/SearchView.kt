package ie.dublinmapper.view.search

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.domain.model.ServiceLocation

interface SearchView : MvpView {

    fun showSearchResults(searchResults: List<ServiceLocation>)

}
