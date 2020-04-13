package ie.dublinmapper.database

// import androidx.room.Room
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import androidx.test.platform.app.InstrumentationRegistry
// import ie.dublinmapper.datamodel.test.LocationEntity
// import ie.dublinmapper.datamodel.test.ServiceEntity
// import ie.dublinmapper.datamodel.test.ServiceLocationCacheResource
// import ie.dublinmapper.datamodel.test.ServiceLocationEntity
// import ie.dublinmapper.test.ServiceLocationCacheResourceImpl
// import io.reactivex.observers.TestObserver
// import org.junit.Before
// import org.junit.Test
// import org.junit.runner.RunWith
//
// @RunWith(AndroidJUnit4::class)
// class ServiceLocationPersistenceTest {
//
//    lateinit var dublinMapperDatabase: DublinMapperDatabase
//    lateinit var serviceLocationCacheResource: ServiceLocationCacheResource
//
//    @Before
//    fun setup() {
//        dublinMapperDatabase = Room.databaseBuilder(
//            InstrumentationRegistry.getInstrumentation().context,
//            DublinMapperDatabase::class.java,
//            "dublinmappertest.db"
//        ).allowMainThreadQueries().build()
//
//        serviceLocationCacheResource = ServiceLocationCacheResourceImpl(
//            dublinMapperDatabase.serviceLocationDao(),
//            dublinMapperDatabase.locationDao(),
//            dublinMapperDatabase.serviceDao(),
//            DatabaseTxRunner(dublinMapperDatabase)
//        )
//
//        serviceLocationCacheResource.insertServiceLocations(mockDublinBusStops())
//        serviceLocationCacheResource.insertServiceLocations(mockDartStations())
//        serviceLocationCacheResource.insertServiceLocations(mockLuasStops())
//    }
//
//    private fun mockDublinBusStops(): Pair<List<LocationEntity>, List<ServiceEntity>> {
//        val locationEntities = mutableListOf<LocationEntity>()
//        val serviceEntities = mutableListOf<ServiceEntity>()
//        locationEntities.add(LocationEntity("1", "Stillorgan Road", 0.0, 0.0))
//        locationEntities.add(LocationEntity("2", "Rock Road", 0.0, 0.0))
//        locationEntities.add(LocationEntity("3", "Clarendon Street", 0.0, 0.0))
//        serviceEntities.add(ServiceEntity(locationId = "1", operator = "BAC", route = "46A"))
//        serviceEntities.add(ServiceEntity(locationId = "1", operator = "BAC", route = "145"))
//        serviceEntities.add(ServiceEntity(locationId = "2", operator = "BAC", route = "184"))
//        serviceEntities.add(ServiceEntity(locationId = "2", operator = "GAD", route = "17"))
//        serviceEntities.add(ServiceEntity(locationId = "2", operator = "GAD", route = "75"))
//        serviceEntities.add(ServiceEntity(locationId = "3", operator = "BAC", route = "114"))
//        serviceEntities.add(ServiceEntity(locationId = "3", operator = "GAD", route = "75A"))
//        return Pair(locationEntities, serviceEntities)
//    }
//
//    private fun mockDartStations(): Pair<List<LocationEntity>, List<ServiceEntity>> {
//        val locationEntities = mutableListOf<LocationEntity>()
//        val serviceEntities = mutableListOf<ServiceEntity>()
//        locationEntities.add(LocationEntity("4", "Lansdowne Road", 0.0, 0.0))
//        locationEntities.add(LocationEntity("5", "Blackrock", 0.0, 0.0))
//        serviceEntities.add(ServiceEntity(locationId = "4", operator = "DART", route = "DART"))
//        serviceEntities.add(ServiceEntity(locationId = "4", operator = "COMMUTER", route = "COMMUTER"))
//        serviceEntities.add(ServiceEntity(locationId = "5", operator = "DART", route = "DART"))
//        return Pair(locationEntities, serviceEntities)
//    }
//
//    private fun mockLuasStops(): Pair<List<LocationEntity>, List<ServiceEntity>> {
//        val locationEntities = mutableListOf<LocationEntity>()
//        val serviceEntities = mutableListOf<ServiceEntity>()
//        locationEntities.add(LocationEntity("6", "St Stephen's Green", 0.0, 0.0))
//        locationEntities.add(LocationEntity("7", "Trinity", 0.0, 0.0))
//        serviceEntities.add(ServiceEntity(locationId = "6", operator = "LUAS", route = "RED"))
//        serviceEntities.add(ServiceEntity(locationId = "7", operator = "LUAS", route = "GREEN"))
//        return Pair(locationEntities, serviceEntities)
//    }
//
//    @Test
//    fun testQuery() {
//        val testObserver = TestObserver<List<ServiceLocationEntity>>()
//        serviceLocationCacheResource.selectServiceLocations("LUAS").subscribe(testObserver)
//        testObserver.assertNoErrors()
//        val serviceLocationEntities = mutableListOf<ServiceLocationEntity>()
//
//        val serviceLocationEntity1 = ServiceLocationEntity(LocationEntity("6", "St Stephen's Green", 0.0, 0.0))
//        val serviceEntities1 = mutableListOf<ServiceEntity>()
//        serviceEntities1.add(ServiceEntity(locationId = "6", operator = "LUAS", route = "RED"))
//        serviceLocationEntity1.services = serviceEntities1
//        serviceLocationEntities.add(serviceLocationEntity1)
//
//        val serviceLocationEntity2 = ServiceLocationEntity(LocationEntity("7", "Trinity", 0.0, 0.0))
//        val serviceEntities2 = mutableListOf<ServiceEntity>()
//        serviceEntities2.add(ServiceEntity(locationId = "7", operator = "LUAS", route = "GREEN"))
//        serviceLocationEntity2.services = serviceEntities2
//        serviceLocationEntities.add(serviceLocationEntity2)
//
//        testObserver.assertResult(serviceLocationEntities)
//    }
//
// }
