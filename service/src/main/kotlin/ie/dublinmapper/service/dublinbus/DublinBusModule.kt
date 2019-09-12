package ie.dublinmapper.service.dublinbus

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
class DublinBusModule {

    @Provides
    @Singleton
    fun dublinBusApi(
        stringProvider: StringProvider,
        @Named("DEFAULT") okHttpClient: OkHttpClient,
        @Named("xml") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): DublinBusApi {
        return Retrofit.Builder()
            .baseUrl(stringProvider.dublinBusBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(DublinBusApi::class.java)
    }

}
