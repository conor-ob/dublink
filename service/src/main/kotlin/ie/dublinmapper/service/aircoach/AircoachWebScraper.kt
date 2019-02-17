package ie.dublinmapper.service.aircoach

import io.reactivex.Single
import org.jsoup.Jsoup
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeObject
import javax.script.ScriptEngineManager

class AircoachWebScraper(
    private val aircoachBaseUrl: String
) : AircoachScraper {

    private val path = "stop-finder"
    private val varStopArray = "var stopArray"
    private val stopArrayPush = "stopArray.push"
    private val stopArray = "stopArray"

    override fun getStops(): Single<List<AircoachStopJson>> {
        val stops = mutableListOf<AircoachStopJson>()
        val document = Jsoup.connect("$aircoachBaseUrl$path").validateTLSCertificates(false).get()
        val scriptElements = document.getElementsByTag("script")
        for (element in scriptElements) {
            var javascript = element.data()
            if (javascript.contains(varStopArray)) {
                javascript = javascript.substring(javascript.indexOf(varStopArray))
                val index = javascript.lastIndexOf(stopArrayPush)
                val endIndex = javascript.indexOf(";", index)
                javascript = javascript.substring(0, endIndex)
                val factory = ScriptEngineManager()
                val engine = factory.getEngineByName("rhino")
                engine.eval(javascript)
                val stopArray = engine.get(stopArray) as NativeArray
                for (stopObject in stopArray) {
                    val stop = stopObject as NativeObject
                    val id = stop["id"] as String
                    val stopId = stop["stopId"] as String
                    val name = stop["name"] as String
                    val shortName = stop["shortName"] as String
                    val linkName = stop["linkName"] as String
                    val ticketName = stop["ticketName"] as String
                    val place = stop["place"] as String
                    val latitude = stop["stopLatitude"] as Double
                    val longitude = stop["stopLongitude"] as Double
                    val services = stop["services"] as NativeArray
                    val servicesJson = mutableListOf<AircoachStopServiceJson>()
                    for (serviceObject in services) {
                        val service = serviceObject as NativeObject
                        val route = service["route"] as String
                        val dir = service["dir"] as String
                        val serviceLinkName = service["linkName"] as String
                        servicesJson.add(
                            AircoachStopServiceJson(
                                route,
                                dir,
                                serviceLinkName
                            )
                        )
                    }
                    stops.add(
                        AircoachStopJson(
                            id,
                            stopId,
                            name,
                            shortName,
                            linkName,
                            ticketName,
                            place,
                            latitude,
                            longitude,
                            servicesJson
                        )
                    )
                }
            }
        }
        return Single.just(stops)
    }

}
