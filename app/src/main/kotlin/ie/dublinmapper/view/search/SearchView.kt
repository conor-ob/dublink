package ie.dublinmapper.view.search

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.xwray.groupie.Group

interface SearchView : MvpView {

    fun showLoading(isLoading: Boolean)

    fun showSearchResults(searchResults: Group)

    fun showError()

}
