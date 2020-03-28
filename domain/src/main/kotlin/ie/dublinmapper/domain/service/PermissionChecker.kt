package ie.dublinmapper.domain.service

interface PermissionChecker {

    fun isLocationPermissionGranted(): Boolean
}
