package ie.dublinmapper.service.irishrail

import dagger.Module
import dagger.Provides
import ie.dublinmapper.util.StringProvider
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import javax.inject.Named
import javax.inject.Singleton

@Module
class MockIrishRailModule {

    @Provides
    @Singleton
    fun irishRailApi(
        stringProvider: StringProvider,
        okHttpClient: OkHttpClient,
        @Named("xml") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): IrishRailApi {
        return MockIrishRailApi()
    }

}
