package ie.dublinmapper.service.di

import dagger.Module
import dagger.Provides
import ie.dublinmapper.service.aircoach.AircoachModule
import ie.dublinmapper.service.dublinbus.DublinBusModule
import ie.dublinmapper.service.github.GithubModule
import ie.dublinmapper.service.irishrail.IrishRailModule
import ie.dublinmapper.service.jcdecaux.JcDecauxModule
import ie.dublinmapper.service.rtpi.RtpiModule
import ie.dublinmapper.service.swordsexpress.SwordsExpressModule
import ie.dublinmapper.util.SslContextProvider
import ie.dublinmapper.util.StringProvider
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        AircoachModule::class,
        DublinBusModule::class,
        GithubModule::class,
        IrishRailModule::class,
        JcDecauxModule::class,
        RtpiModule::class,
        SwordsExpressModule::class
    ]
)
class ServiceModule {

    @Provides
    @Singleton
    @Named("DEFAULT")
    fun defaultOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            //.addInterceptor(downloadInterceptor)
            .addNetworkInterceptor(NetworkLoggingInterceptor())
            .retryOnConnectionFailure(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("AIRCOACH")
    fun okHttpClient(
        sslContextProvider: SslContextProvider,
        stringProvider: StringProvider
    ): OkHttpClient {
        return OkHttpClient.Builder()
//            .addInterceptor(downloadInterceptor)
            .hostnameVerifier { hostname, session ->
                return@hostnameVerifier hostname == stringProvider.aircoachHost()
                        && session.peerHost == stringProvider.aircoachHost()
                        && session.peerPort == stringProvider.aircoachPort().toInt()
            }
            .sslSocketFactory(sslContextProvider.sslContext().socketFactory)
            .addNetworkInterceptor(NetworkLoggingInterceptor())
            .retryOnConnectionFailure(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun callAdapterFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.create()
    }

    @Named("json")
    @Provides
    @Singleton
    fun jsonConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Named("xml")
    @Provides
    @Singleton
    fun xmlConverterFactory(): Converter.Factory {
        return SimpleXmlConverterFactory.create()
    }

}
