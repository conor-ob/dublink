package ie.dublinmapper.service.irishrail

import dagger.Module
import dagger.Provides
import ie.dublinmapper.util.StringProvider
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class IrishRailModule {

    @Provides
    @Singleton
    fun jcdecauxApi(
        stringProvider: StringProvider,
        okHttpClient: OkHttpClient,
        @Named("xml") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): IrishRailApi {
        return Retrofit.Builder()
            .baseUrl(stringProvider.irishRailBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(IrishRailApi::class.java)
    }

}
