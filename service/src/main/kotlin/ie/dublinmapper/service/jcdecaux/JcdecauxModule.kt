package ie.dublinmapper.service.jcdecaux

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class JcdecauxModule {

    @Provides
    @Singleton
    fun jcdecauxApi(
        @Named("jcdecaux_base_url") baseUrl: String,
        okHttpClient: OkHttpClient,
        @Named("json") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): JcdecauxApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(JcdecauxApi::class.java)
    }

}
