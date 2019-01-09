package ie.dublinmapper.view.search

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.util.Thread
import javax.inject.Inject

class SearchPresenterImpl @Inject constructor(
    private val thread: Thread
) : MvpBasePresenter<SearchView>(), SearchPresenter {

}
