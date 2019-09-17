package com.wakemeuphere.internal

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object Utils {
    const val alarm_distance_min: Int = 10
    const val alarm_distance_max: Int = 10000

    fun distanceInRange(dist: Int) : Int {
        return max(min(dist, alarm_distance_max), alarm_distance_min)
    }

    //https://stackoverflow.com/questions/15319431/how-to-convert-a-latlng-and-a-radius-to-a-latlngbounds-in-android-google-maps-ap
    fun boundsByDistance(center: LatLng, distance: Int) : LatLngBounds {
        val distanceFromCenterToCorner = distance * sqrt(2.0)
        val southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0)
        val northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0)
        return LatLngBounds(southwestCorner, northeastCorner)
    }
}