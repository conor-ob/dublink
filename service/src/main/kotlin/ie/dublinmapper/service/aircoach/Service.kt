package ie.dublinmapper.service.aircoach

import com.google.gson.annotations.SerializedName

data class ServiceResponseJson(
    @SerializedName("services") val services: List<ServiceJson> = mutableListOf()
)

data class ServiceJson(
    @SerializedName("date") val date: String,
    @SerializedName("linkDate") val linkDate: String,
    @SerializedName("time") val time: TimeJson,
    @SerializedName("startTime") val startTime: TimestampJson,
    @SerializedName("route") val route: String,
    @SerializedName("dir") val dir: String,
    @SerializedName("colour") val colour: String,
    @SerializedName("depart") val depart: String,
    @SerializedName("arrival") val arrival: String,
    @SerializedName("journeyId") val journeyId: String,
    @SerializedName("linkName") val linkName: String,
    @SerializedName("stopType") val stopType: Int,
    @SerializedName("stops") val stops: List<String>,
    @SerializedName("places") val places: List<String>,
    @SerializedName("live") val live: LiveJson,
    @SerializedName("dups") val dups: String,
    @SerializedName("eta") val eta: EtaJson,
    @SerializedName("delayed") val delayed: Boolean
)

data class EtaJson(
    @SerializedName("etaArrive") val etaArrive: TimestampJson,
    @SerializedName("etaDepart") val etaDepart: TimestampJson,
    @SerializedName("etaLayover") val etaLayover: Int,
    @SerializedName("arrive") val arrive: TimestampJson,
    @SerializedName("depart") val depart: TimestampJson,
    @SerializedName("atStop") val atStop: Boolean,
    @SerializedName("late") val late: Boolean,
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: String
)

data class LiveJson(
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("vehicle") val vehicle: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("bearing") val bearing: Int,
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: TimestampJson,
    @SerializedName("timeZone") val timeZone: String,
    @SerializedName("geoLocation") val geoLocation: String,
    @SerializedName("gpsProvider") val gpsProvider: String
)

data class TimeJson(
    @SerializedName("arrive") val arrive: TimestampJson,
    @SerializedName("depart") val depart: TimestampJson,
    @SerializedName("layover") val layover: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("timeZone") val timeZone: String,
    @SerializedName("actualStatus") val actualStatus: Int,
    @SerializedName("duplicateCount") val duplicateCount: Int
)

data class TimestampJson(
    @SerializedName("dateTime") val dateTime: String,
    @SerializedName("hrs") val hrs: Int,
    @SerializedName("mins") val mins: Int
)
