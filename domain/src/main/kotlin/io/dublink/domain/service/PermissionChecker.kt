package io.dublink.domain.service

interface PermissionChecker {

    fun isLocationPermissionGranted(): Boolean
}
