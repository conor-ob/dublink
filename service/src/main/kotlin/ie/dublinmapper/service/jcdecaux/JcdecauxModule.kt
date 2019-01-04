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
class JcdecauxModule(
//    private val baseUrl: String
) {

    @Provides
    @Singleton
    fun jcdecauxApi(
        okHttpClient: OkHttpClient,
        @Named("json") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): JcdecauxApi {
        return Retrofit.Builder()
            .baseUrl("https://api.jcdecaux.com/vls/v1/")
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(JcdecauxApi::class.java)
    }

}
