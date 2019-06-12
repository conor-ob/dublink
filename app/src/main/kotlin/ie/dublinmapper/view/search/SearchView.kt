package ie.dublinmapper.view.search

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.model.ServiceLocationUi

interface SearchView : MvpView {

    fun showLoading(isLoading: Boolean)

    fun showSearchResults(searchResults: List<ServiceLocationUi>)

    fun showError()

}
