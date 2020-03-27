package ie.dublinmapper.core

interface PermissionChecker {

    fun isLocationPermissionGranted(): Boolean
}
