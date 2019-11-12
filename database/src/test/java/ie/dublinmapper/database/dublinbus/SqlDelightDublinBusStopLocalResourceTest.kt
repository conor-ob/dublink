package ie.dublinmapper.database.dublinbus

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import ie.dublinmapper.database.*
import io.rtpi.api.Coordinate
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Operator
import org.junit.Test
import org.threeten.bp.Instant

class SqlDelightDublinBusStopLocalResourceTest {

    private val database = Database(
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            Database.Schema.create(this)
        },
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
    private val dublinBusStopLocalResource =
        SqlDelightDublinBusStopLocalResource(database)

    @Test
    fun `test dublin bus`() {


        dublinBusStopLocalResource.insertStops(
            listOf(
                DublinBusStop(
                    id = "315",
                    name = "test",
                    coordinate = Coordinate(latitude = 0.0, longitude = 0.0),
                    operators = setOf(Operator.DUBLIN_BUS, Operator.GO_AHEAD),
                    routes = mapOf(
                        Operator.DUBLIN_BUS to listOf("46A", "145", "184"),
                        Operator.GO_AHEAD to listOf("17", "75")
                    )
                )
            )
        )
    }

}
