package ie.dublinmapper.service.jcdecaux

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
class JcDecauxModule {

    @Provides
    @Singleton
    fun jcDecauxApi(
        stringProvider: StringProvider,
        okHttpClient: OkHttpClient,
        @Named("json") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): JcDecauxApi {
        return Retrofit.Builder()
            .baseUrl(stringProvider.jcdecauxBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(JcDecauxApi::class.java)
    }

}