package ie.dublinmapper.service.rtpi

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
class RtpiModule {

    @Provides
    @Singleton
    fun jcdecauxApi(
        stringProvider: StringProvider,
        okHttpClient: OkHttpClient,
        @Named("json") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): RtpiApi {
        return Retrofit.Builder()
            .baseUrl(stringProvider.rtpibaseUrl())
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(RtpiApi::class.java)
    }

}
