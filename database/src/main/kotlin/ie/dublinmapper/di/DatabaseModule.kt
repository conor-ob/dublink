package ie.dublinmapper.di

import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import ie.dublinmapper.database.*
import ie.dublinmapper.database.aircoach.SqlDelightAircoachStopLocalResource
import ie.dublinmapper.database.buseireann.SqlDelightBusEireannStopLocalResource
import ie.dublinmapper.database.dublinbikes.SqlDelightDublinBikesDockLocalResource
import ie.dublinmapper.database.dublinbus.SqlDelightDublinBusStopLocalResource
import ie.dublinmapper.database.irishrail.SqlDelightIrishRailStationLocalResource
import ie.dublinmapper.database.luas.SqlDelightLuasStopLocalResource
import ie.dublinmapper.database.persister.SqlDelightServiceLocationRecordStateLocalResource
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.buseireann.BusEireannStopLocalResource
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockLocalResource
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopLocalResource
import ie.dublinmapper.datamodel.favourite.FavouriteServiceLocationLocalResource
import ie.dublinmapper.datamodel.irishrail.IrishRailStationLocalResource
import ie.dublinmapper.datamodel.luas.LuasStopLocalResource
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.datamodel.persister.PersisterEntity
import ie.dublinmapper.datamodel.persister.ServiceLocationRecordStateLocalResource
import ie.dublinmapper.favourite.SqlDelightFavouriteServiceLocationLocalResource
import ie.dublinmapper.util.StringProvider
import io.reactivex.Maybe
import io.rtpi.api.Service
import org.threeten.bp.Instant
import javax.inject.Singleton

data class FavouriteKey(
    val serviceId: String,
    val service: Service
)

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

    @Provides
    @Singleton
    fun persister(): PersisterDao {
        return object : PersisterDao {

            override fun select(id: String): Maybe<PersisterEntity> {
                return Maybe.empty()
            }

            override fun selectAll(): Maybe<List<PersisterEntity>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun insert(entity: PersisterEntity): Long {
                return 0L
            }

            override fun insertAll(entities: List<PersisterEntity>): List<Long> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun update(entity: PersisterEntity) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun updateAll(entities: List<PersisterEntity>) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun delete(entity: PersisterEntity) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

}
