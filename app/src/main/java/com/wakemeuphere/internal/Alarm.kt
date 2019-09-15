package com.wakemeuphere.internal

import com.beust.klaxon.Json
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.SphericalUtil
import kotlin.math.sqrt

class Alarm {

    var id: Int = 0
    var title: String = ""
    var distance: Int = 0
        set(value) { field = Utils.distanceInRange(value) }
    var soundId: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    @Json(ignored = true)
    lateinit var marker: Marker
    @Json(ignored = true)
    lateinit var circle: Circle

    @Json(ignored = true)
    var point: LatLng
    set(value) {
        longitude = value.longitude
        latitude = value.latitude
    }
    get() = LatLng(latitude, longitude)

    //https://stackoverflow.com/questions/15319431/how-to-convert-a-latlng-and-a-radius-to-a-latlngbounds-in-android-google-maps-ap
    @Json(ignored = true)
    val bounds: LatLngBounds
        get() {
            val distanceFromCenterToCorner = distance * sqrt(2.0)
            val center = point
            val southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0)
            val northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0)
            return LatLngBounds(southwestCorner, northeastCorner)
        }
}