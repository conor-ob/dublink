package ie.dublinmapper.view.search

import com.hannesdorfmann.mosby3.mvp.MvpPresenter

interface SearchPresenter : MvpPresenter<SearchView> {

    fun start(query: String)

    fun stop()

}
