package ie.dublinmapper.permission

interface PermissionChecker {

    fun isLocationPermissionGranted(): Boolean

}
