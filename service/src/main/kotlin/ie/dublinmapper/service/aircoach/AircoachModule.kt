package ie.dublinmapper.service.aircoach

import dagger.Module
import dagger.Provides
import ie.dublinmapper.StringProvider
import ie.dublinmapper.service.github.GithubApi
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class AircoachModule {

    @Provides
    @Singleton
    fun aircoachResource(
        aircoachScraper: AircoachScraper,
        aircoachApi: AircoachApi,
        fallback: GithubApi
    ) : AircoachResource {
        return AircoachWebResource(
            aircoachScraper,
            aircoachApi,
            fallback
        )
    }

    @Provides
    @Singleton
    fun aircoachScraper(
        stringProvider: StringProvider
    ) : AircoachScraper {
        return AircoachWebScraper(stringProvider.aircoachBaseUrl())
    }

    @Provides
    @Singleton
    fun aircoachApi(
        stringProvider: StringProvider,
        @Named("AIRCOACH") okHttpClient: OkHttpClient,
        @Named("json") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): AircoachApi {
        return Retrofit.Builder()
            .baseUrl(stringProvider.aircoachBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(AircoachApi::class.java)
    }

}
