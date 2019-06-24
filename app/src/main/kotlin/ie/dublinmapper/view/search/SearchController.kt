package ie.dublinmapper.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.view.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.util.*
import ie.dublinmapper.view.livedata.LiveDataController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.view_search.view.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchController(args: Bundle) : MvpBaseController<SearchView, SearchPresenter>(args), SearchView {

    private lateinit var searchQueryView: EditText
    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var searchResults: RecyclerView
    private lateinit var searchHintDetail: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override val layoutId = R.layout.view_search

    private val subscriptions = CompositeDisposable()

    override fun createPresenter(): SearchPresenter {
        return getApplicationComponent().searchPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupLayout(view)
//        StatusBarUtil.setLightStatusBar(view, requireActivity())
        return view

    }

//    //TODO add test for keyboard showing
//    override fun onAttach(view: View) {
//        super.onAttach(view)
//        presenter.start("")
//    }

    @SuppressLint("RestrictedApi")
    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)
        if (changeType == ControllerChangeType.POP_EXIT) {
            hideKeyboard(searchQueryView)

//            val colorAnimation =
//                ValueAnimator.ofObject(ArgbEvaluator(), getColour(R.color.grey_400), getColour(R.color.primary_dark))
//            colorAnimation.duration = 600L
//            colorAnimation.start()
//
//            val window = requireActivity().window
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            colorAnimation.addUpdateListener { animator ->
//                val color = animator.animatedValue as Int
//
                // change status, navigation, and action bar color
//                window.statusBarColor = color
//            window.navigationBarColor = color
//            supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
//            }


//        searchQueryView.clearFocus()
//        val window = requireActivity().window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeEnded(changeHandler, changeType)
        if (changeType == ControllerChangeType.PUSH_ENTER) {
            searchQueryView.requestFocus()
//            val colorAnimation =
//                ValueAnimator.ofObject(ArgbEvaluator(), getColour(R.color.primary_dark), getColour(R.color.grey_400))
//            colorAnimation.duration = 500L
//            colorAnimation.start()
//
//            val window = requireActivity().window
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            colorAnimation.addUpdateListener { animator ->
//                val color = animator.animatedValue as Int
//
                // change status, navigation, and action bar color
//                window.statusBarColor = color
//            window.navigationBarColor = color
//            supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
//            }


//        searchQueryView.requestFocus()
//        val window = requireActivity().window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.grey_400)
        }
    }

    override fun onDetach(view: View) {
        presenter.stop()
        super.onDetach(view)
    }

    private fun setupLayout(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { router.handleBack() }
        searchHintDetail = view.searchHintDetail
        searchQueryView = view.findViewById(R.id.search_query)
        searchQueryView.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                showKeyboard(query)
            } else {
                hideKeyboard(query)
            }
        }
        val clearSearch: ImageView = view.findViewById(R.id.clear_search)

        adapter = GroupAdapter()
        searchResults = view.search_results
        searchResults.adapter = adapter
        searchResults.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        searchResults.layoutManager = layoutManager
        adapter.setOnItemClickListener { item, _ ->
            val extras = item.extras
            val serviceLocation = extras["serviceLocation"] as ServiceLocation
            val liveDataController = LiveDataController.Builder(
                serviceLocationId = serviceLocation.id,
                serviceLocationName = serviceLocation.name,
                serviceLocationService = serviceLocation.service,
                serviceLocationStyleId = getStyle(serviceLocation.service),
                serviceLocationIsFavourite = serviceLocation.isFavourite()
            ).build()
            router.pushController(
                RouterTransaction.with(liveDataController)
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler())
            )
        }

        subscriptions.add(
            Observable.create<String> { subscriber ->
                searchQueryView.addTextChangedListener(object : TextWatcher {

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        clearSearch.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
                        subscriber.onNext(s.toString())
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun afterTextChanged(s: Editable?) {}

                })
            }
            .debounce(400L, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .filter { it.length > 1 }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                presenter.start(it)
            }
            .subscribe()
        )

        searchQueryView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> hideKeyboard(searchQueryView)
            }
            true
        }
        clearSearch.setOnClickListener { searchQueryView.setText("") }
        swipeRefresh = view.swipeRefresh
    }

    // TODO this is bollocks
    private fun getStyle(service: Service): Int {
        return when (service) {
            Service.AIRCOACH -> R.style.AircoachTheme
            Service.BUS_EIREANN -> R.style.BusEireannTheme
            Service.DUBLIN_BIKES -> R.style.DublinBikesTheme
            Service.DUBLIN_BUS -> R.style.DublinBusTheme
            Service.IRISH_RAIL -> R.style.DartTheme
            Service.LUAS -> R.style.LuasTheme
            Service.SWORDS_EXPRESS -> TODO()
        }
    }

    override fun showLoading(isLoading: Boolean) {
        swipeRefresh.isRefreshing = isLoading
    }

    override fun showSearchResults(searchResults: Group) {
        if (searchResults.itemCount < 0) {
            this.searchResults.visibility = View.GONE
            searchHintDetail.visibility = View.VISIBLE
        } else {
            searchHintDetail.visibility = View.GONE
            this.searchResults.visibility = View.VISIBLE
        }
        adapter.clear()
        adapter.addAll(listOf(searchResults))
    }

    override fun showError() {

    }

    class Builder(
        serviceLocationStyleId: Int
    ) : MvpBaseController.Builder(serviceLocationStyleId) {

        fun build(): SearchController {
            return SearchController(buildArgs())
        }

    }

}
