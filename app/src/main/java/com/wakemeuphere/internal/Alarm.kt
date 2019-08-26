package com.wakemeuphere.internal

import com.beust.klaxon.Json
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class Alarm {

    var id: Int = 0
    var title: String = ""
    var minDistance: Int = 0
    var definitiveDistance: Int = 0
    var soundId: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    @Json(ignored = true)
    lateinit var marker: Marker

    fun setLatLng (position: LatLng) {
        longitude = position.longitude
        latitude = position.latitude
    }

    fun getLatLng () : LatLng {
        return LatLng(latitude, longitude)
    }
}