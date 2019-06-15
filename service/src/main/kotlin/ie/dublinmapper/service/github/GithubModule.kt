package ie.dublinmapper.service.github

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
class GithubModule {

    @Provides
    @Singleton
    fun githubApi(
        stringProvider: StringProvider,
        @Named("DEFAULT") okHttpClient: OkHttpClient,
        @Named("json") converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): GithubApi {
        return Retrofit.Builder()
            .baseUrl(stringProvider.githubBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(GithubApi::class.java)
    }

}
