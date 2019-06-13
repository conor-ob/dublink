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
import androidx.appcompat.view.ContextThemeWrapper
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
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.model.SpacerItem
import ie.dublinmapper.util.*
import ie.dublinmapper.view.livedata.LiveDataController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.view_search.view.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchController(args: Bundle) : MvpBaseController<SearchView, SearchPresenter>(args), SearchView {

    private lateinit var searchQueryView: EditText
    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var searchResults: RecyclerView
    private lateinit var searchHintDetail: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override val styleId = R.style.SearchTheme

    override val layoutId = R.layout.view_search

    override fun createPresenter(): SearchPresenter {
        return getApplicationComponent().searchPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val contextThemeWrapper = ContextThemeWrapper(requireActivity(), styleId)
        val themeInflater = inflater.cloneInContext(contextThemeWrapper)
        val view = super.onCreateView(themeInflater, container)
        setupLayout(view)
        StatusBarUtil.setLightStatusBar(view, requireActivity())
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
            val serviceLocation = extras["serviceLocation"] as ServiceLocationUi
            val liveDataController = LiveDataController.Builder(
                serviceLocationId = serviceLocation.id,
                serviceLocationName = serviceLocation.name,
                serviceLocationService = serviceLocation.service,
                serviceLocationStyleId = serviceLocation.styleId
            ).build()
            router.pushController(
                RouterTransaction.with(liveDataController)
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler())
            )
        }

        val subscription = Observable.create<String> { subscriber ->

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
                val thread = Thread.currentThread().name
                Timber.d("query ${Thread.currentThread().name}")
            }
            .subscribe()

        searchQueryView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> hideKeyboard(searchQueryView)
            }
            true
        }
        clearSearch.setOnClickListener { searchQueryView.setText("") }
        swipeRefresh = view.swipeRefresh
    }

    override fun showLoading(isLoading: Boolean) {
        swipeRefresh.isRefreshing = isLoading
    }

    override fun showSearchResults(searchResults: List<ServiceLocationUi>) {
        if (searchResults.isEmpty()) {
            this.searchResults.visibility = View.GONE
            searchHintDetail.visibility = View.VISIBLE
        } else {
            searchHintDetail.visibility = View.GONE
            this.searchResults.visibility = View.VISIBLE
        }
        val groups = mutableListOf<Group>()
        val serviceLocations = searchResults.groupBy { it.serviceLocation.service }
        for (entry in serviceLocations) {
            groups.add(DividerItem())
            groups.add(HeaderItem(entry.key.fullName))
            for (item in entry.value) {
                groups.add(item.toItem())
            }
        }
        groups.add(SpacerItem())
        adapter.update(groups)
    }

    override fun showError() {

    }

}
