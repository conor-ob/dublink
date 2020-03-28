package ie.dublinmapper.database

import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.AircoachStopLocalResource
import ie.dublinmapper.domain.datamodel.BusEireannStopLocalResource
import ie.dublinmapper.domain.datamodel.DublinBikesDockLocalResource
import ie.dublinmapper.domain.datamodel.DublinBusStopLocalResource
import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.datamodel.IrishRailStationLocalResource
import ie.dublinmapper.domain.datamodel.LuasStopLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.core.StringProvider
import java.time.Instant
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context, stringProvider: StringProvider): Database {
        return Database(
            driver = AndroidSqliteDriver(
                schema = Database.Schema,
                context = context,
                name = stringProvider.databaseName()
            ),
            aircoachStopServiceEntityAdapter = AircoachStopServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            busEireannStopServiceEntityAdapter = BusEireannStopServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            dublinBikesDockServiceEntityAdapter = DublinBikesDockServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            dublinBusStopServiceEntityAdapter = DublinBusStopServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            irishRailStationServiceEntityAdapter = IrishRailStationServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            luasStopServiceEntityAdapter = LuasStopServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            favouriteServiceLocationEntityAdapter = FavouriteServiceLocationEntity.Adapter(
                serviceAdapter = EnumColumnAdapter()
            ),
            serviceLocationRecordStateEntityAdapter = ServiceLocationRecordStateEntity.Adapter(
                lastUpdatedAdapter = object : ColumnAdapter<Instant, String> {

                    override fun decode(databaseValue: String): Instant {
                        return Instant.parse(databaseValue)
                    }

                    override fun encode(value: Instant): String {
                        return value.toString()
                    }

                }
            )
        )
    }

    @Provides
    @Singleton
    fun aircoachStopLocalResource(database: Database): AircoachStopLocalResource {
        return SqlDelightAircoachStopLocalResource(database)
    }

    @Provides
    @Singleton
    fun busEireannStopLocalResource(database: Database): BusEireannStopLocalResource {
        return SqlDelightBusEireannStopLocalResource(database)
    }

    @Provides
    @Singleton
    fun dublinBikesDockLocalResource(database: Database): DublinBikesDockLocalResource {
        return SqlDelightDublinBikesDockLocalResource(database)
    }

    @Provides
    @Singleton
    fun dublinBusStopLocalResource(database: Database): DublinBusStopLocalResource {
        return SqlDelightDublinBusStopLocalResource(database)
    }

    @Provides
    @Singleton
    fun irishRailStationLocalResource(database: Database): IrishRailStationLocalResource {
        return SqlDelightIrishRailStationLocalResource(database)
    }

    @Provides
    @Singleton
    fun luasStopLocalResource(database: Database): LuasStopLocalResource {
        return SqlDelightLuasStopLocalResource(database)
    }

    @Provides
    @Singleton
    fun favouriteCacheResource(database: Database): FavouriteServiceLocationLocalResource {
        return SqlDelightFavouriteServiceLocationLocalResource(database)
    }

    @Provides
    @Singleton
    fun serviceLocationRecordStateLocalResource(database: Database): ServiceLocationRecordStateLocalResource {
        return SqlDelightServiceLocationRecordStateLocalResource(database)
    }
}
