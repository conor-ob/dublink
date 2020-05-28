package io.dublink.search

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.dublink.domain.service.RxScheduler
import io.dublink.test.getOrAwaitValue
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchViewModelTest {

    private val viewModel = SearchViewModel(
        searchUseCase = mockk {
            every { search(any()) } returns Observable.just(
                SearchResultsResponse.Data(
                    serviceLocations = emptyList(),
                    errorResponses = emptyList()
                )
            )
        },
        permissionChecker = mockk(),
        locationProvider = mockk(),
        preferenceStore = mockk(),
        scheduler = RxScheduler(
            io = Schedulers.trampoline(),
            ui = Schedulers.trampoline()
        )
    )

    @Test
    fun `search`() {
        // arrange
        val query = "Tara"

        // act
        viewModel.dispatch(Action.Search(query = query))

        // assert
        assertThat(viewModel.observableState.getOrAwaitValue()).isEqualTo(
            State(
                searchResults = null,
                loading = null,
                scrollToTop = null,
                throwable = null,
                nearbyLocations = null,
                recentSearches = null
            )
        )

//        assertThat(viewModel.observableState.getOrAwaitValue()).isEqualTo(
//            State(
//                searchResults = SearchResultsResponse.Data(
//                    serviceLocations = emptyList(),
//                    errorResponses = emptyList()
//                ),
//                loading = false,
//                scrollToTop = true,
//                throwable = null,
//                nearbyLocations = null,
//                recentSearches = null
//            )
//        )
    }
}
