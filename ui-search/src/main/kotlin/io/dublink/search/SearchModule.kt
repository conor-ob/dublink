package io.dublink.search

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SearchModule {

    @Provides
    @Singleton
    fun searchService(): SearchService = SearchService()
}
